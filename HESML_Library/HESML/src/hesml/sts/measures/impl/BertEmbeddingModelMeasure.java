/*
 * Copyright (C) 2019 alicia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hesml.sts.measures.impl;

import hesml.measures.impl.MeasureFactory;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.parser.ParseException;

/**
 *  Read and evaluate BERT embedding pretrained models
 * @author alicia
 */

class BertEmbeddingModelMeasure extends SentenceSimilarityMeasure
{
    
    // Path to the model to evaluate.
    
    private final String m_modelDirPath;
    
    // WordProcesser object.
    
    private final IWordProcessing m_preprocesser;
    
    // Temporal files for getting the sentences and setting the embedding vectors.
    
    private final File m_tempFileSentences;
    private final File m_tempFileVectors;
    
    // List of embeddings.
    
    private ArrayList<ArrayList<double[]> > m_vectors;
    
    // Paths to the BERT directory.
    
    private final String m_BERTDir;
    
    // Python executable using the virtual environment.
    
    private final String m_PythonVenvDir;
    
    // Python script wrapper to extract the embeddings.
    
    private final String m_PythonScriptDir;
    
    /**
     * Constructor
     * @param strModelDirPath
     * @param preprocesser 
     */
    
    BertEmbeddingModelMeasure(
            String              modelDirPath,
            IWordProcessing     preprocesser,
            String              bertDir,
            String              pythonVenvDir,
            String              pythonScriptDir) throws InterruptedException,
            IOException, FileNotFoundException, ParseException
    {
        // We initialize main attributes
        
        m_preprocesser = preprocesser;
        m_modelDirPath = modelDirPath;
        m_BERTDir = bertDir;
        m_PythonScriptDir = pythonScriptDir;
        m_PythonVenvDir = pythonVenvDir;
        
        // Initialize the vectors
        
        m_vectors = new ArrayList<>();

        // Create the temporal files and remove (if exists) the preexisting temp files.
        
        m_tempFileSentences = createTempFile(bertDir + "tempSentences.txt");
        m_tempFileVectors = createTempFile(bertDir + "tempVecs.txt");
    }

    /**
     * This function returns the sentence similarity method implemented by the object.
     * @return 
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.BertEmbeddingModelMeasure);
    }
    
    /**
     * This function returns the family of the current sentence similarity method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.SentenceEmbedding);
    }
    
    /**
     * Get the similarity value between two vectors
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    private double getSimilarityValue(
            double[]    sentence1Vector,
            double[]    sentence2Vector) throws FileNotFoundException, IOException
    {
        // We initialize the output value
        
        double similarity = 0.0;
        
        // We check the validity of the word vectors. They could be null if
        // any word is not contained in the vocabulary of the embedding.
        
        if ((sentence1Vector != null) && (sentence2Vector != null))
        {
            // We compute the cosine similarity function (dot product)
            
            for (int i = 0; i < sentence1Vector.length; i++)
            {
                similarity += sentence1Vector[i] * sentence2Vector[i];
            }
            
            // We divide by the vector norms
            
            similarity /= (MeasureFactory.getVectorNorm(sentence1Vector)
                        * MeasureFactory.getVectorNorm(sentence2Vector));
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This method get two lists of sentences and calculate the similarity values.
     * 
     * @param lstSentences1
     * @param lstSentences2
     * @return
     * @throws IOException 
     */
    
    @Override
    public double[] getSimilarityValues(
            String[] lstSentences1, 
            String[] lstSentences2) throws IOException, InterruptedException
    {
        // We check that input vectors have the same length
        
        if(lstSentences1.length != lstSentences2.length)
            throw new IllegalArgumentException("The size of the input arrays are different!");
        
        // We initialize the output score vector
        
        double[] scores = new double[lstSentences1.length];
        
        // 1. Preprocess the sentences and write the sentences in a temporal file
        
        this.writeSentencesInTemporalFile(lstSentences1, lstSentences2);
        
        // 2. Read the vectors and write them in the temporal file for vectors
        
        this.executePythonWrapper();
        this.getVectorsFromTemporalFile();
        
        // We check the recovery of sentence vectors
        
        if(m_vectors.isEmpty())
        {
            String strError = "The vectors temporal file has not been loaded";
            throw new RuntimeException(strError);
        }
        
        // Calculate the scores
        
        /*for (int i = 0; i < m_vectors.size(); i++)
        {
            ArrayList<double[]> listSentences = m_vectors.get(i);
            double[] sentence1 = listSentences.get(0); 
            double[] sentence2 = listSentences.get(1); 
            scores[i] = this.getSimilarityValue(sentence1, sentence2);
        }*/
        
        // We traverse the collection of sentence pairs and compute
        // the similarity score for each pair.
        
        int i = 0;
        
        for (ArrayList<double[]> listSentences : m_vectors)
        {
            scores[i++] = this.getSimilarityValue(listSentences.get(0), listSentences.get(1));
        }
        
        // La liberaciónde recursos la debería hacer getVectorsFromTemporalFile()

        removeTempFile(m_tempFileVectors);
        removeTempFile(m_tempFileSentences);
        
        // We return the result
        
        return (scores);
    }
   
    /**
     * Preprocess and write the input sentences into the temporal file.
     * 
     * @param lstSentences1
     * @param lstSentences2
     * @throws FileNotFoundException
     * @throws IOException 
     */
    
    private void writeSentencesInTemporalFile(
            String[] lstSentences1,
            String[] lstSentences2) throws FileNotFoundException, IOException, InterruptedException
    {
        // We create the file to trasnfer the sentences to the BERT library
        
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(m_tempFileSentences));
        
        // We write all sentence pairs in the BERT file
        
        for (int i = 0; i < lstSentences1.length; i++) 
        {
            // Get the sentences string
            
            String strRawSentence1 = lstSentences1[i];
            String strRawSentence2 = lstSentences2[i];
            
            // Preprocess the sentences
            
            String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
            String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);

            String preprocessedSentence1 = String.join(" ", lstWordsSentence1);
            String preprocessedSentence2 = String.join(" ", lstWordsSentence2);

            // We separate both sentences by a TAB
            
            String line = preprocessedSentence1 + "\t" + preprocessedSentence2;
            
            outputWriter.write(line);
            outputWriter.newLine();
        }
        
        // We close the file
        
        outputWriter.close();
    }
    
    /**
     * After executing the wrapper for Python, get the vectors from the temporal file.
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    
    private void getVectorsFromTemporalFile() throws IOException, InterruptedException
    {
        // Read the vectors from the temporal file
        
        FileReader fileReader = new FileReader(m_tempFileVectors);
        BufferedReader reader = new BufferedReader(fileReader);
       
        // We retrieve each sentecne vector
        
        String line = reader.readLine();
        
        while (line != null)
        {
            // Get the vectors

            String[] sentenceVectors = line.split("\t");
            String[] vectorSentence1 = sentenceVectors[0].split(",");
            String[] vectorSentence2 = sentenceVectors[1].split(",");

            // map to doubles

            double[] embeddingSentence1 = Arrays.stream(vectorSentence1).mapToDouble(Double::parseDouble).toArray();
            double[] embeddingSentence2 = Arrays.stream(vectorSentence2).mapToDouble(Double::parseDouble).toArray();

            // We save together the vectors of both sentences

            ArrayList<double[]> vectorsLineI = new ArrayList<>();
            
            vectorsLineI.add(embeddingSentence1);
            vectorsLineI.add(embeddingSentence2);
            
            m_vectors.add(vectorsLineI);
            
            // We read the next line
            
            line = reader.readLine();
        }
        
        // We clsoe the file
        
        reader.close();
    }
    
    /**
     * Execute the wrapper for infer the vectors.
     * 
     * @throws InterruptedException
     * @throws IOException 
     */
    
    private void executePythonWrapper() throws InterruptedException, IOException
    {
        // Fill the command params and execute the script
        
        String python_command = m_PythonVenvDir + " " + m_PythonScriptDir;
        
        String absPathTempSentencesFile = m_tempFileSentences.getCanonicalPath();
        String absPathTempVectorsFile = m_tempFileVectors.getCanonicalPath();
        
        /*String command = python_command 
                .concat(" ")
                .concat(m_modelDirPath)
                .concat(" ")
                .concat(absPathTempSentencesFile)
                .concat(" ")
                .concat(absPathTempVectorsFile);*/
        
        String command = python_command + " " + m_modelDirPath + " "
                + absPathTempSentencesFile + " " + absPathTempVectorsFile;
        
//        System.out.print("Python command executed: ");
//        System.out.print(command);
        
        Process proc = Runtime.getRuntime().exec(command);

        //  NO SE DEBE DEJAR NUNCA CÓDIGO COMENTADO.
        // Read the output @todo: check if OK

//        BufferedReader readerTerminal = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//        String lineTerminal = "";
//        while((lineTerminal = readerTerminal.readLine()) != null) {
//            System.out.print(lineTerminal + "\n");
//        }
//        readerTerminal = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//        lineTerminal = "";
//        while((lineTerminal = readerTerminal.readLine()) != null) {
//            System.out.print(lineTerminal + "\n");
//        }
        
        // Destroy the process
        
        proc.waitFor();  
        proc.destroy();
    }
    
    /**
     * Create and remove temporal files.
     * 
     * @param strTempFilePath
     * @return
     * @throws IOException 
     */
    
    private static File createTempFile(
            String strTempFilePath) throws IOException
    {
        File tempFile = new File(strTempFilePath);
        if (tempFile.exists())
            tempFile.delete(); 
        tempFile.createNewFile();
        return tempFile;
    }
    
    // Esta función es innecesaria
    
    private static boolean removeTempFile(
            File file)
    {
        return file.delete();
    }
}
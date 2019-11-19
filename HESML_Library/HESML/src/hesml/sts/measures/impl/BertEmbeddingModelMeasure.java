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
 *  Read and evaluate BERT embedding pre-trained models
 * @author alicia
 */

class BertEmbeddingModelMeasure extends SentenceSimilarityMeasure
{
    // Path to the model to evaluate.
    
    private final String m_modelDirPath;
    
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
        // We intialize the base class
        
        super(preprocesser);
        
        // We initialize main attributes
        
        m_modelDirPath = modelDirPath;
        m_BERTDir = bertDir;
        m_PythonScriptDir = pythonScriptDir;
        m_PythonVenvDir = pythonVenvDir;
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
        {
            String strError = "The size of the input arrays are different!";
            throw new IllegalArgumentException(strError);    
        }

        // We initialize the output score vector
        
        double[] scores = new double[lstSentences1.length];
        
        // Initialize the temporal file for writing the sentences and read the vectors
        
        File tempFileSentences = createTempFile(m_BERTDir + "tempSentences.txt");
        File tempFileVectors   = createTempFile(m_BERTDir + "tempVecs.txt");
        
        // Get the canonical path for the temporal files
        
        String absPathTempSentencesFile = tempFileSentences.getCanonicalPath();
        String absPathTempVectorsFile   = tempFileVectors.getCanonicalPath();
        
        // 1. Preprocess the sentences and write the sentences in a temporal file
        
        this.writeSentencesInTemporalFile(tempFileSentences, lstSentences1, lstSentences2);

        // 2. Read the vectors and write them in the temporal file for vectors

        this.executePythonWrapper(absPathTempSentencesFile, absPathTempVectorsFile);
        
        // 3. We read the vectors from the temporal file
        
        ArrayList<ArrayList<double[]> > vectors = this.getVectorsFromTemporalFile(tempFileVectors);

        // We check the recovery of sentence vectors
        
        if(vectors.isEmpty())
        {
            String strError = "The vectors temporal file has not been loaded";
            throw new RuntimeException(strError);
        }
        
        // Remove the temporal files
        
//        tempFileSentences.delete();
//        tempFileVectors.delete();
        
        // We traverse the collection of sentence pairs and compute
        // the similarity score for each pair.
        
        int i = 0;
        
        for (ArrayList<double[]> listSentences : vectors)
        {
            scores[i++] = this.getSimilarityValue(listSentences.get(0), listSentences.get(1));
        }
       
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
            File        tempFileSentences,
            String[]    lstSentences1,
            String[]    lstSentences2) 
            throws FileNotFoundException, IOException, InterruptedException
    {
        // We create the file to trasnfer the sentences to the BERT library
        
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(tempFileSentences));
        
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
            
            // We write the line in the file
            
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
    
    private ArrayList<ArrayList<double[]> > getVectorsFromTemporalFile(
            File tempFileVectors) 
            throws IOException, InterruptedException
    {
        // We initialize the output
        
        ArrayList<ArrayList<double[]> > vectors = new ArrayList<>();
        
        FileReader fileReader = new FileReader(tempFileVectors);
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
            
            vectors.add(vectorsLineI);
            
            // We read the next line
            
            line = reader.readLine();
        }
        
        // We close the file
        
        reader.close();
        
        // We return the result
        
        return (vectors);
    }
    
    /**
     * Execute the wrapper for infer the vectors.
     * 
     * @throws InterruptedException
     * @throws IOException 
     */
    
    private void executePythonWrapper(
            String absPathTempSentencesFile,
            String absPathTempVectorsFile) throws InterruptedException, IOException
    {
        
        // Fill the command params and execute the script
        // Ignore the Tensorflow warnings
        
        String python_command = m_PythonVenvDir + " -W ignore " + m_PythonScriptDir;

        // We fill the command line for the Python call
        
        String command = python_command + " " + m_modelDirPath + " "
                + absPathTempSentencesFile + " " + absPathTempVectorsFile;
        
        System.out.print("Python command executed: \n");
        System.out.print(command);
        
        Process proc = Runtime.getRuntime().exec(command);

        // Read the Python script output 

        String lineTerminal = "";
        
        InputStreamReader inputStreamReader = new InputStreamReader(proc.getErrorStream());
        BufferedReader readerTerminal       = new BufferedReader(inputStreamReader);
        
        System.out.print("\n\n ----------------------------- \n");
        System.out.print("--- Python script output: --- \n");
        
        while((lineTerminal = readerTerminal.readLine()) != null) 
            System.out.print(lineTerminal + "\n");
        
        System.out.print("\n --- End Python script output: --- \n");
        System.out.print("--------------------------------- \n\n");
        
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
        // We create a temporal file, remove if previously exists.
        
        File tempFile = new File(strTempFilePath);
        
        if (tempFile.exists()) tempFile.delete(); 
        
        tempFile.createNewFile();
        
        // Return true if ok
        
        return (tempFile);
    }
}
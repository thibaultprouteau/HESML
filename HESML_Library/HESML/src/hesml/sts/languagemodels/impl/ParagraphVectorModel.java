/* 
 * Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
 *
 * This program is free software for non-commercial use:
 * you can redistribute it and/or modify it under the terms of the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * (CC BY-NC-SA 4.0) as published by the Creative Commons Corporation,
 * either version 4 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * section 5 of the CC BY-NC-SA 4.0 License for more details.
 *
 * You should have received a copy of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0) 
 * license along with this program. If not,
 * see <http://creativecommons.org/licenses/by-nc-sa/4.0/>.
 *
 */

package hesml.sts.languagemodels.impl;

import hesml.sts.languagemodels.LanguageModelMethod;
import hesml.sts.languagemodels.ILanguageModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.iter.NdIndexIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.deeplearning4j.models.embeddings.learning.SequenceLearningAlgorithm;
import org.deeplearning4j.models.embeddings.learning.impl.sequence.*;

/**
 *  This class implements the Paragraph vector model.
 * 
 * @author alicia
 */

class ParagraphVectorModel implements ILanguageModel
{
    // Declare the language model method used.
    
    private LanguageModelMethod m_trainingMethod;
    
    // Declare the ParagraphVectors object from Deepl4Java library.
    
    private ParagraphVectors m_paragraphVectors; 
    
    /**
     * Constructor by default
     */
    
    ParagraphVectorModel()
    {
        // Initialize the paragraph vectors
        
        m_paragraphVectors = null;
    }
    
    /**
     * Constructor with parameters
     * @param trainingMethod 
     */
    
    ParagraphVectorModel(
            LanguageModelMethod trainingMethod)
    {
        // Initialize the training method and the paragraph vectors.
        
        m_trainingMethod = trainingMethod;
        m_paragraphVectors = null;
    }
    
    /**
     * Get the training method
     * @return LanguageModelMethod
     */
    
    @Override
    public LanguageModelMethod getTrainingMethod()
    {
        return (m_trainingMethod);
    }

    /**
     * Execute the training method.
     * 
     * Using default configuration from BIOSSES2017
     * 
     * @param strTrainningInputDocumentPath
     * @param strTrainningOutputDocumentPath
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    
    @Override
    public void train(
            String strTrainningInputDocumentPath,
            String strTrainningOutputDocumentPath) 
            throws FileNotFoundException, IOException
    {
        // Read the file, initialize configuration as BIOSSES2017
        
        File file = new File(strTrainningInputDocumentPath);
        SentenceIterator iter = new BasicLineIterator(file);
        AbstractCache<VocabWord> cache = new AbstractCache<>();
        
        // Initialize the tokenizer by default (whitespace).
        // The sentences has been previously preprocessed in HESML.
        
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        
        // Set the training method
        
        SequenceLearningAlgorithm<VocabWord> learner = 
                m_trainingMethod == LanguageModelMethod.ParagraphVectorDM?new DM<>():new DBOW<>();

        // Configuration from BIOSSES2017
        
        LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = new ParagraphVectors.Builder()
                .minWordFrequency(1)
                .iterations(100)
                .epochs(1)
                .layerSize(50)
                .learningRate(0.02)
                .labelsSource(source)
                .windowSize(5)
                .iterate(iter)
                .trainWordVectors(true)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .sampling(0)
                .sequenceLearningAlgorithm(new DM<VocabWord>()) 
                .build();

        vec.fit();
        
        // Write the results
        
        File tempFile = new File(strTrainningOutputDocumentPath);
        WordVectorSerializer.writeParagraphVectors(vec, tempFile);
    }
    
    /**
     * This function loads the vectors from a model path.
     * 
     * Using default configuration from BIOSSES2017
     * 
     * Important: In the future, the output will differ.
     * 
     * @param strParagraphVectorModelPath
     * @return
     * @throws IOException 
     */
    
    @Override
    public ParagraphVectors loadVectors(
            String strParagraphVectorModelPath) throws IOException 
    {
        // Read the vectors model with default values as BIOSSES2017
        
        m_paragraphVectors = WordVectorSerializer.readParagraphVectors(strParagraphVectorModelPath);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        m_paragraphVectors.setTokenizerFactory(t);
        
        // please note, we set iterations to 1 here, just to speedup inference
        
        m_paragraphVectors.getConfiguration().setIterations(10);
        
        // Return the vectors
        
        return (m_paragraphVectors);
    }
    
    /**
     * This function returns the vector for a sentence.
     * @param strSentence
     * @return 
     */
    
    @Override
    public double[] getVectorFromStrSentence(
            String strSentence)
    {
        // Initialize an INDArray object (from DeepL4Java library)
        
        INDArray inferredVector = null;

        // Get the vectors and transforms to double[]
        
        inferredVector = m_paragraphVectors.inferVector(strSentence);
        double[] vector = transformINDArraytoDoubleArray(inferredVector);
        
        // Return the vector
        
        return (vector);
    }

    /**
     * This function set the training method.
     * @param method 
     */
    
    @Override
    public void setTrainingMethod(LanguageModelMethod method)
    {
        m_trainingMethod = method;
    }
    
    
    /**
     * This function transform an INDArray object to double array.
     * 
     * @param iNDArray
     * @return double[]
     */
    
    private double[] transformINDArraytoDoubleArray(
            INDArray iNDArray)
    {
        // In paragraph vector, the shape is (1, 50)
        
        int nRows = iNDArray.rows();
        int nCols = iNDArray.columns();
        
        // Initialize the vector
        
        double[] vector = new double[nCols];
        
        // Initialize the iterator
        
        NdIndexIterator iter = new NdIndexIterator(nRows, nCols);
        int i = 0;
        while (iter.hasNext()) {
            int[] nextIndex = iter.next();
            
            // Get the iterator value and add to the vector
            
            double nextVal = iNDArray.getDouble(nextIndex);
            vector[i] = nextVal;
            i++;
        }
        
        // Return the result
        
        return (vector);
    }
    
    /**
     * This function set to null the paragraph vectors and clean the memory.
     * 
     * Note: It does not use an specific clear() method cause 
     * the original DeepL4Java does not provide the implementation.
     */
    
    @Override
    public void unsetParagraphVectors()
    {
        m_paragraphVectors = null;
    }
}

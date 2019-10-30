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

import hesml.measures.IPretrainedWordEmbedding;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.String.format;
import static java.lang.String.join;
import java.util.concurrent.TimeUnit;

/**
 *  Read and evaluate BERT embedding pretrained models
 * @author alicia
 */

class BertEmbeddingModelMeasure implements ISentenceSimilarityMeasure
{
    
    private final String m_strModelDirPath;
    private IWordProcessing m_preprocesser;
    /**
     * Word emebedding model
     */
    
    private final IPretrainedWordEmbedding    m_WordEmbedding;
    
    /**
     * Constructor
     * @param strModelDirPath
     * @param preprocesser 
     */
    
    BertEmbeddingModelMeasure(
            String              strModelDirPath,
            IWordProcessing     preprocesser) throws InterruptedException
    {
        m_strModelDirPath = strModelDirPath;
        m_preprocesser = preprocesser;
        m_WordEmbedding = null;
    }

    /**
     * Get the actual method
     * @return 
     */
    @Override
    public SentenceSimilarityMethod getMethod(){return SentenceSimilarityMethod.BertEmbeddingModelMeasure;}

    /**
     * Get the similarity value between two sentences
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    @Override
    public double getSimilarityValue(String strRawSentence1, String strRawSentence2) throws IOException
    {
        double similarity = 0;
        
        
        
        return 0;
    }
    

    /**
     * This function returns the vector representation of the input sentence
     * which is based on a combination of the vectors corresponding to the words
     * in the sentence..
     * @param strRawSentence
     * @return
     * @throws IOException 
     */
    
    private double[] getSentenceEmbeddings(
        String  strRawSentence) throws IOException
    {
        // We obtain the words in the input sentence
        
        String[] strWords = m_preprocesser.getWordTokens(strRawSentence);        

        // We initialize the vector
        
        double[] sentenceVector = null;
        
 
        
        // We return the result
        
        return (sentenceVector);
    }
  
    
    
    
 
    
}

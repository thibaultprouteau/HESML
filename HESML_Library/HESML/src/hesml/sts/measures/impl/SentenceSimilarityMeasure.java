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

import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.IOException;

/**
 * This class implements the general methods for calculating measures scores.
 * It is the abstract base class for all sentence similarity measures.
 * @author alicia
 */

abstract class SentenceSimilarityMeasure implements ISentenceSimilarityMeasure
{
    // Word preprocessing object
    
    protected IWordProcessing m_preprocesser;
    
    /**
     * Constructor with parameters.
     * @param preprocesser 
     */
    
    SentenceSimilarityMeasure(
            IWordProcessing preprocesser)
    {
        m_preprocesser = preprocesser;
    }
    
    /**
     * This function releases all resources used by the measure. Once this
     * function is called the measure is completely disabled.
     */
    
    @Override
    public void clear()
    {       
        m_preprocesser.clear();
    }
    
    /**
     * This function returns the current method.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * This function returns the current family of STS method
     * @return SentenceSimilarityFamily
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    /**
     * Get the similarity value of two sentences.
     * Each measure implements its own method.
     * BERTEmbeddingModelMeasure does not implement this method.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    @Override
    public double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Get the similarity value between a list of pairs of sentences.
     * 
     * @param firstSentencesVector
     * @param secondSentencesVector
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    
    @Override
    public double[] getSimilarityValues(
            String[] firstSentencesVector,
            String[] secondSentencesVector) 
                throws IOException, InterruptedException
    {
        // We check that the length of the lists has to be equal
        
        if(firstSentencesVector.length != secondSentencesVector.length)
        {
            String strerror = "The size of the input arrays are different!";
            throw new IllegalArgumentException(strerror);
        }
        
        // Initialize the scores
        
        double[] similarityScores = new double[firstSentencesVector.length];
        
        // Iterate the sentences and get the similarity scores.
        
        for (int i = 0; i < firstSentencesVector.length; i++)
        {
            similarityScores[i] = this.getSimilarityValue(firstSentencesVector[i], secondSentencesVector[i]);
        }
        
        // Return the result
        
        return (similarityScores);
    }
}
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
import java.io.IOException;

/**
 * This class implements the general methods for calculating measures scores.
 * It is the abstract base class for all sentence similarity measures.
 * @author alicia
 */

abstract class SentenceSimilarityMeasure implements ISentenceSimilarityMeasure
{

    /**
     * Get the current method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Get the current family of STS method
     * @return 
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            String strRawSentence2) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Get the similarity value between a list of pairs of sentences.
     * 
     * @param lstSentences1
     * @param lstSentences2
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    
    @Override
    public double[] getSimilarityValues(
            String[] lstSentences1, 
            String[] lstSentences2) throws IOException, InterruptedException
    {
        // Initialize the scores
        
        double[] scores = new double[lstSentences1.length];
        
        if(lstSentences1.length != lstSentences2.length)
            throw new IllegalArgumentException("The size of the input arrays are different!");
        
        // Iterate the sentences and get the similarity scores.
        
        for (int i = 0; i < lstSentences1.length; i++)
        {
            String sentence1 = lstSentences1[i];
            String sentence2 = lstSentences2[i];
            scores[i] = this.getSimilarityValue(sentence1, sentence2);
        }
        
        return scores;
    }
}

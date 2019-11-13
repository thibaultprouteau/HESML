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

import hesml.sts.measures.IStringBasedSentenceSimMeasure;
import java.io.IOException;

/**
 *  This abstract class implements the general methods 
 * for string-based similarity measures.
 * @author alicia
 */

abstract class StringBasedSentenceSimMeasure implements IStringBasedSentenceSimMeasure
{

    @Override
    public double[] getSimilarityValues(String[] lstSentences1, String[] lstSentences2) throws IOException, InterruptedException
    {
        // Initialize the scores
        
        double[] scores = new double[lstSentences1.length];
        
        // The length of the lists has to be equal
        
        if(lstSentences1.length != lstSentences2.length)
        {
            String strerror = "The size of the input arrays are different!";
            throw new IllegalArgumentException(strerror);
        }

        // Iterate the sentences and get the similarity scores.
        
        for (int i = 0; i < lstSentences1.length; i++)
        {
            String sentence1 = lstSentences1[i];
            String sentence2 = lstSentences2[i];
            scores[i] = this.getSimilarityValue(sentence1, sentence2);
        }
        
        // Return the result
        
        return scores;
    }
}

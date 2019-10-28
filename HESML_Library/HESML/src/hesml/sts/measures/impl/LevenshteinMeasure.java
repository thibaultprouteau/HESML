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
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  This class implements the Levenshtein Measure
 * @author alicia
 */
class LevenshteinMeasure implements ISentenceSimilarityMeasure
{
    private final float insertDelete;
    private final float substitute;
    private float maxCost;
    /**
     * Word preprocesser used to convert the sentence into a string
     * of words.
     */
    
    private final IWordProcessing  m_Preprocesser;
    
    /**
     * Constructor
     * @param preprocesser 
     */
    
    public LevenshteinMeasure(
            IWordProcessing preprocesser)
    {
        m_Preprocesser = preprocesser;
        
        // Initialize internal variables 
        
        insertDelete = 1;
        substitute = 1;
        maxCost = 1;
    }
    
    
    @Override
    public SentenceSimilarityMethod getMethod(){return SentenceSimilarityMethod.Levenshtein;}

    @Override
    public double getSimilarityValue(String strRawSentence1, String strRawSentence2) throws IOException
    {
        // We initialize the output

        double similarity = 0.0;
        float distance = 0;
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_Preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_Preprocesser.getWordTokens(strRawSentence2);
        
        String sentence1 = String.join(" ", lstWordsSentence1);
        String sentence2 = String.join(" ", lstWordsSentence2);
        
        if (lstWordsSentence1.length == 0 && lstWordsSentence2.length == 0) {return 1.0f;}
        
        distance = distance(sentence1, sentence2);

        this.maxCost = max(insertDelete, substitute);
	similarity = 1.0f - (distance / (this.maxCost * max(sentence1.length(), sentence2.length())));
        
        return similarity;
    }
    
    /**
     *  ** Original code extracted from BIOSSES2017 implementation **
     * 
     *  This function calculates the distance between two strings 
     * using the Levenshtein distance.
     * @param s
     * @param t
     * @return
     */
    
    private float distance(final String s, final String t) {

        if (s.isEmpty())
                return t.length();
        if (t.isEmpty())
                return s.length();
        if (s.equals(t))
                return 0;

        final int tLength = t.length();
        final int sLength = s.length();

        float[] swap;
        float[] v0 = new float[tLength + 1];
        float[] v1 = new float[tLength + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        
        for (int i = 0; i < v0.length; i++) {
                v0[i] = i * this.insertDelete;
        }

        for (int i = 0; i < sLength; i++) {

                // first element of v1 is A[i+1][0]
                // edit distance is delete (i+1) chars from s to match empty t
                
                v1[0] = (i + 1) * this.insertDelete;

                for (int j = 0; j < tLength; j++) {
                        v1[j + 1] = min(v1[j] + this.insertDelete,
                                        v0[j + 1] + this.insertDelete,
                                        v0[j]
                                                        + (s.charAt(i) == t.charAt(j) ? 0.0f
                                                                        : this.substitute));
                }

                swap = v0;
                v0 = v1;
                v1 = swap;
        }

        // latest results was in v1 which was swapped with v0
        return v0[tLength];
    }
    
    /**
     * Get the minimum value from 3 floats
     * @param a
     * @param b
     * @param c
     * @return 
     */
    
    private static float min(float a, float b, float c) {
        return java.lang.Math.min(java.lang.Math.min(a, b), c);
    }
    
    /**
     * Get the maximum value from 2 floats
     * @param a
     * @param b
     * @return 
     */
    
    private static float max(float a, float b) {
        return java.lang.Math.max(a, b);
    }

}

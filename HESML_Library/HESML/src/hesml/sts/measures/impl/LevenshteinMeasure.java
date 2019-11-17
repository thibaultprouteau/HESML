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
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.measures.StringBasedSentSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *  This class implements the Levenshtein Measure.
 * 
 *  * In this implementation, the cost of insert and deletions are 1 by default.
 * 
 *  Levenshtein, Vladimir I. 1966. “Binary Codes Capable of 
 *  Correcting Deletions, Insertions, and Reversals.” 
 *  In Soviet Physics Doklady, 10:707–10. nymity.ch.
 * 
 * @author alicia
 */

class LevenshteinMeasure extends SentenceSimilarityMeasure implements IStringBasedSentenceSimMeasure
{
    
    // Internal variables used in the method by the original code (BIOSSES2017).
    // @param insertDelete: positive non-zero cost of an insert or deletion operation
    // @param substitute: positive cost of a substitute operation
    // @param maxCost: max(insertDelete, substitute)
    
    private final float insertDelete;
    private final float substitute;
    private float maxCost;
    
    /**
     * Constructor
     * @param preprocesser 
     */
    
    public LevenshteinMeasure(
            IWordProcessing preprocesser)
    {
        super(preprocesser);
        
        // Initialize the default internal variables
        // The cost of inserts and subtitutions are 1 (equal costs)
        
        this.insertDelete = 1;
        this.substitute = 1;
        this.maxCost = max(this.insertDelete, this.substitute);
    }
    
    /**
     * Get the method used 
     * @return 
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return SentenceSimilarityMethod.Levenshtein;
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.String);
    }
    
    /**
     * Return the String method
     * @return 
     */
    
    @Override
    public StringBasedSentSimilarityMethod getStringBasedMethodType()
    {
        return (StringBasedSentSimilarityMethod.Levenshtein);
    }
    
    /**
     * Get the similarity value for the Levenshtein distance.
     * 
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    @Override
    public double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) 
            throws IOException, FileNotFoundException, FileNotFoundException, InterruptedException
    {
        // We initialize the output

        double similarity = 0.0;
        double distance = 0.0;
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // Join the words and get a preprocessed sentence
        
        String sentence1 = String.join(" ", lstWordsSentence1);
        String sentence2 = String.join(" ", lstWordsSentence2);
        
        // The similarity is 1 if the two lists are empty
        
        if (lstWordsSentence1.length == 0 && lstWordsSentence2.length == 0) {return 1.0f;}
        
        // Calculate the distance between the sentences
        
        distance = distance(sentence1, sentence2);

        // The max cost is the maximum number between the costs of insert and substitution
        // By default is 1
        
        this.maxCost = max(this.insertDelete, this.substitute);
        
        // Calculate the similarity
        
	similarity = 1.0 - (distance / (this.maxCost * max(sentence1.length(), sentence2.length())));
        
        // Return the result
        
        return (similarity);
    }
    
    /**
     *  ** Original code extracted from BIOSSES2017 implementation **
     * 
     *  This function calculates the distance between two strings 
     * using the Levenshtein distance.
     * 
     * @param s
     * @param t
     * @return
     */
    
    private float distance(
            final String strSentence1, 
            final String strSentence2) {

        // if there is an empty sentence, the total cost will be the other sentence lenght.
        
        if (strSentence1.isEmpty())
                return strSentence2.length();
        if (strSentence2.isEmpty())
                return strSentence1.length();
        
        // If the sentences are equal, there is no cost and the distance will be zero.
        
        if (strSentence1.equals(strSentence2))
                return 0;

        final int sentence1Length = strSentence1.length(); 
        final int sentence2Length = strSentence2.length();
 
        // Initialize the vectors cost
        
        float[] swap;
        float[] v0 = new float[sentence2Length + 1];
        float[] v1 = new float[sentence2Length + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        
        for (int i = 0; i < v0.length; i++) 
        {
            
                v0[i] = i * this.insertDelete;
        }

        for (int i = 0; i < sentence1Length; i++) 
        {
            
                // first element of v1 is A[i+1][0]
                // edit distance is delete (i+1) chars from s to match empty t
                
                v1[0] = (i + 1) * this.insertDelete;

                for (int j = 0; j < sentence2Length; j++) 
                {
                    v1[j + 1] = min(v1[j] + this.insertDelete,
                                v0[j + 1] + this.insertDelete,
                                v0[j] + (strSentence1.charAt(i) == strSentence2.charAt(j) ? 0.0f: this.substitute));
                }

                swap = v0;
                v0 = v1;
                v1 = swap;
        }

        // latest results was in v1 which was swapped with v0
        
        return v0[sentence2Length];
    }
    
    /**
     * Get the minimum value from 3 floats
     * @param a
     * @param b
     * @param c
     * @return 
     */
    
    private static float min(
            float a, 
            float b, 
            float c) 
    {
        return java.lang.Math.min(java.lang.Math.min(a, b), c);
    }
    
    /**
     * Get the maximum value from 2 floats
     * @param a
     * @param b
     * @return 
     */
    
    private static float max(
            float a, 
            float b) 
    {
        return java.lang.Math.max(a, b);
    }
}

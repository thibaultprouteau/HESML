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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  This function implements the Qgram similarity between two sentences 
 * 
 * @author alicia
 */

class QgramMeasure extends SentenceSimilarityMeasure
{

    // Word preprocesser used to convert the sentence into a string of words.
    
    private final IWordProcessing  m_Preprocesser;
    
    /**
     * Constructor
     * @param preprocesser 
     */
    
    public QgramMeasure(
            IWordProcessing preprocesser)
    {
        m_Preprocesser = preprocesser;
    }

    
    /**
     * This function returns the type of method implemented by the current
     * sentence similarity measure.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod(){return SentenceSimilarityMethod.Qgram;}

    /**
     * This function returns the similarity value (score) between two
     * raw sentences using the Qgram similarity.
     * 
     * The implementation is based on the BIOSSES2017 library 
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return similarity score
     * @throws java.io.IOException
     */
    
    @Override
    public double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) throws IOException 
    {
        // We initialize the output

        double similarity = 0.0;
        double distance = 0.0;
        
        int padding = 3; // The default padding in the original code (BIOSSES2017) is 3 
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_Preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_Preprocesser.getWordTokens(strRawSentence2);

        // Get the basic results
        
        if (Arrays.equals(lstWordsSentence1, lstWordsSentence2)) {return 0.0f;}
        if (lstWordsSentence1.length == 0 && lstWordsSentence2.length == 0) {return 1.0f;}
        if (lstWordsSentence1.length == 0 || lstWordsSentence2.length == 0) {return 0.0f;}
        
        // Create the maps for the ngrams and get the total values for each map.
        
        Map<String, Integer> mapQgramsS1 = qGramsWithPadding(lstWordsSentence1, padding);
        Map<String, Integer> mapQgramsS2 = qGramsWithPadding(lstWordsSentence2, padding);
        int totalQgramsS1 = mapQgramsS1.values().stream().mapToInt(Integer::intValue).sum();
        int totalQgramsS2 = mapQgramsS2.values().stream().mapToInt(Integer::intValue).sum();
    
        // Get the total distance - for each ngram, get the abs(v1 - v2), 
        // v1 is the frequency of the trigram in the first sentence
        // v2 is the frequency of the trigram in the second sentence
        
        Set<String> union = new HashSet<>();
        union.addAll(mapQgramsS1.keySet());
        union.addAll(mapQgramsS2.keySet());

        for (String key : union) 
        {
            int v1 = 0;
            int v2 = 0;
            Integer iv1 = mapQgramsS1.get(key);
            if (iv1 != null) 
            {
                v1 = iv1;
            }
            Integer iv2 = mapQgramsS2.get(key);
            if (iv2 != null) 
            {
                v2 = iv2;
            }
            distance += Math.abs(v1 - v2);
        }

        // The similarity is calculated as in BIOSSES2017 implementation.
        
        similarity = 1.0f - distance / (totalQgramsS1 + totalQgramsS2);
        
        return similarity;

    }
    
    /**
     * Get the qgrams with a defined padding o window.
     * For example, the input ["hello", "world"] will generate:
     * [[hell, 1], [ell, 1], [low, 1], ...]
     * @param strings
     * @return 
     */
    
    private Map<String, Integer> qGramsWithPadding(
            String[]    strings, 
            int         padding) 
    {
        HashMap<String, Integer> mapQgrams = new HashMap<>();

        String strSentence = "##" + String.join(" ", strings) + "##";

        for (int i = 0; i < (strSentence.length() - padding + 1); i++) 
        {
            String strSubstring = strSentence.substring(i, i + padding);
            Integer aux = mapQgrams.get(strSubstring);
            if (aux != null) 
            {
                mapQgrams.put(strSubstring, aux + 1);
            } 
            else 
            {
                mapQgrams.put(strSubstring, 1);
            }
        }
        return mapQgrams;
    }

}
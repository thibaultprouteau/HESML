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
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
  *  This function implements the Qgram similarity between two sentences.
  * 
  *     Ukkonen, Esko. 1992. “Approximate String-Matching with Q-Grams 
  *     and Maximal Matches.” Theoretical Computer Science 92 (1): 191–211.
  * 
  * @author alicia
  */

class QgramMeasure extends SentenceSimilarityMeasure 
                   implements IStringBasedSentenceSimMeasure
{
    // Padding parameter used by the measure
    // The padding defines the character window used by the method.
    
    private final int m_padding;
    
    /**
     * Constructor
     * @param preprocesser 
     */
    
    QgramMeasure(
            IWordProcessing preprocesser,
            int padding)
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // We set the default padding value
        
        m_padding = padding;
    }
       
    /**
     * This function returns the type of method implemented by the current
     * sentence similarity measure.
     * 
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.Qgram);
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return SentenceSimilarityFamily
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.SentenceEmbedding);
    }
    
    /**
     * This function returns the String method implemented by the current
     * sentence similarity measure.
     * @return StringBasedSentenceSimilarityMethod
     */
    
    @Override
    public StringBasedSentenceSimilarityMethod getStringBasedMethodType()
    {
        return (StringBasedSentenceSimilarityMethod.Qgram);
    }
    
    /**
     * This function returns the similarity value (score) between two
     * raw sentences using the Qgram similarity.
     * 
     * The string-distance measure is based on counting the number of 
     * the occurrences of different q-grams in the two strings; 
     * the strings are the closer relatives the more they have q-grams in common. 
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return similarity score
     * @throws java.io.IOException, FileNotFoundException, InterruptedException 
     */
    
    @Override
    public double getSimilarityValue(
            String  strRawSentence1, 
            String  strRawSentence2) 
            throws IOException, FileNotFoundException, InterruptedException 
    {
        // We initialize the output

        double similarity = 0.0;
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);

        // Get the basic results
        
        if (Arrays.equals(lstWordsSentence1, lstWordsSentence2))
        {
            similarity = 0.0;
        }
        else if ((lstWordsSentence1.length == 0) && (lstWordsSentence2.length == 0))
        {
            similarity = 1.0;
        }
        else if ((lstWordsSentence1.length == 0) || (lstWordsSentence2.length == 0))
        {
            similarity = 0.0;
        }
        else
        {
            // Create the maps for the ngrams 

            Map<String, Integer> mapQgramsSentence1 = getQGramsWithPadding(lstWordsSentence1, m_padding);
            Map<String, Integer> mapQgramsSentence2 = getQGramsWithPadding(lstWordsSentence2, m_padding);

            // We get the total values for each map.

            int totalQgramsS1 = mapQgramsSentence1.values().stream().mapToInt(Integer::intValue).sum();
            int totalQgramsS2 = mapQgramsSentence2.values().stream().mapToInt(Integer::intValue).sum();

            // We build a globat set with all q-grams
            
            Set<String> union = new HashSet<>();

            union.addAll(mapQgramsSentence1.keySet());
            union.addAll(mapQgramsSentence2.keySet());

            // We initialize the accummulated distance
            
            double distance = 0.0;
            
            for (String key : union) 
            {
                // We get the frequency for each q-gram in each sentence
                
                int qgramFreqInSentence1 = !mapQgramsSentence1.containsKey(key) ? 0 : mapQgramsSentence1.get(key);
                int qgramFreqInSentence2 = !mapQgramsSentence2.containsKey(key) ? 0 : mapQgramsSentence2.get(key);

                // We compute the frequency difference for the q-gram
                
                distance += Math.abs(qgramFreqInSentence1 - qgramFreqInSentence2);
            }

            // The similarity is calculated as in BIOSSES2017 implementation.

            similarity = 1.0 - distance / (totalQgramsS1 + totalQgramsS2);
            
            // We release the mappins
            
            mapQgramsSentence1.clear();
            mapQgramsSentence2.clear();
            union.clear();
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * Get the qgrams with a defined padding o window.
     * For example, the input ["hello", "world"] will generate:
     * [[hell, 1], [ell, 1], [low, 1], ...]
     * @param strings
     * @return 
     */
    
    private Map<String, Integer> getQGramsWithPadding(
            String[]    strings, 
            int         padding) 
    {
        // Initialize a hashmap with the qgram
        
        HashMap<String, Integer> mapQgrams = new HashMap<>();

        // Add a padding "##" at the start and the end of the sentence
        
        String strSentence = "##" + String.join(" ", strings) + "##";

        // Get all the substrings of the sentence and add to the map counting the frequency
        
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
        
        // Return the map of Qgrams
        
        return mapQgrams;
    }
}
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  This class implements the Block Distance Measure.
 *  Krause, Eugene F. 1986. Taxicab Geometry: 
 *  An Adventure in Non-Euclidean Geometry. Courier Corporation.
 * @author alicia
 */

class BlockDistanceMeasure extends SentenceSimilarityMeasure implements IStringBasedSentenceSimMeasure
{
    
    /**
     * Constructor with parameters
     * @param preprocesser 
     */
    
    public BlockDistanceMeasure(
            IWordProcessing preprocesser)
    {
        super(preprocesser);
    }
    
    /**
     * This function returns the current similarity method
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return SentenceSimilarityMethod.BlockDistance;
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return SentenceSimilarityFamily
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
        return (StringBasedSentSimilarityMethod.BlockDistance);
    }
    
    /**
     * This function calculates the similarity value between two sentences.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InterruptedException 
     */
    
    @Override
    public double getSimilarityValue(
            String  strRawSentence1, 
            String  strRawSentence2) 
            throws IOException, FileNotFoundException, InterruptedException
    {
        // We initialize the output

        double similarity = 0.0;
        float distance = 0;
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // Hashmap to store the frequency that an element occurs in each sentence
        
        Map<String, Integer> mapOccurrencesSentence1 = getStringOccurrences(lstWordsSentence1);
        Map<String, Integer> mapOccurrencesSentence2 = getStringOccurrences(lstWordsSentence2);
        
        // Count the total of items in each sentence (sum of the map values)
        
        int totalWordsS1 = mapOccurrencesSentence1.values().stream().mapToInt(Integer::intValue).sum();
        int totalWordsS2 = mapOccurrencesSentence2.values().stream().mapToInt(Integer::intValue).sum();
       
        //Calculate the distance
        
        // Get the union set of both sentences (a dictionary)
                
        Set<String> union = new HashSet<>();
        
        union.addAll(mapOccurrencesSentence1.keySet());
        union.addAll(mapOccurrencesSentence2.keySet());
        
        // for each word get the abs(v1 - v2) being:
        // v1 the number of times the actual word occurs in the sentence 1
        // v2 the number of times the actual word occurs in the sentence 2
        
        for (String word : union) 
        {
            int v1 = 0;
            int v2 = 0;
            
            Integer iv1 = mapOccurrencesSentence1.get(word);
            if (iv1 != null) 
            {
                v1 = iv1;
            }
            
            Integer iv2 = mapOccurrencesSentence2.get(word);
            if (iv2 != null) 
            {
                v2 = iv2;
            }
            
            distance += Math.abs(v1 - v2);
        }

        // The similarity is calculated as in BIOSSES2017 implementation.
        
        similarity = 1.0f - distance / (totalWordsS1 + totalWordsS2);
        
        // Return the similarity
        
        return (similarity);
    }
    
    /**
     * Get a HashMap with the number of occurrences for each word in the sentence.
     * 
     * For example: "this words this ..."
     * 
     * {{"this", 2}, {"word", 1}, ...}
     * 
     * @param lstWordsSentence
     * @return Map<String, Integer> string occurrences.
     */
    
    private Map<String, Integer> getStringOccurrences(
            String[]    lstWordsSentence)
    {
        // Initialize the output
        
        Map<String, Integer> occurrences = new HashMap<>(); 
  
        // For each word in the sentence, 
        // count the time the word appears in the sentence and store it.
        
        for (String i : lstWordsSentence) { 
            Integer j = occurrences.get(i); 
            occurrences.put(i, (j == null) ? 1 : j + 1); 
        } 
        
        // Return the result
        
        return (occurrences);
    }
}

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

import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.measures.StringBasedSentSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  This function implements the Jaccard similarity between two sentences 
 * 
 * @author alicia
 */

class JaccardMeasure extends StringBasedSentenceSimMeasure
{
    /**
     * Word preprocesser used to convert the sentence into a string
     * of words.
     */
    
    private final IWordProcessing  m_Preprocesser;
    
    /**
     * Constructor
     * @param preprocesser 
     */
    
    public JaccardMeasure(
            IWordProcessing preprocesser)
    {
        m_Preprocesser = preprocesser;
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
     * This function returns the type of method implemented by the current
     * sentence similarity measure.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return SentenceSimilarityMethod.Jaccard;
    }
    
    @Override
    public StringBasedSentSimilarityMethod getStringBasedMethodType()
    {
        return (StringBasedSentSimilarityMethod.Jaccard);
    }

    /**
     * This function returns the similarity value (score) between two
     * raw sentences. 
     * 
     * The Jaccard similarity measures the similarity between two sets and is
     * computed as the number of common terms over the number of unique terms 
     * in both sets.
     * 
     * similarity(a,b) = ∣a ∩ b∣ / ∣a ∪ b∣
     * 
     * When ∣a ∪ b∣ is empty the sets have no elements in common.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return similarity score
     * @throws java.io.IOException
     */
    
    @Override
    public double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) throws IOException, FileNotFoundException, InterruptedException 
    {
        
        double similarity = 0.0; // We initialize the output
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_Preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_Preprocesser.getWordTokens(strRawSentence2);
        
        // Convert the lists to set objects
        // HashSet is a set where the elements are not sorted or ordered.
        
        Set<String> setWordsSentence1 = new HashSet<>(Arrays.asList(lstWordsSentence1)); 
        Set<String> setWordsSentence2 = new HashSet<>(Arrays.asList(lstWordsSentence2)); 

        // If both sets are empty, the similarity is 1
        
        if (setWordsSentence1.isEmpty() && setWordsSentence2.isEmpty()) {return 1.0f;}
        
        // If only one set is empty, the similarity is 0
        
        if (setWordsSentence1.isEmpty() || setWordsSentence2.isEmpty()) {return 0.0f;}

        float intersection = intersection(setWordsSentence1, setWordsSentence2).size();

        // ∣a ∩ b∣ / ∣a ∪ b∣
        // Implementation note: The size of the union of two sets is equal to
        // the size of both sets minus the duplicate elements.
        
        similarity = intersection / (setWordsSentence1.size() + setWordsSentence2.size() - intersection);
        
        return similarity;
    }
    
    /**
     * This function calculates the intersection between two sets 
     * to compute the Jaccard Similarity.
     * @param s1
     * @param s2
     * @return 
     */
    
    static Set<String> intersection(
            Set<String> s1, 
            Set<String> s2) 
    {
        Set<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        return intersection;
    }
}
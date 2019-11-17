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

import hesml.measures.IWordSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the BIOSSES2017 Measure for WBSM methods (WordNet-based methods)
 * Given two sentences, the method works as follows:
 * 
 * 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
 * 2. Initialize the semantic vectors.
 * 3. Use WordNet to construct the semantic vector
 * 4. Calculate the cosine similarity of the semantic vectors
 * 
 * @author alicia
 */

class WBSMMeasure extends SentenceSimilarityMeasure
{
    // Set with all joint words from the sentences
    
    private final Set<String> m_dictionary;
    
    // Semantic vectors for the sentences with the words and its score.
    
    private HashMap<String, Double> m_semanticVector1;
    private HashMap<String, Double> m_semanticVector2;
    
    // Word Similarity measure used for calculating similarity between words.
    
    IWordSimilarityMeasure wordSimilarityMeasure;
    
    /**
     * Constructor
     * @param preprocesser 
     */
    
    WBSMMeasure(IWordProcessing  preprocesser)
    {
        super(preprocesser);
        
        // Initialize the set
        
        m_dictionary = new HashSet<>();
        
        // Initialize the word similarity measure to null
        
        wordSimilarityMeasure = null;
    }
    
    /**
     * Return the current method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return SentenceSimilarityMethod.WBSMMeasure;
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.OntologyBased);
    }
    
    /**
     * The method
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    @Override
    public double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) throws IOException, FileNotFoundException, InterruptedException
    {
        // We initialize the output score
        
        double similarity = 0.0;
        
        // Preprocess the sentences and get the tokens for each sentence
        
        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
                
        this.constructDictionarySet(lstWordsSentence1, lstWordsSentence2);
        
        // 2. Initialize the semantic vectors.
        
        this.constructSemanticVectors(lstWordsSentence1, lstWordsSentence2);
        
        // Return the similarity value
        
        return (similarity);
    }
    
    /**
     * Constructs the dictionary set with all the distinct words from the sentences.
     * 
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private void constructDictionarySet(
            String[] lstWordsSentence1, 
            String[] lstWordsSentence2) throws FileNotFoundException
    {
        // Union of the two lists of words in a set.
        
        m_dictionary.addAll(Arrays.asList(lstWordsSentence1));
        m_dictionary.addAll(Arrays.asList(lstWordsSentence2));
    }
    
    /**
     * This method initializes the semantic vectors.
     * Each vector has the words from the dictionary vector.
     * If the word exists in the sentence of the semantic vector, the value is 1.
     * 
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private void constructSemanticVectors(
            String[] lstWordsSentence1, 
            String[] lstWordsSentence2) throws FileNotFoundException
    {
        
        // Convert arrays to set to facilitate the operations 
        // (this method do not preserve the word order)
        
        Set<String> setWordsSentence1 = new HashSet<>(Arrays.asList(lstWordsSentence1));
        Set<String> setWordsSentence2 = new HashSet<>(Arrays.asList(lstWordsSentence2));
        
        for (String word : m_dictionary)
        {
            // For each list of words of a sentence
            // If the value is in the sentence, the value of the semantic vector will be 1.
            
            if(setWordsSentence1.contains(word))
            {
                m_semanticVector1.put(word, 1.0);
            }
            else
            {
                m_semanticVector1.put(word, 0.0);
            }
            if(setWordsSentence2.contains(word)) 
            {
                m_semanticVector2.put(word, 1.0);
            }
            else
            {
                m_semanticVector2.put(word, 0.0);
            }
        } 
    }
}

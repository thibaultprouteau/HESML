/*
 * Copyright (C) 2019 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.sts.benchmarks.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents a sentence similarity dataset made up by two vectors
 * of sentences and another vector containg their corresponding similairity
 * scores.
 * @author j.lastra
 */

class SentenceSimilarityDataset
{
    /**
     * First and second sentences
     */
    
    private final String[]  m_FirstSentences;
    private final String[]  m_SecondSentences;
    
    /**
     * Similarity score defined by a human judgement
     */
    
    private final double[]  m_HumanJudgementSimilarity;
    
    /**
     * Constructor
     * @param indexId
     * @param word1
     * @param word2
     * @param similarity 
     */
    
    SentenceSimilarityDataset(
            String  strDatasetFilename) throws IOException, Exception
    {
        // We create the auxiliary list to read all sentence pairs
        
        ArrayList<String> temp = new ArrayList<>();
        
        // We create the reader of the file
        
        BufferedReader reader = new BufferedReader(new FileReader(strDatasetFilename));
        
        // We read the content of the file in row mode

        String strLine = reader.readLine();
        
        while (strLine != null)
        {
            temp.add(strLine);
            
            strLine = reader.readLine();
        }
        
        // We close the file
        
        reader.close();
        
        // We create the sentence pair arrays
        
        m_FirstSentences = new String[temp.size()];
        m_SecondSentences = new String[m_FirstSentences.length];
        m_HumanJudgementSimilarity = new double[m_FirstSentences.length];
        
        // we parser every line to extract the sentence pair and human
        // similarity judgement
        
        for (int i = 0; i < temp.size(); i++)
        {
            // We retrieve the 3 fields
            
            String[] strFields = strLine.split("\t");
            
            // We create a new word pair
            
            if (strFields.length == 3)
            {
                try
                {
                    m_FirstSentences[i] = strFields[0];
                    m_SecondSentences[i] = strFields[1];
                    m_HumanJudgementSimilarity[i] = Double.parseDouble(strFields[2]);
                }
                
                catch (NumberFormatException badFormatError)
                {
                    throw new Exception("Badly formatted line -> " + strLine);
                }
            }
        }
        
        // We release the temp list
        
        temp.clear();
    }
    
    /**
     * This functions returns the first word compared 1
     * @return First word compared
     */
    
    public String[] getSentencePairAt(
        int index)
    {
        // We check the index bounds
        
        if ((index < 0) || (index > m_FirstSentences.length - 1))
        {
            throw (new IndexOutOfBoundsException());
        }
        
        // We extract the returned sentence pair
        
        String[] strSentencePair = new String[2];
        
        strSentencePair[0] = m_FirstSentences[index];
        strSentencePair[1] = m_SecondSentences[index];
        
        // We return the result
        
        return (strSentencePair);
    }
    
    /**
     * Similarity defined for the word pair.
     * @return Human judgement of similarity between words
     */
    
    public double getHumanJudgementAt(
        int index)
    {
        // We check the index bounds
        
        if ((index < 0) || (index > m_FirstSentences.length - 1))
        {
            throw (new IndexOutOfBoundsException());
        }
        
        // We return the result
        
        return (m_HumanJudgementSimilarity[index]);
    }
}

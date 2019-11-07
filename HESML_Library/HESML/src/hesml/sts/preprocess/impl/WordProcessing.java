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
package hesml.sts.preprocess.impl;

import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This class configures the preprocess method and preprocess the sentences.
 * @author alicia
 */

class WordProcessing implements IWordProcessing
{

    private boolean m_lowercaseNormalization; // Configure if the text would be lowercased.

    private TokenizerType m_tokenizerType; // Set the tokenization method
    
    private CharFilteringType m_charFilteringType; // Set the filtering method

    // Set the stopwords filename and hashset. 
    // If empty, there's not a stopwords preprocessing.
    
    private String m_strStopWordsFileName;
    private HashSet<String> m_stopWordsSet;

    /**
     * Constructor by default
     */
    
    WordProcessing(){}
    
    /**
     * Constructor with parameters
     * @param tokenizerType
     * @param lowercaseNormalization 
     */
    
    WordProcessing(
            TokenizerType tokenizerType,
            boolean lowercaseNormalization,
            String strStopWordsFileName,
            CharFilteringType charFilteringType) throws IOException
    {
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilteringType = charFilteringType;
        HashSet<String> m_stopWordsSet = new HashSet<>();
        
        // If the file name of stop words is set, get the stop words
        
        if(m_strStopWordsFileName.length() > 0 
                && Files.exists(Paths.get(m_strStopWordsFileName)))
        {
            m_stopWordsSet = getStopWords();
        }
    }

    /**
     * Get the tokens from a string sentence.
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String[] getWordTokens(String strRawSentence)
    {
        // Initialize tokens
        
        String[] tokens;
        
        // Lowercase if true
        
        if(m_lowercaseNormalization) strRawSentence = strRawSentence.toLowerCase();
        
        // Filter the punctuation marks
        
        CharsFiltering filtering = new CharsFiltering(m_charFilteringType);
        strRawSentence = filtering.filter(strRawSentence);
        
        // Remove stop words if there are stop words
        
        if(m_stopWordsSet != null && !m_stopWordsSet.isEmpty()) 
            strRawSentence = removeStopwords(strRawSentence, m_stopWordsSet);

        // Tokenize the text
        
        Tokenizer tokenizer = new Tokenizer(m_tokenizerType);
        tokens = tokenizer.getTokens(strRawSentence);
        
        return tokens;
    }
    
    /**
     * Get the stop words using the list provided in BIOSSES2017 
     * and original code.
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    
    @Override
    public final HashSet<String> getStopWords() throws FileNotFoundException, IOException
    {
        // Initialize the hash set for stop words
        
        HashSet<String> stopWords = new HashSet<>();
        
        // Read the file and return the hash set
        
        BufferedReader buffer = new BufferedReader(new FileReader(new File(m_strStopWordsFileName)));
        String line;
        while((line=buffer.readLine()) != null)
        {
            String stop = line.replaceAll(" ", "");
            stopWords.add(stop);
        }
        buffer.close();
        return stopWords;
    }
    
    /**
     * Remove the stop words from a sentence
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String removeStopwords(String strRawSentence, HashSet<String> m_stopWords)
    {

        String [] splitArray = strRawSentence.split("\\s+"); // Split the sentence into words
        
        // Create the new string
        
        String sentenceString = new String();
        
        // Remove the words that are a stop word from the list.
        
        for (String strWord : splitArray)
        {
            if (!m_stopWords.contains(strWord))
            {
                sentenceString = sentenceString + strWord + " ";
            }
        }
        return sentenceString;
    }
    
    

    /**
     * Getters and setters
     * @return 
     */
    
    @Override
    public TokenizerType getTokenizerType(){return m_tokenizerType;}

    @Override
    public void setTokenizerType(TokenizerType tokenizerType){m_tokenizerType = tokenizerType;}

    @Override
    public boolean getLowercaseNormalization(){return m_lowercaseNormalization;}
    
    @Override
    public void setLowercaseNormalization(boolean lowercaseNormalization){m_lowercaseNormalization = lowercaseNormalization;}

    @Override
    public String getStrStopWordsFileName(){return m_strStopWordsFileName;}

    @Override
    public void setStrStopWordsFileName(String strStopWordsFileName)
    {
        m_strStopWordsFileName = strStopWordsFileName;
        
        if(m_strStopWordsFileName.length() > 0 && Files.exists(Paths.get(m_strStopWordsFileName)))
        {
            try
            {
                m_stopWordsSet = getStopWords();
            } catch (IOException ex)
            {
                Logger.getLogger(WordProcessing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public CharFilteringType getCharFilteringType(){return m_charFilteringType;}

    @Override
    public void setCharFilteringType(CharFilteringType charFilteringType){m_charFilteringType = charFilteringType;}
}

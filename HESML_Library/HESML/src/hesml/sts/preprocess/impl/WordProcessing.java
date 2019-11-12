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
import hesml.sts.preprocess.ITokenizer;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *  This class configures the preprocess method and preprocess the sentences.
 * @author alicia
 */

class WordProcessing implements IWordProcessing
{
    
    // Configure if the text would be lowercased.
    
    private final boolean m_lowercaseNormalization; 

    // Set the tokenization method
    
    private final TokenizerType m_tokenizerType; 
    
    // Set the filtering method
    
    private final CharFilteringType m_charFilteringType; 

    // Stopwords filename and hashset. 
    // If empty, there's not a stopwords preprocessing.
    
    private final String m_strStopWordsFileName;
    private HashSet<String> m_stopWordsSet;
    
    // The Temp and Python directories are used in some 
    // tokenizer methods that uses the Python wrapper.
    // Path to the temp directory.
    
    private final String m_TempDir;
    
    // Python executable using the virtual environment.
    
    private final String m_PythonVenvDir;
    
    // Python script wrapper
    
    private final String m_PythonScriptDir;
    
    // Path to the pretrained model embedding 
    
    private final String m_modelDirPath;

    /**
     * Constructor with parameters
     * @param tokenizerType tokenizer type used in the method
     * @param lowercaseNormalization true if lowercased
     * @param strStopWordsFileName stopWords file path
     * @param charFilteringType char filtering method used
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
        m_stopWordsSet = null;
        
        // If there is a valid file name for stop words, 
        // get the stop words in the hashSet
        
        if(m_strStopWordsFileName.length() > 0 
            && Files.exists(Paths.get(m_strStopWordsFileName)))
        {
            getStopWords();
        }
        
        // Initialize the temporal dirs to null.
        
        m_TempDir = null;
        m_PythonVenvDir = null;
        m_PythonScriptDir = null;
        m_modelDirPath = null;
    }
    
    /**
     * Constructor with parameters
     * 
     * @param tokenizerType tokenizer type used in the method
     * @param lowercaseNormalization true if lowercased
     * @param strStopWordsFileName stopWords file path
     * @param charFilteringType char filtering method used
     * 
     */
    
    WordProcessing(
            TokenizerType tokenizerType,
            boolean lowercaseNormalization,
            String strStopWordsFileName,
            CharFilteringType charFilteringType,
            String tempDir,
            String pythonVenvDir,
            String pythonScriptDir,
            String modelDirPath) throws IOException
    {
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilteringType = charFilteringType;
        m_stopWordsSet = null;
        
        // If there is a valid file name for stop words, 
        // get the stop words in the hashSet
        
        if(m_strStopWordsFileName.length() > 0 
            && Files.exists(Paths.get(m_strStopWordsFileName)))
        {
            getStopWords();
        }
        
        // Initialize the temporal dirs to null.
        
        m_TempDir = tempDir;
        m_PythonVenvDir = pythonVenvDir;
        m_PythonScriptDir = pythonScriptDir;
        m_modelDirPath = modelDirPath;
    }

    /**
     * Get the tokens from a string sentence.
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String[] getWordTokens(String strRawSentence) throws IOException, InterruptedException
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
            strRawSentence = removeStopwords(strRawSentence);

        // Tokenize the text
        
        if(m_tokenizerType != TokenizerType.WordPieceTokenizer)
        {
            ITokenizer tokenizer = new Tokenizer(m_tokenizerType);
            tokens = tokenizer.getTokens(strRawSentence);
        }
        else
        { 
            ITokenizer tokenizer = new Tokenizer(
                    m_tokenizerType,
                    m_PythonVenvDir,
                    m_PythonScriptDir,
                    m_modelDirPath);
            tokens = tokenizer.getTokens(strRawSentence);
        }
        
        return tokens;
    }
    
    /**
     * Get the stop words list
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    
    @Override
    public final void getStopWords() throws FileNotFoundException, IOException
    {
        // Initialize the hash set for stop words
        
        m_stopWordsSet = new HashSet<>();
        
        // Read the file and return the hash set
        
        BufferedReader buffer = new BufferedReader(new FileReader(new File(m_strStopWordsFileName)));
        String line;
        while((line=buffer.readLine()) != null)
        {
            String stop = line.replaceAll(" ", "");
            m_stopWordsSet.add(stop);
        }
        buffer.close();
    }
    
    /**
     * Remove the stop words from a sentence
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String removeStopwords(String strRawSentence)
    {

        // Split the sentence into words
        
        String [] splitArray = strRawSentence.split("\\s+"); 
        
        // Create the new string
        
        String sentenceString = new String();
        
        // Remove the words that are a stop word from the list.
        
        for (String strWord : splitArray)
        {
            if (!m_stopWordsSet.contains(strWord))
            {
                sentenceString = sentenceString + strWord + " ";
            }
        }
        return sentenceString;
    }


}
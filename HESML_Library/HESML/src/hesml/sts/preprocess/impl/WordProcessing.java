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
    
    private final CharsFiltering m_charFilter; 

    // Stopwords filename and hashset. 
    // If empty, there's not a stopwords preprocessing.
    
    private final String m_strStopWordsFileName;
    
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
    
    // Set with all the stop words
    
    private HashSet<String> m_stopWordsSet;

    /**
     * Constructor with parameters
     * @param tokenizerType tokenizer type used in the method
     * @param lowercaseNormalization true if lowercased
     * @param strStopWordsFileName stopWords file path
     * @param charFilteringType char filtering method used
     */
    
    WordProcessing(
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            String              strStopWordsFileName,
            CharFilteringType   charFilteringType) throws IOException
    {
        // We save the preprocessing parameters
        
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilter = new CharsFiltering(charFilteringType);
        
        // Initialize the temporal dirs to null.
        
        m_TempDir = null;
        m_PythonVenvDir = null;
        m_PythonScriptDir = null;
        m_modelDirPath = null;
        
        // loads the stop words in the constructor once
        
        HashSet<String> m_stopWordsSet = getStopWords();
    }
    
    /**
     * Constructor with parameters when using the python wrapper.
     * 
     * @param tokenizerType tokenizer type used in the method
     * @param lowercaseNormalization true if lowercased
     * @param strStopWordsFileName stopWords file path
     * @param charFilteringType char filtering method used
     * 
     */
    
    WordProcessing(
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            String              strStopWordsFileName,
            CharFilteringType   charFilteringType,
            String              tempDir,
            String              pythonVenvDir,
            String              pythonScriptDir,
            String              modelDirPath) throws IOException
    {
        // We saev the tokeniztion parameters
        
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilter = new CharsFiltering(charFilteringType);
        
        // Initialize the temporal dirs to null.
        
        m_TempDir = tempDir;
        m_PythonVenvDir = pythonVenvDir;
        m_PythonScriptDir = pythonScriptDir;
        m_modelDirPath = modelDirPath;
        
        // loads the stop words
        
        HashSet<String> m_stopWordsSet = getStopWords();
    }

    /**
     * This function releases all resources used by the object.
     */
    
    @Override
    public void clear()
    {
        // Clear the objects after use them.
        
        m_charFilter.clear();
        m_stopWordsSet.clear();
    }
    
    /**
     * Get the tokens from a string sentence.
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String[] getWordTokens(
            String  strRawSentence) throws IOException, InterruptedException
    {
        // Initialize tokens
        
        String[] tokens = {};
        
        // We initialize the filtered sentence
        
        String strFilteredSentence = new String(strRawSentence);
        
        // Tokenize the text
        
        // Check if the sentence is not empty
        
        if (strFilteredSentence.length() == 0)
        {
            String exception = "The sentence is empty";
            throw new IllegalArgumentException(exception);
        }
        
        // Tokenize the text
        
        ITokenizer tokenizer = new Tokenizer(m_tokenizerType);

        // We split the sentence into tokens

        String[] tokens_tokenized = tokenizer.getTokens(strFilteredSentence);
        
        // Preprocess each token and add to the output
        
        // Initialize an auxiliary arraylist to store the preprocessed words
        
        ArrayList<String> lstWordsPreprocessed = new ArrayList();
        
        // Iterate the tokens and preprocess them
        
        for (String token : tokens_tokenized)
        {
            String preprocessedToken = token;
            
            // Lowercase if true
        
            if (m_lowercaseNormalization) preprocessedToken = preprocessedToken.toLowerCase();

            // Filter the punctuation marks

            preprocessedToken = m_charFilter.filter(preprocessedToken);

            // Remove the word if its a stop word
            
            if(isStopword(preprocessedToken))
                preprocessedToken = "";
            
            // Is there is a word, add to the new sentence
            
            if(preprocessedToken.length() > 0)
            {
                lstWordsPreprocessed.add(preprocessedToken);
            }
        }
        
        // Convert the arraylist to a string array
        
        tokens = lstWordsPreprocessed.toArray(new String[0]);
        
        // Clear the arraylist
        
        lstWordsPreprocessed.clear();
        
        // Return the tokens
        
        return (tokens);
    }
    
    /**
     * Get the stop words list
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    
    private HashSet<String> getStopWords() throws FileNotFoundException, IOException
    {
        // Initialize the hash set for stop words
        
        HashSet<String> stopWordsSet = new HashSet<>();
    
        if ((m_strStopWordsFileName.length() > 0)
                && Files.exists(Paths.get(m_strStopWordsFileName)))
        {
            // Read the file and return the hash set

            FileReader fileReader = new FileReader(new File(m_strStopWordsFileName));
            BufferedReader buffer = new BufferedReader(fileReader);

            String line;

            while((line=buffer.readLine()) != null)
            {
                String stop = line.replaceAll(" ", "");
                stopWordsSet.add(stop);
            }

            // Close the file

            buffer.close();
            fileReader.close();
        }
        
        // Return the set of stop words
        
        return (stopWordsSet);
    }
    
    /**
     * Check if the word is a stop word
     * @param strRawSentence
     * @return Boolean true if there is a stop word, false if not.
     */
    
    private Boolean isStopword(
            String          strWord) throws IOException
    {
        // Initialize the boolean value to false
        
        Boolean isStopWord = false;
        
        // If the set of stop words is not empty, remove the stop words

        if ((m_stopWordsSet != null) && !m_stopWordsSet.isEmpty())
        {
            // If the token is a stop word, remove

            if(m_stopWordsSet.contains(strWord))
            {
                isStopWord = true;
            }
        }
        
        // Return the stop words
        
        return isStopWord;
    }
}
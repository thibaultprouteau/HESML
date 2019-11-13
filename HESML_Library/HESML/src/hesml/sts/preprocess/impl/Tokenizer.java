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

import hesml.sts.preprocess.ITokenizer;
import hesml.sts.preprocess.TokenizerType;

import edu.stanford.nlp.simple.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 *  Implementation of the tokenization methods
 * @author alicia
 */

class Tokenizer implements ITokenizer
{
    
    // Type of tokenization method
    
    private final TokenizerType m_tokenizerType;
    
    // Python executable using the virtual environment.
    
    private final String m_PythonVenvDir;
    
    // Python script wrapper
    
    private final String m_PythonScriptDir;
    
    // Path to the model dir path
    
    private final String m_modelDirPath;
    
    /**
     * Constructor
     * @param tokenizerType 
     */
    
    Tokenizer(
            TokenizerType tokenizerType)
    {
        
        // Set the tokenizer type 
        
        m_tokenizerType = tokenizerType;
        
        // If the Python wrapper is not used, set the variables null.
        
        m_PythonVenvDir = null;
        m_PythonScriptDir = null;
        m_modelDirPath = null;
    }
    
    /**
     * Constructor using the python wrapper.
     * 
     * @param TempDir
     * @param PythonVenvDir
     * @param PythonScriptDir
     * @throws IOException 
     */
    
    Tokenizer(
            TokenizerType   tokenizerType,
            String          PythonVenvDir,
            String          PythonScriptDir,
            String          modelDirPath) throws IOException
    {
        
        // Set the variables by constructor
        
        m_tokenizerType = tokenizerType;
        m_PythonScriptDir = PythonScriptDir;
        m_PythonVenvDir = PythonVenvDir;
        m_modelDirPath = modelDirPath;
    }
    
    
    /**
     * Get the tokenizer method
     * @return 
     */
    
    @Override
    public TokenizerType getTokenizerType()
    {
        return m_tokenizerType;
    }

    /**
     * Get the tokens from a sentence
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String[] getTokens(
            String  strRawSentence) throws InterruptedException, IOException
    {
        
        // Initialize the output
        
        String[] tokens = null; 
        
        // We create the tokenizer
        
        switch (m_tokenizerType)
        {
            
            case WhiteSpace:
                
                // Split words by whitespace.
                
                tokens = strRawSentence.split("\\s+"); 
                
                break;
                
            case StanfordCoreNLPv3_9_1:
                
                // convert to a corenlp sentence and get the tokens.
                
                Sentence sent = new Sentence(strRawSentence);
                tokens = sent.words().toArray(new String[0]); 
                
                break;
            
            case WordPieceTokenizer:
                
                tokens = this.getTokensUsingWordPiecePythonWrapper(strRawSentence);
                
                break;
        }
            
        return tokens;
    }

    /**
     * Execute the wrapper for get the tokenized texts.
     * 
     * @throws InterruptedException
     * @throws IOException 
     */
    
    private String[] getTokensUsingWordPiecePythonWrapper(
            String  strSentence) 
            throws InterruptedException, IOException
    {
        
        //Initialize the output
        
        String[] tokenizedText = null;
                
        // Fill the command params and execute the script
        
        String python_command = m_PythonVenvDir + " -W ignore " + m_PythonScriptDir;

        // Create the command 
        
        Process proc = new ProcessBuilder(
                m_PythonVenvDir,
                "-W",
                "ignore",
                m_PythonScriptDir,
                m_modelDirPath + "vocab.txt",
                strSentence).start();


        // Read the output 
        
        InputStreamReader inputStreamReader = new InputStreamReader(proc.getInputStream());
        BufferedReader readerTerminal = new BufferedReader(inputStreamReader);

        // Read the sentence and split by whitespaces
        
        String sentence = readerTerminal.readLine();
        sentence = sentence.trim();
        tokenizedText = sentence.split(" ");
        
        // Destroy the process
        
        proc.waitFor();  
        proc.destroy();
        
        // Return the result
        
        return tokenizedText;
    }
}

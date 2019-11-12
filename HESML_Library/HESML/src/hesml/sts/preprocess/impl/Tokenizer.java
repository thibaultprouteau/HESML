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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;


/**
 *  Implementation of the tokenization methods
 * @author alicia
 */

class Tokenizer implements ITokenizer
{
    /**
     * Type of tokenization method
     */
    
    private TokenizerType m_tokenizerType;
    
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
        m_tokenizerType = tokenizerType;
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
            TokenizerType tokenizerType,
            String PythonVenvDir,
            String PythonScriptDir,
            String modelDirPath) throws IOException
    {
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
    public TokenizerType getTokenizerType(){return m_tokenizerType;}

    /**
     * Get the tokens from a sentence
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String[] getTokens(
            String strRawSentence) throws InterruptedException, IOException
    {
        String[] tokens = null; // Returned value
        
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
//                tokens = this.getWordPieceTokens(strRawSentence);
                
                System.out.println(String.join(" ", tokens));
                
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
    
    private String[] getTokensUsingWordPiecePythonWrapper(String strSentence) throws InterruptedException, IOException
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

        BufferedReader readerTerminal = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//        String lineTerminal = "";
//        while((lineTerminal = readerTerminal.readLine()) != null) {
//            System.out.print(lineTerminal + "\n");
//        }

//        readerTerminal = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//        lineTerminal = "";
//        while((lineTerminal = readerTerminal.readLine()) != null) {
//            System.out.print(lineTerminal + "\n");
//        }

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
    
    private String[] getWordPieceTokens(String strRawSentence) throws IOException
    {
        // Initialize the result
        
        String[] tokens = null;
        
        strRawSentence = splitTokensOnPunctuation(strRawSentence);
        
        // Initialize an arraylist to use as auxiliar for saving the results
        
        List<String> aux = new ArrayList<String>();
        
        // Get the vocabulary set
        
        HashSet<String> vocab = getVocabList();
        
        // Define some variables as original BERT code does
        
        int max_input_chars_per_word = 200;
        String unk_token = "[UNK]";
        
        // Split by whitespace
        
        String[] tokens_whitespace = strRawSentence.split(" "); 
        
        
        for (int i = 0; i < tokens_whitespace.length; i++)
        {
            // Get the actual token (word)
            
            String token = tokens_whitespace[i];
            
            // Split the word into a list of chars
            
            String[] chars = token.split("(?!^)");
            
            if(chars.length > max_input_chars_per_word)
            {
                aux.add(unk_token);
                continue;
            }
            boolean isBad = false;
            int start = 0;
            List<String> sub_tokens = new ArrayList<>();
            while(start < chars.length)
            {
                int end = chars.length;
                String cur_substr = "";
                while(start < end)
                {
                    String substr = "";
                    for (int j = start; j < end; j++)
                    {
                        substr = substr + chars[j];
                    }
                    if(start > 0)
                    {
                        substr = "##" + substr;
                    }
                    if(vocab.contains(substr))
                    {
                        cur_substr = substr;
                        break;
                    }
                    end = end-1;
                }
                if(cur_substr.length() == 0)
                {
                    isBad = true;
                    break;
                }
                sub_tokens.add(cur_substr);
                start = end;
            }
            if(isBad)
            {
                aux.add(unk_token);
            }
            else
            {
                aux.addAll(sub_tokens);
            }
            
        }
        
        tokens = new String[aux.size()];
        tokens = aux.toArray(tokens);
        
        return tokens;
    }
    
    private String splitTokensOnPunctuation(String strRawSentence)
    {
        ArrayList<String> output = new ArrayList<>();
        
        // Split the text into a list of chars
            
        String[] chars = strRawSentence.split("(?!^)");
        int i = 0;
        boolean start_new_word = true;
        while(i < chars.length)
        {
            String _char = chars[i];
            if(Pattern.matches("\\p{IsPunctuation}", _char))
            {
                output.add(_char);
                start_new_word = true;
            }
            else
            {
                if(start_new_word)
                {
                    output.add("");
                }
                start_new_word = false;
                output.add(_char);
            }
            i++;
        }

        // Return the output
        
        return String.join(" ", output);
    }
    
    private HashSet<String> getVocabList() throws IOException
    {
        // Initialize the result
        
        HashSet<String> vocab = new HashSet<>();
        
        // Read the file and return the hash set
        
        BufferedReader buffer = new BufferedReader(new FileReader(new File(m_modelDirPath + "vocab.txt")));
        String line;
        while((line=buffer.readLine()) != null)
        {
            String stop = line.replaceAll(" ", "");
            vocab.add(stop);
        }
        buffer.close();
        
        
        // Return the result
        
        return vocab;
    }
}

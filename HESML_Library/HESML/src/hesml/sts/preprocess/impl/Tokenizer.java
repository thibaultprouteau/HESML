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
    
    /**
     * Constructor
     * @param tokenizerType 
     */
    
    public Tokenizer(
            TokenizerType tokenizerType)
    {
        m_tokenizerType = tokenizerType;
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
            String strRawSentence)
    {
        String[] tokens = null; // Returned value
        
        // We create the tokenizer
        
        switch (m_tokenizerType)
        {
            case WhiteSpace:
                tokens = strRawSentence.split("\\s+"); // Split words by whitespace.
                break;
            case StanfordCoreNLPv3_9_1:
                
                // convert to a corenlp sentence and get the tokens.
                
                Sentence sent = new Sentence(strRawSentence);
                tokens = sent.words().toArray(new String[0]); 
                break;
        }
            
        return tokens;
    }

    /**
     * Set the tokenizer method
     * @param tokenizerType 
     */
    
    @Override
    public void setTokenizerType(
            TokenizerType tokenizerType)
    {
        m_tokenizerType = tokenizerType;
    }
    

    
}

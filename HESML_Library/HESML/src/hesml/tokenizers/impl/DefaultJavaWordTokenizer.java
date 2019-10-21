/*
 * Copyright (C) 2019 j.lastra
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

package hesml.tokenizers.impl;

import hesml.tokenizers.IWordTokenizer;
import hesml.tokenizers.WordTokenizerMethod;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class implements a default work tokenizer using Java classes.
 * @author j.lastra
 */

class DefaultJavaWordTokenizer implements IWordTokenizer
{
    /**
     * Default delimitors
     */
    
    private final String  m_Delimitors;
    
    /**
     * Constructor
     */
    
    DefaultJavaWordTokenizer()
    {
        m_Delimitors = " ,.;[]?()/:|!\t";
    }
    
    /**
     * This function returns the method implemented by current object.
     * @return 
     */
    
    @Override
    public WordTokenizerMethod getMethod()
    {
        return (WordTokenizerMethod.DefaultJava);
    }
    
    /**
     * This function returns the collection of word tokens extracted from
     * the raw input sentence.
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String[] getWordTokens(String strRawSentence)
    {
        // We split the input sentences into tokens
        
        String[] strTokens = strRawSentence.split(" ");
        
        // We return the result
        
        return (strTokens);
    }
}

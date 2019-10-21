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

/**
 * This function builds the tokenizer instances.
 * @author j.lastra
 */

public class TokenizerFactory
{
    /**
     * This function creates a word tokenizer method.
     * @param method
     * @return 
     */
    
    public static IWordTokenizer getWordTokenizer(
            WordTokenizerMethod    method)
    {   
        return (new DefaultJavaWordTokenizer());
    }
}

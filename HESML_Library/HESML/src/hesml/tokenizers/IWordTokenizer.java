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
package hesml.tokenizers;

/**
 * This interface sets the asbtract methods which must be implemented by
 * any specific word tokenizer method.
 * @author j.lastra
 */

public interface IWordTokenizer
{
    /**
     * This fucntion returns the method implemented by current object.
     * @return 
     */
    
    WordTokenizerMethod getMethod();
    
    /**
     * This function returns the collection of word tokens extracted from
     * the raw input sentence.
     * @param strRawSentence
     * @return 
     */
    
    String[] getWordTokens(String strRawSentence);
}

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

package hesml.sts.preprocess;

import java.io.IOException;

/**
 *  Tokenizer for preprocessing the texts
 * @author alicia
 */

public interface ITokenizer
{
    
    /**
     * Get the tokenizer method
     * @return
     */
    
    TokenizerType getTokenizerType();
    
    
    /**
     * Given a sentence, get the tokens using the method selected.
     * @param strRawSentence
     * @return
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    
    String[] getTokens(
            String strRawSentence) throws InterruptedException, IOException;
}

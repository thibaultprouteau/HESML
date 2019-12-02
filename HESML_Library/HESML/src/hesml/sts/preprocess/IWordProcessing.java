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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This interface sets the abstract methods which must be implemented by
 * any specific word preprocessing method.
 * 
 * @author alicia
 */

public interface IWordProcessing
{
    /**
     * This function releases all resoruces used by the word preprocessingmethod.
     */
    
    void clear();
    
    /**
     * This function returns the collection of word tokens extracted from
     * the raw input sentence.
     * 
     * @param strRawSentence
     * @return String[]
     * @throws java.io.FileNotFoundException 
     * @throws java.lang.InterruptedException 
     */
    
    String[] getWordTokens(
            String strRawSentence)  
            throws FileNotFoundException, IOException, InterruptedException;
}

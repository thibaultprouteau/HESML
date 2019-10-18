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

package hesml.measures;

import java.io.IOException;

/**
 * This interface represents a pre-trained word embedding model which
 * can be interrogated to retrieve word vectors encoded by the model.
 * @author j.lastra
 */

public interface IPretrainedWordEmbedding
{
    /**
     * This function checks the existence of the word in the model.
     * @param strWord
     * @return 
     */
    
    boolean ContainsWord(String strWord);
    
    /**
     * This function returns the vector corresponding to the input word,
     * or a zero-value vector if the word is not in the model.
     * @param strWord
     * @return 
     */
    
    double[] getWordVector(String strWord)  throws IOException;
    
    /**
     * This function returns the dimensions of the vectors in the model.
     * @return 
     */
    
    int getVectorDimension();
}

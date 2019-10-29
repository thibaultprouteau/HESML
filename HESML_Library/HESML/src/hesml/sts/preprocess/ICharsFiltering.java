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

/**
 *  Abstract class for filtering punctuation methods
 * @author alicia
 */

public interface ICharsFiltering
{
    /**
     *  Get the filtering method
     * @return
     */
    
    CharFilteringType getCharFilteringType();
    
    /**
     * Set the filtering method
     * @param charFilteringType
     */
    
    void setCharFilteringType(
            CharFilteringType charFilteringType);
    
    /**
     *  Given a sentence, filter the chars using the method selected.
     * @param strRawSentence
     * @return
     */
    
    String filter(
            String strRawSentence);
}

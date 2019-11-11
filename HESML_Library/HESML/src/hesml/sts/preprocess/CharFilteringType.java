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
 *  Enumeration class for filtering the punctuation marks.
 * @author alicia
 */

public enum CharFilteringType
{
    /**
     * Use no filter method.
     */
    
    None,
    
    /**
     * Filter all the punctuation marks using a standard Java regex.
     */
    
    DefaultJava,
    
    /**
     * Filter punctuation marks using the BIOSSES2017 implementation.
     * 
     * Filter symbols: .;-:,_!()[]*?, whitespaces and "/"
     */
    
    BIOSSES2017,
    
    /**
     * Filter punctuation marks using Blagec2019 approximation.
     *  Filter full stop, comma, colon, semicolon, question mark, 
     * exclamation mark slash, dash.
     */
    
    Blagec2019
}

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
    
    Default,
    
    /**
     * Filter punctuation marks used by the authors of BIOSSES[1] paper
     * in their experiments.
     * 
     * Filtered symbols: .;-:,_!()[]*?, whitespaces and "/"
     * 
     * [1] G. Sogancioglu, H. Öztürk, A. Özgür,
     * BIOSSES: a semantic sentence similarity estimation system for
     * the biomedical domain, Bioinformatics. 33 (2017) i49–i58.
     */
    
    BIOSSES,
    
    /**
     * Filter punctuation marks used by Blagec et al. [1] in their
     * experiments.
     * Filtered symbols: full stop, comma, colon, semicolon, question mark, 
     * exclamation mark slash, dash.
     * 
     * [1] K. Blagec, H. Xu, A. Agibetov, M.
     * Samwald, Neural sentence embedding models for semantic similarity
     * estimation in the biomedical domain, BMC Bioinformatics. 20 (2019) 178.
     */
    
    Blagec2019
}

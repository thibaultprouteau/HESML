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
package hesml.sts.languagemodels;

/**
 *  This enum groups all the language models implemented in HESML STS.
 *  @author alicia
 */

public enum LanguageModelMethod
{
    /**
     * Paragraph vector DBOW approximation
     * 
     * Le, Quoc, and Tomas Mikolov. 2014. 
     * “Distributed Representations of Sentences and Documents.” 
     * In International Conference on Machine Learning, 1188–96. jmlr.org.
     * 
     */
    
    ParagraphVectorDBOW,
    
    /**
     * Paragraph vector DBOW approximation
     * 
     * Le, Quoc, and Tomas Mikolov. 2014. 
     * “Distributed Representations of Sentences and Documents.” 
     * In International Conference on Machine Learning, 1188–96. jmlr.org.
     * 
     */
    
    ParagraphVectorDM
}

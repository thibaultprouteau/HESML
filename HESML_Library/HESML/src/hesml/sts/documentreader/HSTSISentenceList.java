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
package hesml.sts.documentreader;

/**
 * A SentenceList is an object for iterating between sentences.
 * 
 * @author Alicia Lara-Clares
 */

public interface HSTSISentenceList extends Iterable<HSTSISentence>
{
    /**
     * This function returns the number of paragraphs.
     * @return int number of documents.
     */
    
    int getCount();
    
    /**
     * This function returns the paragraph object by its id.
     * @param idSentence
     * @return idParagraph document object.
     */
    
    HSTSISentence getSentence(
            int idSentence);
    
    /**
     * Add a paragraph to the object.
     * @param sentence
     */
    
    void addSentence(
            HSTSISentence sentence);
}

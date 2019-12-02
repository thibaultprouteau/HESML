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
 * This interface encapsulates the creation of a Sentence object.
 * @author Alicia Lara-Clares
 */

public interface HSTSISentence
{
    /**
     * Set a text to the sentence
     * @param text
     */
    
    void setText(String text);
    
    /**
     * Get the text from a sentence
     * @return String sentence
     */
    
    String getText();
    
    /**
     * Return the sentence id
     * @return id sentence
     */
    
    int getIdSentence();
    
    /**
     * Return the paragraph id
     * @return id paragraph
     */
    
    int getIdParagraph();
    
    /**
     * Return the document id
     * @return id document
     */
    
    int getIdDocument();
}

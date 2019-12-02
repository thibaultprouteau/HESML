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
package hesml.sts.documentreader.impl;

import hesml.sts.documentreader.HSTSISentence;

/**
 * This class implements a HSTS sentence object.
 * 
 * @author Alicia Lara-Clares
 */

class HSTSSentence implements HSTSISentence
{  
    // A sentence belongs to a document
    
    private final int m_idDocument;
    
    // A sentence belongs to a paragraph
    
    private final int m_idParagraph;
    
    // The sentence has an id sentence.
    
    private final int m_idSentence;
    
    // Text of the sentence.
    
    private String m_text;

    /**
     * HSTS sentence constructor
     * This class initializes the internal variables.
     * @param idSentence
     * @param idParagraph
     * @param idDocument
     * @param text 
     */
    
    HSTSSentence(
            int     idSentence, 
            int     idParagraph, 
            int     idDocument, 
            String  text) 
    {
        // Initialize the internal variables
        
        this.m_idSentence = idSentence;
        this.m_idParagraph = idParagraph;
        this.m_idDocument = idDocument;
        this.m_text = text;
    }
    
    /**
     * Set the text to the sentence
     * @param text 
     */
    
    @Override
    public void setText(
            String text)
    {
        m_text = text;
    }
    
    /**
     * Get the sentence text
     * @param text 
     */
    
    @Override
    public String getText()
    {
        return (m_text);
    }

    /**
     * Return the id sentence.
     * 
     * @return int id sentence
     */
    
    @Override
    public int getIdSentence() {
        return (m_idSentence);
    }

    /**
     * Return the id paragraph
     * @return 
     */
    
    @Override
    public int getIdParagraph() {
        return (m_idParagraph);
    }

    /**
     * Return the id document
     * @return 
     */
    
    @Override
    public int getIdDocument() {
        return (m_idDocument);
    }
}

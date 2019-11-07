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

import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.documentreader.HSTSISentence;

/**
 * HSTSSentence object.
 * @author Alicia Lara-Clares
 */

class HSTSSentence implements HSTSISentence
{  
    private final int m_idDocument;
    private final int m_idParagraph;
    private final int m_idSentence;
    
    private String m_text;

    public HSTSSentence(int idSentence, int idParagraph, int idDocument, String text) {
        this.m_idSentence = idSentence;
        this.m_idParagraph = idParagraph;
        this.m_idDocument = idDocument;
        this.m_text = text;
    }
    
    /**
     * Add a text to the paragraph
     * @param text 
     */
    @Override
    public void setText(String text)
    {
        m_text = text;
    }
    
    /**
     * Get the paragraph text
     * @param text 
     */
    @Override
    public String getText()
    {
        return m_text;
    }

    @Override
    public int getIdSentence() {
        return m_idSentence;
    }

    @Override
    public int getIdParagraph() {
        return m_idParagraph;
    }

    @Override
    public int getIdDocument() {
        return m_idDocument;
    }

  
}

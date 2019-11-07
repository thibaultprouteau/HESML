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

import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.documentreader.HSTSISentenceList;

/**
 * Paragraph object. Contains a SentenceList and a text field.
 * @author Alicia Lara-Clares
 */

class HSTSParagraph implements HSTSIParagraph{
    
    private final int m_idParagraph;
    private final int m_idDocument;
    
    private HSTSISentenceList m_sentenceList;
    private String m_text;
   
    
    /**
     * Creates an object document and extract the sentences.
     * @param strDocumentPath Path to the document.
     * @param documentType Type of input document.
     */
    
    public HSTSParagraph(int idParagraph, int idDocument) 
    {
        m_idParagraph = idParagraph;
        m_idDocument = idDocument;
        m_sentenceList = new HSTSSentenceList();
        m_text = "";
    }
    /**
     * Read the paragraphs from the document
     * @return ArrayList<IParagraphList> the list of paragraphs in the document.
     * @todo IMPLEMENT
     */
    
    @Override
    public HSTSISentenceList getSentenceList()
    {
        return m_sentenceList;
    }
    
    /**
     * Add sentences in the paragraph
     * @return ArrayList<ISentenceList> the list of sentences in the paragraph.
     * @todo IMPLEMENTAR - ojo, controlar que se intente insertar un sent que ya existia.
     */
    
    @Override
    public void setSentenceList(
            HSTSISentenceList sentenceList)
    {
        m_sentenceList = sentenceList;
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
    
    /**
     * Get the document id
     * @param id document 
     */
    
    @Override
    public int getIdDocument()
    {
        return m_idDocument;
    }

    @Override
    public int getId() {
        return m_idParagraph;
    }
}

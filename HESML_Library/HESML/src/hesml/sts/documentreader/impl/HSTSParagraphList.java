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

import java.util.ArrayList;
import java.util.Iterator;
import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.documentreader.HSTSIParagraphList;

/**
 * Iterable for Paragraph objects.
 * @author Alicia Lara-Clares
 */

public class HSTSParagraphList implements HSTSIParagraphList{
    private ArrayList<HSTSIParagraph> m_paragraphs;
    
    
    public HSTSParagraphList()
    {
        /**
         * Initialize the mapping table in blank.
         */
        
        m_paragraphs = new ArrayList<>();
    }
    
    /**
     * This function returns the number of documents existing.
     * @return int number of documents.
     */
    
    @Override
    public int getCount()
    {
        return (m_paragraphs.size());
    }
    
    /**
     * This function returns the Document object by its id.
     * @param idParagraph id of the document.
     * @return idParagraph document object.
     */
    
    @Override
    public HSTSIParagraph getParagraph(int idParagraph)
    {
        return (m_paragraphs.get(idParagraph));
    }
    
    
    @Override
    public void addParagraph(HSTSIParagraph paragraph)
    {
        m_paragraphs.add(paragraph);
    }
    
    @Override
    public Iterator<HSTSIParagraph> iterator() 
    {
        return (m_paragraphs.iterator());
    }
}

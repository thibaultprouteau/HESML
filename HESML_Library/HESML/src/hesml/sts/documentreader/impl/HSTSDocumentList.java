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
import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.documentreader.HSTSIDocumentList;

public class HSTSDocumentList implements HSTSIDocumentList{
    
    
    private ArrayList<HSTSIDocument> m_documents;
    
    
    public HSTSDocumentList()
    {
        /**
         * Initialize the mapping table in blank.
         */
        
        m_documents = new ArrayList<>();
    }
    
    /**
     * This function returns the number of documents existing.
     * @return int number of documents.
     */
    
    @Override
    public int getCount()
    {
        return (m_documents.size());
    }
    
    /**
     * This function returns the Document object by its id.
     * @param idDocument id of the document.
     * @return HSTSIDocument document object.
     */
    
    @Override
    public HSTSIDocument getDocument(int idDocument)
    {
        return (m_documents.get(idDocument));
    }
    
    
    @Override
    public void addDocument(HSTSIDocument document)
    {
        m_documents.add(document);
    }

    @Override
    public Iterator<HSTSIDocument> iterator() {
        return (m_documents.iterator());
    }
}

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
 * List of documents. Only for testing purposes.
 * @author Alicia Lara-Clares
 */

public interface HSTSIDocumentList extends Iterable<HSTSIDocument>
{
    /**
     * This function returns the number of documents existing.
     * @return int number of documents.
     */
    
    int getCount();
    
    /**
     * This function returns the Document object by its id.
     * @param idDocument id of the document.
     * @return HSTSIDocument document object.
     */
    
    HSTSIDocument getDocument(
            int idDocument);
    
    /**
     * Add a document
     * @param document
     */
    
    void addDocument(
            HSTSIDocument document);
}

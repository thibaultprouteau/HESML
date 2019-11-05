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

import hesml.sts.documentreader.HSTSDocumentType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.documentreader.HSTSIDocumentList;

/**
 * This class is responsible of creating the input Documents readers and writers
 * @author Alicia Lara-Clares
 */

public class HSTSDocumentFactory {
    
    /**
     * This function loads all the files from a directory.
     * 
     * @param strDocumentsPath
     * @param documentType
     * @return 
     * @throws java.io.IOException 
     * @throws javax.xml.stream.XMLStreamException 
     */
    
    public static HSTSIDocumentList loadDatabase(
            String strDocumentsPath,
            HSTSDocumentType documentType) throws IOException, XMLStreamException
    {
        if (documentType == HSTSDocumentType.BioCXMLASCII) {
            return (BioCReader.loadFilesFromDir(strDocumentsPath));
        }
        
        return null;
        
    }
    
    /**
     * This function load a file and creates an HSTSIDocument object.
     * @param iDocument
     * @param fileInput
     * @param documentType
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     * 
     * @todo switch case! Exception if null!! try catch en el main!
     */
    
    public static HSTSIDocument loadDocument(
            int             iDocument,
            File            fileInput,
            HSTSDocumentType    documentType) throws FileNotFoundException, XMLStreamException
    {
        
        // We initialize the output document
        
        HSTSIDocument doc = (documentType == HSTSDocumentType.BioCXMLASCII) ?
                        BioCReader.loadFile(iDocument, fileInput) : null;
     
        // We return the result
        
        return doc;
    }
    
    /**
     * This function encapsulates the writing of sentences from an HSTSIDocument object to a file
     * @param document
     * @param fileOutput
     * @throws IOException
     */
    
    public static void writeSentencesToFile(
            HSTSIDocument document,
            File fileOutput) throws IOException
    {
        document.saveSentencesToFile(fileOutput);
    }
    
    

}
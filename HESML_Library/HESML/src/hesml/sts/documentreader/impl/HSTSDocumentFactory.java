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
import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.preprocess.IWordProcessing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class is responsible of creating the input Documents readers and writers
 * @author Alicia Lara-Clares
 */

public class HSTSDocumentFactory 
{
    /**
     * This function load a file and creates an HSTSIDocument object 
     * filling the paragraphs and sentences.
     * 
     * @param iDocument
     * @param fileInput
     * @param documentType
     * @param preprocesser
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    
    public static HSTSIDocument loadDocument(
            int                 iDocument,
            File                fileInput,
            HSTSDocumentType    documentType,
            IWordProcessing     preprocesser) 
            throws FileNotFoundException, XMLStreamException
    {
        // We initialize the output document
        HSTSIDocument document = null;
        
        if(documentType == HSTSDocumentType.BioCXMLUnicode)
        {
            // Initialize the reader
            
            BioCReader biocReader = new BioCReader(iDocument, fileInput, preprocesser);
            
            // Read the bioc file
            
            document = biocReader.readFile();
            
            // Clean the internal variables.
            
            biocReader.clean();
        }

        // We return the result
        
        return (document);
    }
    
    
    /**
     * This function encapsulates the writing of 
     * sentences from an HSTSIDocument object to a file.
     * 
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
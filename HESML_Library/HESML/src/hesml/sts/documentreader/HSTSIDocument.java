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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Interface that encapsulates the creation of a Document.
 * @author Alicia Lara-Clares
 */

public interface HSTSIDocument 
{   
    /**
     * This function extracts the paragraphs from the document
     * @return HSTSIParagraphList
     */
    
    HSTSIParagraphList getParagraphList();
    
    /**
     * Set a ParagraphList to the HSTSIDocument object.
     * @param paragraphList
     */
    
    void setParagraphList(HSTSIParagraphList paragraphList);
    
    /**
     * Preprocess the document
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    
    void preprocessDocument() throws FileNotFoundException, IOException, InterruptedException;

    /**
     * Save sentences for each paragraph to an output file.
     * @param fileOutput
     * @throws IOException
     */
    
    void saveSentencesToFile(File fileOutput) throws IOException;
}

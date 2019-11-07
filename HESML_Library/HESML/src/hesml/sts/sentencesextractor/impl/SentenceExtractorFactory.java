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
package hesml.sts.sentencesextractor.impl;

import hesml.sts.documentreader.HSTSDocumentType;
import hesml.sts.documentreader.impl.HSTSDocumentFactory;
import hesml.sts.sentencesextractor.SentenceExtractorType;
import hesml.sts.sentencesextractor.SentenceSplitterType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import hesml.sts.documentreader.HSTSIDocument;


/**
 * The aim of this class is to instantiate all the preprocessing pipeline
 * in order to hide the implementation classes to the client code.
 * @author Alicia Lara-Clares
 */

public class SentenceExtractorFactory {

    /**
     *  Constructor of the preprocess pipeline factory
     */
    
    public SentenceExtractorFactory()
    {
        
    }
    
    /**
     * This function preprocess file-by-file.For each file in the directory:
     *   (1) check if the file is preprocessed.(2.1) If the file is processed, continue.(2.2) If the file is not processed:
       (3) Extract the paragraphs
       (5) Split into sentences
       (6) Write to output directory
 
 This method is efficient if there is a large number of files in the directory.
     * 
     * 
     * @param strDocumentsPathInput: Directory where are the input files.
     * @param strDocumentsPathOutput: Directory for writing the results.
     * @param documentType: Type of input document.
     * @param sentenceSplitterType: Sentence Splitter method used.
     * @param preprocessType
     * @param allInOneFile: save all the sentences in the same file
     * @throws IOException
     * @throws FileNotFoundException
     * @throws XMLStreamException
     * 
     * 
     * @todo Process file-by-file if the file is not processed!.
     * @todo Write the output with subdirectories info!
     */
    
    public void runSentenceExtractorPipeline(
            String strDocumentsPathInput,
            String strDocumentsPathOutput,
            HSTSDocumentType documentType,
            SentenceSplitterType sentenceSplitterType,
            SentenceExtractorType preprocessType,
            boolean allInOneFile) throws IOException, FileNotFoundException, XMLStreamException 
    {

        // List all directories in the path
        
        File[] directories = new File(strDocumentsPathInput).listFiles(File::isDirectory);
        
        
        //For each directory, list all files
        
        for (File directory : directories) 
        {
            if(!allInOneFile)
                SentenceExtractorFactory.createOutputDirectoryStructure(directory, strDocumentsPathOutput);
            
            // List all the regular files in the folder.
            // @todo List XML files if XML, ...
           
            List<File> filesInFolder = Files.walk(Paths.get(directory.getAbsolutePath()))
                            .filter(Files::isRegularFile)
                            .map(Path::toFile)
                            .collect(Collectors.toList());
            
            // For each file...
        
            for (int i = 0; i < filesInFolder.size(); i++) 
            {
                File fileInput = filesInFolder.get(i);

                int idDocument = i;


                // Load the document and fill the paragraphs


                HSTSIDocument documentWithParagraphs = HSTSDocumentFactory.loadDocument(idDocument, fileInput, documentType);

                HSTSIDocument documentWithSentences = documentWithParagraphs;


                /**
                 * Write the output sentences into the correct subdirectories
                 */
                if(!allInOneFile)
                {
                    String strFileOutput = strDocumentsPathOutput
                        .concat(directory.getName())
                        .concat("/")
                        .concat(fileInput.getName().substring(0, fileInput.getName().lastIndexOf('.'))
                        .concat(".txt"));
                    File fileOutput = new File(strFileOutput);
                    HSTSDocumentFactory.writeSentencesToFile(documentWithSentences, fileOutput);
                } else
                {
                    String strFileOutput = strDocumentsPathOutput.concat("allSentencesInAFile.txt");
                    File fileOutput = new File(strFileOutput);
                    HSTSDocumentFactory.writeSentencesToFile(documentWithSentences, fileOutput);
                }
                


            }
        }
        
        
    }
    
    public static void createOutputDirectoryStructure(File subdirectory, String strDocumentsPathOutput) 
    {
        String dirName = subdirectory.getName();
        String outputSubdirectoryPath = strDocumentsPathOutput.concat(dirName);
        
        File directory = new File(outputSubdirectoryPath);
        if (! directory.exists()){
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

    }

}
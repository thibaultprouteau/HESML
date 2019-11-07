/*
 * Copyright (C) 2019
 *
 * This program is free software for non-commercial use:
 * you can redistribute it and/or modify it under the terms of the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * (CC BY-NC-SA 4.0) as published by the Creative Commons Corporation,
 * either version 4 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * section 5 of the CC BY-NC-SA 4.0 License for more details.
 *
 * You should have received a copy of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0) 
 * license along with this program. If not,
 * see <http://creativecommons.org/licenses/by-nc-sa/4.0/>.
 *
 */

package hesmlstsclient;

import hesml.HESMLversion;
import hesml.sts.documentreader.HSTSDocumentType;
import hesml.sts.sentencesextractor.SentenceExtractorType;
import hesml.sts.sentencesextractor.SentenceSplitterType;
import hesml.sts.sentencesextractor.impl.SentenceExtractorFactory;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import javax.xml.stream.XMLStreamException;

/**
 * This class implements the sentence splitting of documents for the input of some trainning methods.
 * 
 * @author alicia
 */

public class HESMLSTSDocumentsSentenceSplittingclient
{
    /**
     * This function loads an input XML file detailing a
     * set of reproducible experiments on sentence similarity.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        
        boolean   showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, October 2019) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file.
        
        testD0();
        
    }
    
    /**
     *  Function only for testing purposes. 
     * Set an input BioC files directory and run the preprocess pipeline to create D0 documents.
     * -> D0 documents: tokenization and sentence splitting*
     *      * In BioC documents the tokenization and sentence splitting is carried out using the bioc library.
     * -> D1 documents: metamap annotations @todo not implemented yet
     * @throws IOException
     * @throws XMLStreamException
     * 
     * @todo establish input directory and read the tree. Output the results using the same structure than the input directories.
     */
    
    private static void testD0() throws IOException, XMLStreamException
    {
        //Calculate the execution time of the method
        
        Instant start = Instant.now();
        
        // We define the input and ooutput directory
        
//        String strBioCDirInput = "../BioC/";
//        String strBioCDirOutput = "../BioC_sentencesSplitted_D0/";
        
        String strBioCDirInput = "/home/alicia/Desktop/BioCManuscriptCorpus/";
        String strBioCDirOutput = "../BioCCorpus/BioC_sentencesSplitted_D0/";
        
        // Select BioC XML files and the default BioC Project Sentences Splitter (from Stanford CoreNLP)
        
        HSTSDocumentType documentType = HSTSDocumentType.BioCXMLASCII;
        SentenceSplitterType sentenceSplitterType = SentenceSplitterType.BioCSentenceSplitter;
        SentenceExtractorType preprocessType = SentenceExtractorType.D0;
        boolean saveAllSentencesToASingleFile = Boolean.TRUE;
        // Create a preprocess object and run the pipeline for D0 documents
        
        SentenceExtractorFactory sentenceExtractor = new SentenceExtractorFactory();
        sentenceExtractor.runSentenceExtractorPipeline(
                                strBioCDirInput, 
                                strBioCDirOutput, 
                                documentType, 
                                sentenceSplitterType,
                                preprocessType,
                                saveAllSentencesToASingleFile);
        
        //Calculate the execution time 
        
        Instant end = Instant.now();
        Duration interval = Duration.between(start, end);
        System.out.println("Execution time in seconds: " + interval.getSeconds());
    } 
    
}

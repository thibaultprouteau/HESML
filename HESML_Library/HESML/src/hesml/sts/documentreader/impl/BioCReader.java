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
import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.preprocess.IWordProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.io.BioCCollectionReader;
import bioc.io.BioCFactory;
import bioc.preprocessing.pipeline.PreprocessingPipeline;

// Used Stanford CoreNLP for extracting sentences

import edu.stanford.nlp.ling.CoreLabel; 

/**
 * The BioCReader class gets the directory path of the document and the 
 type of document and create an HSTSIDocument object.
 * @author Alicia Lara-Clares
 */

class BioCReader { 

    /**
     * Loads the BioC file, extract the documents and paragraphs.
     * -> A BioCColection has one or more BioCDocuments.
     * -> Each BioCDocument has one or more BioCPassages.
     * -> Each BioCPassage has one text (similar to paragraph).
     * @param strDocumentsPath
     * @param documentType
     * @return HSTSIDocument
     * @throws IOException
     * @throws XMLStreamException 
     */
    
    static HSTSIDocument loadFile(
            int             idDocument,
            File            biocFile,
            IWordProcessing wordPreprocessing) throws FileNotFoundException, XMLStreamException
    {
        // read BioC XML collection
        
        BioCCollection collection = readBioCCollection(biocFile);
        
        // Extract the documents from the BioC collection
        
        List<BioCDocument> biocDocuments = collection.getDocuments();

        /**
        * Create an IDocument
        */
        
        String str_documentPath = biocFile.getAbsolutePath();
        HSTSDocument document = new HSTSDocument(idDocument, str_documentPath, wordPreprocessing);

        /**
         * Create a ParagraphList
         */
        
        HSTSParagraphList paragraphList = new HSTSParagraphList();

        /**
         * Iterate the BioCCollection: a list of documents
         */

        for (Iterator<BioCDocument> iterator = biocDocuments.iterator(); iterator.hasNext();) 
        {
            BioCDocument nextBioCDocument = iterator.next();


            List<BioCPassage> bioCPassages = nextBioCDocument.getPassages();

            /**
             * Iterate over passages and extract the texts
             */
            
            int idParagraph = 0;
            for (Iterator<BioCPassage> iterator1 = bioCPassages.iterator(); iterator1.hasNext();) {
                BioCPassage nextBioCPassage = iterator1.next();

                /**
                * Create an IParagraph
                */
                
                HSTSParagraph paragraph = new HSTSParagraph(idParagraph, idDocument);

                String strParagraph = nextBioCPassage.getText();
                paragraph.setText(strParagraph);
                
                // In BioC, the sentences are collected using the BioC library. 
                // In the preprocess pipeline it is possible to clean the sentences
                // and re-split them.
                
                paragraph.setSentenceList(BioCReader.extractBioCSentences(paragraph));
                
                
                // Add the text to the paragraph
                
                paragraphList.addParagraph(paragraph);
                idParagraph++;
            }

        }
        
        // Add the paragraphs to the document
        
        document.setParagraphList(paragraphList);
        return document;
    }
    
    /**
    * Call to the BioC Java Pipeline library to perform the sentence splitting
    * 
    * "Sentence segmenter
       * An efficient sentence segmenter, DocumentPreprocessor, is used to produce a list of sentences from a plain text. 
       * It is a creation of the Stanford NLP group using a heuristic finite-state machine that assumes the sentence 
       * ending is always signaled by a fixed set of characters. Tokenization is performed by the default rule-based 
       * tokenizer of the sentence segmenter, PTBTokenizer, before the segmenting process to divide text into a sequence of tokens. 
       * The ‘invertible’ option of the tokenizer is invoked to ensure that multiple whitespaces 
       * are reflected in token offsets so that the resulting tokens can be faithfully converted back to the original text. 
       * Sentence segmentation is then a deterministic consequence of tokenization."
       * Cited from:
       * Comeau, D. C., Liu, H., Islamaj Doğan, R., & Wilbur, W. J. (2014). 
       * Natural language processing pipelines to annotate BioC collections with 
       * an application to the NCBI disease corpus. Database : the journal of 
       * biological databases and curation, 2014, bau056. doi:10.1093/database/bau056
    */
    
    
    static HSTSSentenceList extractBioCSentences(HSTSIParagraph paragraph)
    {
        PreprocessingPipeline preprocessingPipeline = new PreprocessingPipeline("sentence");
        String strParagraph = paragraph.getText();
        preprocessingPipeline.setParseText(strParagraph);
        List<List<CoreLabel>> sentencesCoreLabels = preprocessingPipeline.performSentenceSplit();
        int idParagraph = paragraph.getId();
        int idDocument = paragraph.getIdDocument();
    
        //Create a new HSTSSentenceList object
        
        HSTSSentenceList sentenceList = new HSTSSentenceList();
        int idSentence = 0;
        for(List<CoreLabel> sentenceCoreLabel : sentencesCoreLabels) 
        {
            String text = strParagraph.substring(sentenceCoreLabel.get(0).beginPosition(), sentenceCoreLabel.get(sentenceCoreLabel.size()-1).endPosition());
            HSTSSentence sentence = new HSTSSentence(idSentence, idParagraph, idDocument, text);
                    
            //add the sentence to the sentences list
        
            sentenceList.addSentence(sentence);
            idSentence++;
        }
        return sentenceList;
    }
            
    static BioCCollection readBioCCollection(File biocFile) throws FileNotFoundException, XMLStreamException
    {
        // read BioC XML collection
        
        Reader inputReader = new FileReader(biocFile);
        BioCFactory bioCFactory = BioCFactory.newFactory("STANDARD");
        
        /**
         * Now I create a BioCCollection item. 
         */
        
        BioCCollectionReader collectionReader =
        bioCFactory.createBioCCollectionReader(inputReader);
        BioCCollection collection = collectionReader.readCollection();
        return collection;
    }
}

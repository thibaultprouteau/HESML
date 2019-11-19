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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.documentreader.HSTSIParagraphList;
import hesml.sts.documentreader.HSTSISentence;
import hesml.sts.documentreader.HSTSISentenceList;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

/**
 *
 * @author Alicia Lara-Clares
 */
class HSTSDocument implements HSTSIDocument{
    
    private final int m_idDocument;
    
    private final String m_strDocumentPath;
    
    private HSTSIParagraphList m_paragraphList;
    
    // Preprocesser object
    
    private final IWordProcessing m_preprocessing;
    
    /**
     * Creates an object document and extract the sentences.
     * @param strDocumentPath Path to the document.
     */
    
    public HSTSDocument(
            int idDocument,
            String strDocumentPath,
            IWordProcessing wordPreprocessing) 
    {
        m_idDocument = idDocument;
        m_strDocumentPath = strDocumentPath;
        m_preprocessing = wordPreprocessing;
        m_paragraphList = new HSTSParagraphList();
    }

    /**
     * Read the paragraphs from the document
     * @return ArrayList<IParagraphList> the list of paragraphs in the document.
     */
    
    @Override
    public HSTSIParagraphList getParagraphList()
    {
        return m_paragraphList;
    }
    
    /**
     * Add a new Paragraph in the document
     * @return HSTSIParagraphList the list of paragraphs in the document.
     */
    
    @Override
    public void setParagraphList(HSTSIParagraphList paragraphList)
    {
        //ITerate paragraphs... and add to the list
        m_paragraphList = paragraphList;
    }
    
    @Override
    public void saveSentencesToFile(
        File fileOutput) throws IOException
    {
        
        /**
         * Create a StringBuilder object and fill with the sentences
         */
        
        StringBuilder sb = new StringBuilder();
        for (HSTSIParagraph paragraph : m_paragraphList) {
            HSTSISentenceList sentenceList = paragraph.getSentenceList();
            for (HSTSISentence sentence : sentenceList) {
                String strSentence = sentence.getText();
                if(strSentence.length() > 0)
                {
                    sb.append(strSentence);
                    sb.append("\n");
                }
            }
        }
        
        // Get the output that will be written to the file
        
        String strFinalText = sb.toString();
        
        // Write the file
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileOutput, true))) {
            writer.write(strFinalText);
        }
        
    }

    /**
     * Get the document and preprocess using IWordProcesser 
     * 
     */
    
    @Override
    public void preprocessDocument() 
            throws FileNotFoundException, IOException, InterruptedException
    {
        // Create a pattern for checking if there is an alphanumeric character in the sentence
        
        Pattern p = Pattern.compile("[[:alnum:]]");
        
        // For each paragraph iterate the sentences
        
        for (HSTSIParagraph paragraph : m_paragraphList) 
        {
            // For each sentence get word tokens if it's not empty and has alphanumeric characters.
            
            HSTSISentenceList sentenceList = paragraph.getSentenceList();
            for (HSTSISentence sentence : sentenceList) 
            {
                // Get the sentence
                
                String strSentence = sentence.getText();
                if(strSentence.length() > 0 && p.matcher(strSentence).find())
                {
                    String[] tokens = m_preprocessing.getWordTokens(strSentence);
                    if(tokens.length > 0)
                    {
                        // Join the tokens and create a new preprocessed sentence
                        
                        String newSentence = String.join(" ", tokens);
                        sentence.setText(newSentence); 
                    }
                }
                else
                {
                    sentence.setText(""); 
                }
            }
        }
    }
    
}

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

package hesml.sts.preprocess;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

/**
 * This interface sets the abstract methods which must be implemented by
 * any specific word preprocessing method.
 * @author alicia
 */

public interface IWordProcessing
{
    /**
     * This function returns the collection of word tokens extracted from
     * the raw input sentence.
     * @param strRawSentence
     * @return 
     * @throws java.io.FileNotFoundException 
     */
    
    String[] getWordTokens(
            String strRawSentence)  throws FileNotFoundException;
    
    /**
     * Get the tokenizer method
     * @return
     */
    
    TokenizerType getTokenizerType();

    /**
     *  Set the tokenizer method
     * @param tokenizerType
     */
    
    void setTokenizerType(
            TokenizerType tokenizerType);
    
    /**
     *  Get if the preprocess normalizes as lowercase
     * @return
     */
    
    boolean getLowercaseNormalization();

    /**
     *  Set the lowercase normalization
     * @param lowercaseNormalization
     */
    
    void setLowercaseNormalization(
            boolean lowercaseNormalization);
    
    /**
     * Set the stopWords file name
     * @return
     */
    
    String getStrStopWordsFileName();
    
    /**
     *  Set the stopwords file name
     * @param strStopWordsFileName
     */
    
    void setStrStopWordsFileName(
            String strStopWordsFileName);
    
    /**
     * Remove the stop words
     * @param strRawSentence
     * @param m_stopWords
     * @return 
     */
    
    String removeStopwords(
            String          strRawSentence, 
            HashSet<String> m_stopWords);
    
    /**
     * Read the stop words file name
     * @return 
     * @throws java.io.FileNotFoundException 
     */
    
    HashSet<String> getStopWords() throws FileNotFoundException, IOException;
    
    /**
     *  Get the char filtering method
     * @return
     */
    
    CharFilteringType getCharFilteringType();
    
    /**
     *  Set the char filtering method
     * @param charFilteringType
     */
    
    void setCharFilteringType(
            CharFilteringType charFilteringType);
    
}

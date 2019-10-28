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
package hesml.sts.preprocess.impl;

import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.PreprocessType;
import hesml.sts.preprocess.TokenizerType;
import java.io.IOException;


/**
 * The aim of this class is to instantiate the preprocessing pipeline
 * in order to hide the implementation classes to the client code.
 * 
 * @author alicia
 */

public class PreprocessFactory {

    /**
     *  Constructor of the preprocess pipeline factory
     * 
     * Given a preset preprocess method, configure the preprocessing.
     * 
     * @param method
     * @return 
     * @throws java.io.IOException
     */
    
    public static IWordProcessing getPreprocessPipeline(
            PreprocessType    method) throws IOException
    {   
        // Initialize de preprocessing object
        
        IWordProcessing processed = new WordProcessing(); // Returned value
        
        // Initialize the stop words file
        
        String stopWordFileName = "";

        // Configure each method
        
        switch (method)
        {
            case DefaultJava:
                processed.setLowercaseNormalization(Boolean.TRUE);
                processed.setTokenizerType(TokenizerType.WhiteSpace);
                processed.setCharFilteringType(CharFilteringType.DefaultJava);
                break;
            case Biosses2017:
                processed.setLowercaseNormalization(Boolean.TRUE);
                processed.setTokenizerType(TokenizerType.WhiteSpace);
                processed.setCharFilteringType(CharFilteringType.BIOSSES2017);
                stopWordFileName = "../StopWordsFiles/Biosses2017StopWords.txt";
                break;
            case Blagec2019:
                processed.setLowercaseNormalization(Boolean.FALSE);
                processed.setCharFilteringType(CharFilteringType.Blagec2019);
                stopWordFileName = "../StopWordsFiles/Biosses2017StopWords.txt";
                processed.setTokenizerType(TokenizerType.StanfordCoreNLPv3_9_1);
                break;
        }
        
        processed.setStrStopWordsFileName(stopWordFileName);

        return processed;
    }
    
    /**
     *  Constructor of the preprocess pipeline factory 
     * 
     * Custom preprocessing factory, giving all the parameters.
     * 
     * @param tokenizerType
     * @param lowercaseNormalization
     * @param stopWordFileName
     * @return 
     * @throws java.io.IOException
     */
    
    public static IWordProcessing getPreprocessPipeline(
            boolean lowercaseNormalization,
            TokenizerType tokenizerType,
            String stopWordFileName,
            CharFilteringType charFilteringType) throws IOException
    {   
        
        // Initialize de preprocessing object
        
        IWordProcessing processed = new WordProcessing(); // Returned value

        processed.setLowercaseNormalization(lowercaseNormalization);
        processed.setTokenizerType(tokenizerType);
        processed.setStrStopWordsFileName(stopWordFileName);
        processed.setCharFilteringType(charFilteringType);

        return processed;
    }
}
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
  
        String stopWordFileName = ""; // Initialize the stop words file

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
            case Biosses2017_withStopWords:
                processed.setLowercaseNormalization(Boolean.TRUE);
                processed.setTokenizerType(TokenizerType.WhiteSpace);
                processed.setCharFilteringType(CharFilteringType.BIOSSES2017);
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
     * @param charFilteringType
     * @return 
     * @throws java.io.IOException
     */
    
    public static IWordProcessing getPreprocessPipeline(
            boolean             lowercaseNormalization,
            TokenizerType       tokenizerType,
            String              stopWordFileName,
            CharFilteringType   charFilteringType) throws IOException
    {   
        
        // Initialize de preprocessing object
        
        IWordProcessing processed = new WordProcessing(); // Returned value

        processed.setLowercaseNormalization(lowercaseNormalization);
        processed.setTokenizerType(tokenizerType);
        processed.setStrStopWordsFileName(stopWordFileName);
        processed.setCharFilteringType(charFilteringType);

        return processed;
    }
    
    
    public static void preprocessDataset(
            PreprocessType    method,
            String strInputDatasetPath,
            String strOutputDatasetPath) throws IOException, Exception
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(method);

        System.out.println("Loading raw dataset from " + strInputDatasetPath);
        
        SentenceSimilarityDataset dataset = new SentenceSimilarityDataset(strInputDatasetPath);

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(strOutputDatasetPath)));
  
                
        for (int i = 0; i < dataset.getNumerSentences(); i++)
        {
            String[] pair = dataset.getSentencePairAt(i);
            String[] sentence1Processed = preprocess.getWordTokens(pair[0]);
            String[] sentence2Processed = preprocess.getWordTokens(pair[1]);
            
            String preprocessedSentences = String.join(" ", sentence1Processed) + "\t" + String.join(" ", sentence2Processed) + "\n";
            writer.write(preprocessedSentences);
        }

        writer.close();

        System.out.println("Loaded preprocessed dataset in: " + strOutputDatasetPath);
    }
}
/*
 * Copyright (C) 2019 j.lastra
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
package hesml.sts.measures.impl;

import hesml.measures.WordEmbeddingFileType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SWEMpoolingMethod;
import hesml.sts.measures.SentenceEmbeddingMethod;
import hesml.sts.measures.StringBasedSentSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.IOException;
import java.text.ParseException;

/**
 * This class builds the sentence similarity measures
 * @author j.lastra
 */

public class SentenceSimilarityFactory
{
    /**
     * This function creates a Simple Word-Emebedding model for
     * sentence similarity based on a pooling strategy and one
     * pre-traiend WE file.
     * 
     * @param poolingMethod
     * @param embeddingType
     * @param preprocesser
     * @param strPretrainedWEFilename
     * @return 
     * @throws java.io.IOException 
     * @throws java.text.ParseException 
     */
    
    public static ISentenceSimilarityMeasure getSWEMMeasure(
            SWEMpoolingMethod       poolingMethod,
            WordEmbeddingFileType   embeddingType,
            IWordProcessing         preprocesser,
            String                  strPretrainedWEFilename) throws IOException, ParseException
    {
        return (new SimpleWordEmbeddingModelMeasure(poolingMethod,
                embeddingType, preprocesser, strPretrainedWEFilename));
    }
    
    /**
     * This function creates a string-based sentence similarity measure.
     * 
     * @param method
     * @param wordPreprocessing
     * @return 
     */
    
    public static ISentenceSimilarityMeasure getStringBasedMeasure(
        StringBasedSentSimilarityMethod method,
            IWordProcessing             wordPreprocessing)
    {
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = null;
        
        // We creates an instance of the required method
        
        switch (method)
        {
            case BlockDistance:
                
                measure = new BlockDistanceMeasure(wordPreprocessing);
                
                break;
                
            case Jaccard:
                
                measure = new JaccardMeasure(wordPreprocessing);
                
                break;
                
            case Levenshtein:
                
                measure = new LevenshteinMeasure(wordPreprocessing);
                
                break;
                
            case OverlapCoefficient:
                
                measure = new OverlapCoefficientMeasure(wordPreprocessing);
                
                break;
                
            case Qgram:
                
                measure = new QgramMeasure(wordPreprocessing);
                
                break;
        }
        
        // We return the result
        
        return (measure);
    }
    
    /**
     * This function creates a sentence embedding method.
     * 
     * @param method
     * @param wordPreprocessor
     * @param strPretrainedModelFilename
     * @param BERTDir
     * @param PythonVenvDir
     * @param PythonScriptDir
     * @return 
     * @throws java.io.IOException 
     * @throws java.lang.InterruptedException 
     * @throws org.json.simple.parser.ParseException 
     */
    
    public static ISentenceSimilarityMeasure getSentenceEmbeddingMethod(
            SentenceEmbeddingMethod method,
            IWordProcessing         wordPreprocessor,
            String                  strPretrainedModelFilename,
            String                  BERTDir,
            String                  PythonVenvDir,
            String                  PythonScriptDir) throws IOException,
            InterruptedException, org.json.simple.parser.ParseException
    {
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = null;
        
        // We creates an instance of the required method
        
        switch (method)
        {
            case ParagraphVector:
                
                measure = new ParagraphVectorMeasure(strPretrainedModelFilename, wordPreprocessor);
                
                break;
                
            case BertEmbeddingModelMethod:
                
                measure = new BertEmbeddingModelMeasure(
                        strPretrainedModelFilename, 
                        wordPreprocessor,
                        BERTDir,
                        PythonVenvDir,
                        PythonScriptDir);
                
                break;
        }
        
        // We return the result
        
        return (measure);
    }
}
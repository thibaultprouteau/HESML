/* 
 * Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.sts.measures.impl;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.WordEmbeddingFileType;
import hesml.sts.measures.BERTpoolingMethod;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SWEMpoolingMethod;
import hesml.sts.measures.SentenceEmbeddingMethod;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

/**
 * This class builds the sentence similarity measures.
 * @author j.lastra
 */

public class SentenceSimilarityFactory
{
    /**
     * This function creates a Simple Word-Emebedding model for
     * sentence similarity based on a pooling strategy and one
     * pre-traiend WE file.
     * 
     * @param strLabel
     * @param poolingMethod
     * @param embeddingType
     * @param preprocesser
     * @param strPretrainedWEFilename
     * @return 
     * @throws java.io.IOException 
     * @throws java.text.ParseException 
     */
    
    public static ISentenceSimilarityMeasure getSWEMMeasure(
            String                  strLabel,
            SWEMpoolingMethod       poolingMethod,
            WordEmbeddingFileType   embeddingType,
            IWordProcessing         preprocesser,
            String                  strPretrainedWEFilename) 
            throws IOException, ParseException, Exception
    {
        return (new SimpleWordEmbeddingModelMeasure(strLabel, poolingMethod,
                embeddingType, preprocesser, strPretrainedWEFilename));
    }
    
    /**
     * This function creates a string-based sentence similarity measure.
     * 
     * @param strLabel
     * @param method
     * @param wordPreprocessing
     * @return 
     */
    
    public static ISentenceSimilarityMeasure getStringBasedMeasure(
            String                  strLabel,
            StringBasedSentenceSimilarityMethod method,
            IWordProcessing                     wordPreprocessing)
    {
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = null;
        
        // We creates an instance of the required method
        
        switch (method)
        {
            case BlockDistance:
                
                measure = new BlockDistanceMeasure(strLabel, wordPreprocessing);
                
                break;
                
            case Jaccard:
                
                measure = new JaccardMeasure(strLabel, wordPreprocessing);
                
                break;
                
            case Levenshtein:
                
                // We use the default values provided in other state-of-the-art implementations.
                
                measure = new LevenshteinMeasure(strLabel, wordPreprocessing, 1, 1);
                
                break;
                
            case OverlapCoefficient:
                
                measure = new OverlapCoefficientMeasure(strLabel, wordPreprocessing);
                
                break;
                
            case Qgram:
                
                // We use the default values provided in other state-of-the-art implementations.
                
                measure = new QgramMeasure(strLabel, wordPreprocessing, 3);
                
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
     * @param strPretrainedModelDir
     * @return 
     * @throws java.io.IOException 
     * @throws java.lang.InterruptedException 
     * @throws org.json.simple.parser.ParseException 
     */
    
    public static ISentenceSimilarityMeasure getParagraphVectorSentenceMethod(
            String                  strLabel,
            SentenceEmbeddingMethod method,
            IWordProcessing         wordPreprocessor,
            String                  strPretrainedModelFilename,
            String                  strPretrainedModelDir) throws IOException,
            InterruptedException, org.json.simple.parser.ParseException
    {
        // We check the existence of the pre-trained model file
        
        File pretainedModelFileInfo = new File(strPretrainedModelDir + "/" +
                                            strPretrainedModelFilename);
        
        if (!pretainedModelFileInfo.exists())
        {
            throw (new FileNotFoundException(pretainedModelFileInfo.getAbsolutePath()));
        }
            
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = new ParagraphVectorMeasure(
                        strLabel, pretainedModelFileInfo.getAbsolutePath(),
                        wordPreprocessor);

        // We return the result
        
        return (measure);
    }
    
    /**
     * This function creates a sentence embedding method.
     * 
     * @param strLabel
     * @param method
     * @param wordPreprocessor
     * @param strPretrainedModelFilename
     * @param strPretrainedModelDir
     * @param strPythonVirtualEnvironmentDir
     * @param pythonScriptDir
     * @param poolingStrategy
     * @param poolingLayers
     * @return 
     * @throws java.io.IOException 
     * @throws java.lang.InterruptedException 
     * @throws org.json.simple.parser.ParseException 
     */
    
    public static ISentenceSimilarityMeasure getBERTSentenceEmbeddingMethod(
            String                  strLabel,
            SentenceEmbeddingMethod method,
            IWordProcessing         wordPreprocessor,
            String                  strPretrainedModelFilename,
            String                  strPretrainedModelDir,
            String                  strPythonVirtualEnvironmentDir,
            String                  pythonScriptDir,
            BERTpoolingMethod       poolingStrategy,
            String[]                poolingLayers,
            String                  PythonServerPort) throws IOException,
            InterruptedException, org.json.simple.parser.ParseException
    {
        // We check the existence of the pre-trained model file
        
        File pretainedModelFileInfo = new File(strPretrainedModelDir + "/" +
                                            strPretrainedModelFilename);
        
        if (!pretainedModelFileInfo.exists())
        {
            throw (new FileNotFoundException(pretainedModelFileInfo.getAbsolutePath()));
        }
            
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = new BertEmbeddingModelMeasure(
                        strLabel, strPretrainedModelFilename, wordPreprocessor, 
                        strPretrainedModelDir, strPythonVirtualEnvironmentDir,
                        pythonScriptDir, poolingStrategy, poolingLayers, PythonServerPort);

        // We return the result
        
        return (measure);
    }
    
    /**
     * This function creates a USE sentence embedding method.
     * 
     * @param strLabel
     * @param method
     * @param wordPreprocessor
     * @param strModelURL
     * @param pythonScriptFilename
     * @param strPythonVirtualEnvironmentDir
     * @param pythonScriptDir
     * @return 
     * @throws java.io.IOException 
     * @throws java.lang.InterruptedException 
     * @throws org.json.simple.parser.ParseException 
     */
    
    public static ISentenceSimilarityMeasure getUSESentenceEmbeddingMethod(
            String                  strLabel,
            SentenceEmbeddingMethod method,
            IWordProcessing         wordPreprocessor,
            String                  strModelURL,
            String                  pythonScriptFilename,
            String                  strPythonVirtualEnvironmentDir,
            String                  pythonScriptDir) throws IOException,
            InterruptedException, org.json.simple.parser.ParseException
    {  
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = new USEModelMeasure(strLabel,
                            strModelURL, wordPreprocessor, pythonScriptDir, 
                        strPythonVirtualEnvironmentDir, pythonScriptFilename);
        
        // We return the result
        
        return (measure);
    }
    
    /**
     * This function creates a sent2vec sentence embedding method.
     * 
     * @param strLabel
     * @param method
     * @param wordPreprocessor
     * @param strModelPath
     * @param pythonScriptFilename
     * @param strPythonVirtualEnvironmentDir
     * @param pythonScriptDir
     * @return 
     * @throws java.io.IOException 
     * @throws java.lang.InterruptedException 
     * @throws org.json.simple.parser.ParseException 
     */
    
    public static ISentenceSimilarityMeasure getSent2vecMethodMeasure(
            String                  strLabel,
            SentenceEmbeddingMethod method,
            IWordProcessing         wordPreprocessor,
            String                  strModelPath,
            String                  pythonScriptFilename,
            String                  strPythonVirtualEnvironmentDir,
            String                  pythonScriptDir) throws IOException,
            InterruptedException, org.json.simple.parser.ParseException
    {  
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = new Sent2vecModelMeasure(strLabel,
                            strModelPath, wordPreprocessor, pythonScriptDir, 
                        strPythonVirtualEnvironmentDir, pythonScriptFilename);
        
        // We return the result
        
        return (measure);
    }
    
    /**
     * This function creates a WBSM measure.
     * 
     * @param strLabel
     * @param preprocesser
     * @param wordnetTaxonomy
     * @param wordSimilarityMeasureType
     * @param icModelType
     * @param wordnet
     * @return ISentenceSimilarityMeasure
     * @throws java.lang.Exception
     */
    
    public static ISentenceSimilarityMeasure getWBSMMeasure(
            String                  strLabel,
            IWordProcessing         preprocesser,
            IWordNetDB              wordnet,
            ITaxonomy               wordnetTaxonomy,
            SimilarityMeasureType   wordSimilarityMeasureType,
            IntrinsicICModelType    icModelType) throws Exception
    {
        return (new WBSMMeasure(strLabel, preprocesser, wordnet,
                wordnetTaxonomy, wordSimilarityMeasureType, icModelType));
    }
}
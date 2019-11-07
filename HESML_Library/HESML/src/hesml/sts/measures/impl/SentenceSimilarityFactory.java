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
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
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
        return (new SimpleWordEmbeddingModelMeasure(poolingMethod, embeddingType, preprocesser, strPretrainedWEFilename));
    }
    
    /**
     *  This function creates a Jaccard measure object for sentence similarity
     * @param preprocesser
     * @return
     * @throws java.io.IOException
     */
    
    public static ISentenceSimilarityMeasure getJaccardMeasure(
            IWordProcessing     preprocesser) throws IOException
    {
        return (new JaccardMeasure(preprocesser));
    }
    
    /**
     *  This function creates a Qgram measure object for sentence similarity
     * @param preprocesser
     * @return
     * @throws java.io.IOException
     */
    
    public static ISentenceSimilarityMeasure getQgramMeasure(
            IWordProcessing     preprocesser) throws IOException
    {
        return (new QgramMeasure(preprocesser));
    }
    
    /**
     *  This function creates a Block distance similarity measure 
     * object for sentence similarity
     * @param preprocesser
     * @return
     * @throws java.io.IOException
     */
    
    public static ISentenceSimilarityMeasure getBlockDistanceMeasure(
            IWordProcessing     preprocesser) throws IOException
    {
        return (new BlockDistanceMeasure(preprocesser));
    }
    
    /**
     *  This function creates a Overlap coefficient similarity measure 
     * object for sentence similarity
     * @param preprocesser
     * @return
     * @throws java.io.IOException
     */
    
    public static ISentenceSimilarityMeasure getOverlapCoefficientMeasure(
            IWordProcessing     preprocesser) throws IOException
    {
        return (new OverlapCoefficientMeasure(preprocesser));
    }
    
    /**
     *  This function creates a Levenshtein similarity measure 
     * object for sentence similarity
     * @param preprocesser
     * @return
     * @throws java.io.IOException
     */
    
    public static ISentenceSimilarityMeasure getLevenshteinMeasure(
            IWordProcessing     preprocesser) throws IOException
    {
        return (new LevenshteinMeasure(preprocesser));
    }
    
    /**
     *  This function creates a Bert Embedding similarity measure 
     * object for sentence similarity
     * @param modelDirPath
     * @param preprocesser
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.io.FileNotFoundException
     * @throws org.json.simple.parser.ParseException
     */
    
    public static ISentenceSimilarityMeasure getBertEmbeddingModelMeasure(
            String              modelDirPath,
            IWordProcessing     preprocesser) throws IOException, InterruptedException, FileNotFoundException, org.json.simple.parser.ParseException
    {
        return (new BertEmbeddingModelMeasure(modelDirPath, preprocesser));
    }
    
    /**
     *  This function creates a Paragraph Vector similarity measure 
     * object for sentence similarity
     * @param strModelDirPath
     * @param preprocesser
     * @return
     * @throws java.io.IOException
     */
    
    public static ISentenceSimilarityMeasure getParagraphVectorModelMeasure(
            String    strModelDirPath,
            IWordProcessing             preprocesser) throws IOException 
    {
        return (new ParagraphVectorMeasure(strModelDirPath, preprocesser));
    }
}
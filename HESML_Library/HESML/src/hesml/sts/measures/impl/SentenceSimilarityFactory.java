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
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.tokenizers.WordTokenizerMethod;
import java.io.IOException;
import java.text.ParseException;

/**
 * This class builds the senetence similarity measures
 * @author j.lastra
 */

public class SentenceSimilarityFactory
{
    /**
     * This function creates a Simple Word-Emebedding model for
     * sentence similarity based on a pooling strategy and one
     * pre-traiend WE file.
     * @param method
     * @param strPretrainedWEFilename
     * @return 
     */
    
    public static ISentenceSimilarityMeasure getSWEMMeasure(
            SWEMpoolingMethod       poolingMethod,
            WordEmbeddingFileType   embeddingType,
            WordTokenizerMethod     tokenizer,
            boolean                 lowercaseNormalization,
            String                  strPretrainedWEFilename) throws IOException, ParseException
    {
        return (new SimpleWordEmbeddingModelMeasure(poolingMethod,
                embeddingType, tokenizer, lowercaseNormalization,
                strPretrainedWEFilename));
    }
}

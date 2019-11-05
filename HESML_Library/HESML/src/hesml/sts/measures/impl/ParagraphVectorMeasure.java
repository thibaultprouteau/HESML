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
package hesml.sts.measures.impl;

import hesml.measures.impl.MeasureFactory;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.languagemodels.LanguageModelMethod;
import hesml.sts.languagemodels.impl.LanguageModelFactory;
import java.io.IOException;
import org.nd4j.linalg.api.ndarray.INDArray;
import hesml.sts.languagemodels.ILanguageModel;


/**
 *
 * @author alicia
 */
class ParagraphVectorMeasure implements ISentenceSimilarityMeasure
{
    private final String m_strModelDirPath;
    private final IWordProcessing m_preprocesser;
    private ILanguageModel m_model;
    
    /**
     * Constructor with parameters
     * @param sentenceSimilarityMethod
     * @param preprocesser 
     */
    
    ParagraphVectorMeasure(
            String strModelDirPath,
            IWordProcessing preprocesser) throws IOException
    {
        m_strModelDirPath = strModelDirPath;
        m_preprocesser = preprocesser;
        m_model = null;
    }

    @Override
    public SentenceSimilarityMethod getMethod(){return SentenceSimilarityMethod.ParagraphVector;}

    @Override
    public double getSimilarityValue(String strRawSentence1, String strRawSentence2) throws IOException
    {
        double similarity = 0.0; // We initialize the output
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        ILanguageModel model = LanguageModelFactory.loadModel(m_strModelDirPath);
        
        double[] sentence1Vector = model.getVectorFromStrSentence(String.join(" ", lstWordsSentence1));
        double[] sentence2Vector = model.getVectorFromStrSentence(String.join(" ", lstWordsSentence2));

        // We check the validity of the word vectors. They could be null if
        // any word is not contained in the vocabulary of the embedding.
        
        if ((sentence1Vector != null) && (sentence2Vector != null))
        {
            // We compute the cosine similarity function (dot product)
            
            for (int i = 0; i < sentence1Vector.length; i++)
            {
                similarity += sentence1Vector[i] * sentence2Vector[i];
            }
            
            // We divide by the vector norms
            
            similarity /= (MeasureFactory.getVectorNorm(sentence1Vector)
                        * MeasureFactory.getVectorNorm(sentence2Vector));
        }
        
        // We return the result
        
        return (similarity);
    
    }
    
   
}

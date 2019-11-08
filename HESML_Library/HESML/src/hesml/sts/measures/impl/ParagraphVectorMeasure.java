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
import hesml.sts.languagemodels.impl.LanguageModelFactory;
import hesml.sts.languagemodels.ILanguageModel;
import hesml.sts.measures.SentenceSimilarityFamily;

import java.io.IOException;

/**
 *   This class implements the Paragraph Vector model measure.
 * 
 * @author alicia
 */

class ParagraphVectorMeasure extends SentenceSimilarityMeasure
{
    private final String m_strModelDirPath; // Path to the trained model (.zip file)
    private final IWordProcessing m_preprocesser; // Preprocessed configured object
    private final ILanguageModel m_model; // Language Model 
    
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
        
        // Load the model
        
        m_model = LanguageModelFactory.loadModel(m_strModelDirPath);
    }

    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return SentenceSimilarityMethod.ParagraphVector;
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.SentenceEmbedding);
    }
    
    @Override
    public double getSimilarityValue(String strRawSentence1, String strRawSentence2) throws IOException
    {
        double similarity = 0.0; // We initialize the output
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
       
        double[] sentence1Vector = m_model.getVectorFromStrSentence(String.join(" ", lstWordsSentence1));
        double[] sentence2Vector = m_model.getVectorFromStrSentence(String.join(" ", lstWordsSentence2));

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

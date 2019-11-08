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

import hesml.measures.IPretrainedWordEmbedding;
import hesml.measures.WordEmbeddingFileType;
import hesml.measures.impl.MeasureFactory;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SWEMpoolingMethod;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.IOException;
import java.text.ParseException;

/**
 * This function implements a Simple Word-Embedding Model as introduced by
 * the paper [1] below.
 * 
 * [1] D. Shen, G. Wang, W. Wang, M.R. Min, Q. Su, Y. Zhang, C. Li, R. Henao, L. Carin,
 * Baseline Needs More Love: On Simple Word-Embedding-Based Models and Associated
 * Pooling Mechanisms, in: Proc. of the 56th Annual Meeting of the Association
 * for Computational Linguistics (Long Papers), ACL, Melbourne, Australia,
 * 2018: pp. 440–450.
 * 
 * @author j.lastra
 */

class SimpleWordEmbeddingModelMeasure extends SentenceSimilarityMeasure
{
    /**
     * Word emebedding model
     */
    
    private final IPretrainedWordEmbedding    m_WordEmbedding;
    
    /**
     * Specific method implemented
     */
    
    private final SWEMpoolingMethod    m_PoolingMethod;
    
    /**
     * Word preprocesser used to convert the sentence into a string
     * of words.
     */
    
    private final IWordProcessing  m_Preprocesser;
    
    /**
     * Constructor
     * @param embeddingType
     * @param strPretrainedModelFilename 
     */
    
    SimpleWordEmbeddingModelMeasure(
            SWEMpoolingMethod       poolingMethod,
            WordEmbeddingFileType   embeddingType,
            IWordProcessing         preprocesser,
            String                  strPretrainedModelFilename) throws IOException, ParseException
    {
        // We initializer the object
        
        m_PoolingMethod = poolingMethod;
        m_Preprocesser = preprocesser;
        m_WordEmbedding = MeasureFactory.getWordEmbeddingModel(embeddingType,
                            strPretrainedModelFilename);
    }
    
    /**
     * This function returns the type of method implemented by the current
     * sentence similarity measure.
     * @return 
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        // We initialize the output
        
        SentenceSimilarityMethod method = SentenceSimilarityMethod.SWEM_AveragePooling;
        
        // We return the method
        
        switch (m_PoolingMethod)
        {
            case Average:
                
                method = SentenceSimilarityMethod.SWEM_AveragePooling;
                
                break;
                
            case Max:
                
                method = SentenceSimilarityMethod.SWEM_MaxPooling;
                
                break;
                
            case Min:
                
                method = SentenceSimilarityMethod.SWEM_MinPooling;
                
                break;
                
            case Sum:
                
                method = SentenceSimilarityMethod.SWEM_SumPooling;
                
                break;
        }
        
        // We return the result
        
        return (method);
    }
    
    /**
     * This function returns the family of the current sentence similarity method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.WordEmbeddingAggregation);
    }
    
    /**
     * This fucntion returns the similarity value (score) between two
     * raw sentences. Any sentence pre-processing is made by the underlying
     * methods, such as lowercase normalziation, tokenization, etc.
     * @param strRawSentence1
     * @param strRawSentence2
     * @return 
     */
    
    @Override
    public double getSimilarityValue(
            String  strRawSentence1,
            String  strRawSentence2) throws IOException
    {
        // We initialize the output

        double similarity = 0.0;

        // We obtain the words in both input sentences
        
        double[] sentence1Vector = getSentenceEmbedding(strRawSentence1);
        double[] sentence2Vector = getSentenceEmbedding(strRawSentence2);
        
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
    
    /**
     * This function returns the vector representation of the input sentence
     * which is based on a combination of the vectors corresponding to the words
     * in the sentence..
     * @param strRawSentence
     * @return
     * @throws IOException 
     */
    
    private double[] getSentenceEmbedding(
        String  strRawSentence) throws IOException
    {
        // We obtain the words in the input sentence
        
        String[] strWords = m_Preprocesser.getWordTokens(strRawSentence);        

        // We initialize the accumulated word vector
        
        double[] sentenceVector = new double[m_WordEmbedding.getVectorDimension()];
        
        // We obtain the vectors for each word
        
        int nonNullVectorCount = 0;
        
        for (int iWord = 0; iWord < strWords.length; iWord++)
        {
            // We get the next word
            
            String strWord = strWords[iWord];
            
            // We extract the word vector from the pre-trained embedding
            
            if (m_WordEmbedding.ContainsWord(strWord))
            {
                // We obtaion the word vector
                
                double[] wordVector = m_WordEmbedding.getWordVector(strWord);
                
                // We accumulate the result
                        
                for (int i = 0; i < wordVector.length; i++)
                {
                    switch (m_PoolingMethod)
                    {
                        case Average:
                        case Sum:
                            
                            sentenceVector[i] += wordVector[i];
                            
                            break;
                            
                        case Max:
                            
                            sentenceVector[i] = Math.max(sentenceVector[i], wordVector[i]);
                            
                            break;
                            
                        case Min:
                        
                            sentenceVector[i] = Math.min(sentenceVector[i], wordVector[i]);
                            
                            break;
                    }
                }
                
                // We increment the non-null vector count
                
                nonNullVectorCount++;
            }
        }
        
        // For the case of avergae pooling, we divde by the vector count @alicia: if (m_PoolingMethod == Average)¿?
        
        for (int i = 0; i < sentenceVector.length; i++)
        {
            sentenceVector[i] /= (double)nonNullVectorCount;
        }
        
        // We return the result
        
        return (sentenceVector);
    }
}

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
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.languagemodels.impl.LanguageModelFactory;
import hesml.sts.languagemodels.ILanguageModel;
import hesml.sts.measures.SentenceSimilarityFamily;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *   This class implements the Paragraph Vector model measure.
 * 
 *  Le, Quoc, and Tomas Mikolov. 2014. 
 *  “Distributed Representations of Sentences and Documents.” 
 *  In International Conference on Machine Learning, 1188–96. jmlr.org.
 * 
 * @author alicia
 */

class ParagraphVectorMeasure extends SentenceSimilarityMeasure
{
    // Path to the trained model (.zip file)
    
    private final String m_strModelDirPath; 

    // Language Model 
    
    private final ILanguageModel m_pretrainedModel; 
    
    /**
     * label shown in all raw matrix results
     */
    
    private final String m_strLabel;
    
    /**
     * Constructor with parameters
     * @param sentenceSimilarityMethod
     * @param preprocesser 
     */
    
    ParagraphVectorMeasure(
            String          strLabel,
            String          strModelDirPath,
            IWordProcessing preprocesser) throws IOException
    {
        // We initialize the base class
        
        super(preprocesser);
        
        // We save the pre-trained model file path
        
        m_strModelDirPath = strModelDirPath;
        m_strLabel = strLabel;
        
        // Load the model
        
        m_pretrainedModel = LanguageModelFactory.loadModel(m_strModelDirPath);
    }

    /**
     * This function returns the label used to identify the measure in
     * a raw matrix results. This string attribute is set by the users
     * to provide the column header name included in all results generated
     * by this measure. This attribute was especially defined to
     * provide a meaningful name to distinguish the measures based on
     * pre-trained model files.
     * @return 
     */
    
    @Override
    public String getLabel()
    {
        return (m_strLabel);
    }
    
    /**
     * Get the current method.
     * @return 
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.ParagraphVector);
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return SentenceSimilarityFamily
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.SentenceEmbedding);
    }
    
    /**
     *  Get the similarity value between the two sentences 
     *  using the cosine distance between the generated embeddings.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InterruptedException 
     */
    
    @Override
    public double getSimilarityValue(
            String  strRawSentence1, 
            String  strRawSentence2) 
            throws IOException, FileNotFoundException, InterruptedException
    {
        // We initialize the output
        
        double similarity = 0.0; 
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
       
        // Join the words and generate the preprocessed sentences
        
        double[] sentence1Vector = m_pretrainedModel.getVectorFromStrSentence(
                String.join(" ", lstWordsSentence1));
        double[] sentence2Vector = m_pretrainedModel.getVectorFromStrSentence(
                String.join(" ", lstWordsSentence2));

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
     * This function releases all resources used by the measure. Once this
     * function is called the measure is completely disabled.
     */
    
    @Override
    public void clear()
    {
        // We release the paragraph vectors
        
        m_pretrainedModel.unsetParagraphVectors();
        
        // We release the resoruces of the base class
        
        super.clear();
    }
}
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *  Read and evaluate BERT embedding pretrained models
 * @author alicia
 */

class BertEmbeddingModelMeasure implements ISentenceSimilarityMeasure
{
    
    private final String m_strEmbeddingsDirPath;
    private final IWordProcessing m_preprocesser;
    private JSONObject m_embeddings;
    
    /**
     * Constructor
     * @param strModelDirPath
     * @param preprocesser 
     */
    
    BertEmbeddingModelMeasure(
            String              strEmbeddingsDirPath,
            IWordProcessing     preprocesser) throws InterruptedException, IOException, FileNotFoundException, ParseException
    {
        m_strEmbeddingsDirPath = strEmbeddingsDirPath;
        m_preprocesser = preprocesser;
        m_embeddings = null;
        this.loadEmbeddings();
    }

    /**
     * Get the actual method
     * @return 
     */
    
    @Override
    public SentenceSimilarityMethod getMethod(){return SentenceSimilarityMethod.BertEmbeddingModelMeasure;}

    /**
     * Get the similarity value between two sentences
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    @Override
    public double getSimilarityValue(String strRawSentence1, String strRawSentence2) throws FileNotFoundException, IOException
    {
        double similarity = 0;
        
        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        String preprocessedSentence1 = String.join(" ", lstWordsSentence1);
        String preprocessedSentence2 = String.join(" ", lstWordsSentence2);

        double[] sentence1Vector = this.getEmbedding(preprocessedSentence1);
        double[] sentence2Vector = this.getEmbedding(preprocessedSentence2);
        
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
    
    private double[] getEmbedding(String preprocessedSentence)
    {   
        JSONArray jsonArray = (JSONArray) m_embeddings.get(preprocessedSentence);
        double[] embedding = new double[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
           embedding[i] = (Double) jsonArray.get(i);
        }
        return embedding;
    }
    
    
    
    /**
     * Load the sentence embeddings into a JSON object.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParseException 
     */
    
    private void loadEmbeddings() throws FileNotFoundException, IOException, ParseException
    {
        // Init the parser and get the embeddings.
        
        JSONParser parser = new JSONParser();
        JSONObject embeddings = (JSONObject) parser.parse(new FileReader(m_strEmbeddingsDirPath));
        m_embeddings = embeddings;
    }   
}
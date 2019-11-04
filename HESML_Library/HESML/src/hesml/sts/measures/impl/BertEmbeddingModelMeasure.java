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

import hesml.measures.IPretrainedWordEmbedding;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *  Read and evaluate BERT embedding pretrained models
 * @author alicia
 */

class BertEmbeddingModelMeasure implements ISentenceSimilarityMeasure
{
    
    private final String m_strEmbeddingsDirPath;
    private final IWordProcessing m_preprocesser;

    
    /**
     * Constructor
     * @param strModelDirPath
     * @param preprocesser 
     */
    
    BertEmbeddingModelMeasure(
            String              strEmbeddingsDirPath,
            IWordProcessing     preprocesser) throws InterruptedException
    {
        m_strEmbeddingsDirPath = strEmbeddingsDirPath;
        m_preprocesser = preprocesser;
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
    public double getSimilarityValue(String strRawSentence1, String strRawSentence2) throws IOException
    {
        double similarity = 0;
        
        
        try {
            this.getSentenceEmbeddings(m_strEmbeddingsDirPath);
        } catch (ParseException ex) {
            Logger.getLogger(BertEmbeddingModelMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    

    /**
     * This function returns the vector representation of the input sentence
     * which is based on a combination of the vectors corresponding to the words
     * in the sentence..
     * @param strRawSentence
     * @return
     * @throws IOException 
     */
    
    private double[] getSentenceEmbeddings(
        String  strDatasetEmbeddingsFile) throws IOException, ParseException
    {
        // We initialize the vector
        
        double[] sentenceVector = null;
        
//        DataInputStream  reader = new DataInputStream(new FileInputStream(strDatasetEmbeddingsFile));
        JSONParser parser = new JSONParser();
        Object a = parser.parse(new FileReader(strDatasetEmbeddingsFile));
//        for (Object o : a)
//        {
//          JSONObject person = (JSONObject) o;
//
////          String name = (String) person.get("name");
////          System.out.println(name);
////
////          String city = (String) person.get("city");
////          System.out.println(city);
////
////          String job = (String) person.get("job");
////          System.out.println(job);
////
////          JSONArray cars = (JSONArray) person.get("cars");
//
////          for (Object c : cars)
////          {
////            System.out.println(c+"");
////          }
            System.out.println("debug");
//        }
        
        // We return the result
        
        return (sentenceVector);
    }
    
 
    
}

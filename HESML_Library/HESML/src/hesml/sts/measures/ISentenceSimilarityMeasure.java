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

package hesml.sts.measures;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This interface represents the abstract class for all sentence similarity
 * measures.
 * @author j.lastra
 */

public interface ISentenceSimilarityMeasure
{
    /**
     * This function releases all resources used by the measure. Once this
     * function is called the measure is completely disabled.
     */
    
    void clear();
    
    /**
     * This function returns the label used to identify the measure in
     * a raw matrix results. This string attribute is set by the users
     * to provide the column header name included in all results generated
     * by this measure. This attribute was especially defined to
     * provide a meaningful name to distinguish the measures based on
     * pre-trained model files.
     * @return 
     */
    
    String getLabel();
    
    /**
     * This function returns the type of method implemented by the current
     * of sentence similarity measure.
     * @return SentenceSimilarityMethod 
     */
    
    SentenceSimilarityMethod getMethod();
    
    /**
     * This function returns the family of the current sentence similarity method.
     * @return SentenceSimilarityFamily
     */
    
    SentenceSimilarityFamily getFamily();
    
    /**
     * This function returns the similarity value (score) between two
     * raw sentences.Any sentence pre-processing is made by the underlying 
     * methods, such as lowercase normalziation, tokenization, etc.
     * @param strRawSentence1
     * @param strRawSentence2
     * @return double score
     * @throws java.io.IOException 
     * @throws java.lang.InterruptedException 
     */
    
    double getSimilarityValue(
            String  strRawSentence1,
            String  strRawSentence2)  throws IOException,
            FileNotFoundException, InterruptedException, Exception;
    
    /**
     *  Given a list of sentences, calculate the similarity for its inferred vectors.
     * 
     * @param lstSentences1
     * @param lstSentences2
     * @return double[] the list of scores
     * @throws IOException
     * @throws InterruptedException
     */
    
    double[] getSimilarityValues(
            String[] lstSentences1,
            String[] lstSentences2) throws IOException,
            FileNotFoundException, InterruptedException, Exception;
}

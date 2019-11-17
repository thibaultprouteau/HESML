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
package hesml.sts.languagemodels;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;

/**
 * This class creates an interface to train and load language models.
 * @author alicia
 */

public interface ILanguageModel
{
    /**
     * Get the training method
     * @return
     */
    
    LanguageModelMethod getTrainingMethod();
    
    /**
     *  Execute the training method
     * @param strTrainningInputDocumentPath
     * @param strTrainningOutputDocumentPath
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    
    void train(
            String strTrainningInputDocumentPath,
            String strTrainningOutputDocumentPath) throws FileNotFoundException, IOException;
    
    /**
     * This function loads the vectors from a model path
     * 
     * Important: In the future, the output will differ.
     * 
     * @param strParagraphVectorModelPath
     * @return
     * @throws IOException 
     */
    
    ParagraphVectors loadVectors(
            String strParagraphVectorModelPath) throws IOException;
    
    /**
     * This function returns the vector for a sentence.
     * @param strSentence
     * @return 
     */
    
    double[] getVectorFromStrSentence(
            String strSentence);
    
    /**
     * This function set the training method.
     * @param method 
     */
    
    void setTrainingMethod(LanguageModelMethod method);
}

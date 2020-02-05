/* 
 * Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
 *
 * This program is free software for non-commercial use:
 * you can redistribute it and/or modify it under the terms of the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * (CC BY-NC-SA 4.0) as published by the Creative Commons Corporation,
 * either version 4 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * section 5 of the CC BY-NC-SA 4.0 License for more details.
 *
 * You should have received a copy of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0) 
 * license along with this program. If not,
 * see <http://creativecommons.org/licenses/by-nc-sa/4.0/>.
 *
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
     * @return LanguageModelMethod
     */
    
    LanguageModelMethod getTrainingMethod();
    
    /**
     *  Execute the training method.
     * 
     *  @param strTrainningInputDocumentPath
     *  @param strTrainningOutputDocumentPath
     *  @throws java.io.FileNotFoundException
     *  @throws java.io.IOException
     */
    
    void train(
            String strTrainningInputDocumentPath,
            String strTrainningOutputDocumentPath) 
            throws FileNotFoundException, IOException;
    
    /**
     * This function loads the vectors from a model path
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
    
    void setTrainingMethod(
            LanguageModelMethod method);
    
    /**
     * This function set to null the paragraph vectors and clean the memory.
     */
    
    void unsetParagraphVectors();
}

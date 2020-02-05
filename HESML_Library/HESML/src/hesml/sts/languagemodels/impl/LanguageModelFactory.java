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

package hesml.sts.languagemodels.impl;

import hesml.sts.languagemodels.LanguageModelMethod;
import java.io.FileNotFoundException;
import java.io.IOException;
import hesml.sts.languagemodels.ILanguageModel;

/**
 * This class encapsulates the creation of language model objects.
 * 
 *  @author alicia
 */

public class LanguageModelFactory
{
    /**
     *  Constructor of the preprocess pipeline factory
     * 
     * Given a preset preprocess method, configure the preprocessing.
     * 
     * @param method
     * @param strTrainningInputDocumentPath
     * @param strTrainningOutputDocumentPath
     * @return ILanguageModel
     * @throws java.io.FileNotFoundException
     */
    
    public static ILanguageModel executeTraining(
            LanguageModelMethod     method,
            String                  strTrainningInputDocumentPath,
            String                  strTrainningOutputDocumentPath) 
            throws FileNotFoundException, IOException
    {   
        // There is only implemented PV model, so there is not a TrainingModel implementation.
        
        ILanguageModel trainingModel = new ParagraphVectorModel(method);
        trainingModel.train(strTrainningInputDocumentPath, strTrainningOutputDocumentPath);
        
        // Return the trained model
        
        return (trainingModel);
    }
    
    /**
     * This function load the model vectors and return a ILanguageModel object.
     * 
     * @param strTrainedModelDirPath
     * @return
     * @throws IOException 
     */
    
    public static ILanguageModel loadModel(
            String strTrainedModelDirPath) throws IOException
    {
        ILanguageModel trainingModel = new ParagraphVectorModel();
        trainingModel.loadVectors(strTrainedModelDirPath);
        
        // Return the model
        
        return (trainingModel);
    }
    
    /**
     * This function set to null the paragraph vectors and clean the memory.
     * @param trainingModel 
     */
    
    public static void clear(
            ILanguageModel trainingModel)
    {
        trainingModel.unsetParagraphVectors();
    }
}

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
package hesml.sts.languagemodels.impl;

import hesml.sts.languagemodels.LanguageModelMethod;
import java.io.FileNotFoundException;
import java.io.IOException;
import hesml.sts.languagemodels.ILanguageModel;

/**
 *  This class encapsulates the creation of language model objects.
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

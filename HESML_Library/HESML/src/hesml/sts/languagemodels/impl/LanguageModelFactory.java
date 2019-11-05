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
 *
 * @author alicia
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
     * @return 
     * @throws java.io.FileNotFoundException
     */
    
    public static ILanguageModel executeTraining(
            LanguageModelMethod    method,
            String strTrainningInputDocumentPath,
            String strTrainningOutputDocumentPath) throws FileNotFoundException, IOException
    {   
        // There is only implemented PV model, so there is not a TrainingModel implementation.
        
        ILanguageModel trainingModel = new ParagraphVectorModel(method);
        trainingModel.train(strTrainningInputDocumentPath, strTrainningOutputDocumentPath);
        return trainingModel;
    }
    
    public static ILanguageModel loadModel(
            String strTrainedModelDirPath) throws IOException
    {
        ILanguageModel trainingModel = new ParagraphVectorModel();
        trainingModel.loadVectors(strTrainedModelDirPath);
        return trainingModel;
    }
}

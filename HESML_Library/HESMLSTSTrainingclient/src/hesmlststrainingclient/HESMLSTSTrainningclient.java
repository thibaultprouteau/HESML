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

package hesmlststrainingclient;

import hesml.HESMLversion;
import hesml.sts.languagemodels.ILanguageModel;
import hesml.sts.languagemodels.LanguageModelMethod;
import hesml.sts.languagemodels.impl.LanguageModelFactory;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class implements a basic client application of the HESML trainning methods.
 * 
 * @author alicia
 */

public class HESMLSTSTrainningclient
{
    /**
     * Test function.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        
        boolean   showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, October 2019) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        
        // Test functions
        
        // @todo create automatically the 
        
        String strTrainningInputDocumentPath = "../BioCCorpus/BioC_sentencesSplitted_D0/allSentencesInAFile.txt";
        String strTrainningOutputDocumentPath = "/home/alicia/Desktop/HESML/HESML_Library/STSTrainedModels/ParagraphVectorDM/vectors.zip";
        
        ILanguageModel model = testParagraphVectorDMModelTraining(
                strTrainningInputDocumentPath, 
                strTrainningOutputDocumentPath);
    }
    
    /**
     * Test function for train the paragraph vector example
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ILanguageModel testParagraphVectorDMModelTraining(
            String strTrainningInputDocumentPath,
            String strTrainningOutputDocumentPath) throws FileNotFoundException, IOException
    {
        
        // Create the paragraph vector model and execute the training.
        
        ILanguageModel model = LanguageModelFactory.executeTraining(
                LanguageModelMethod.ParagraphVectorDM,
                strTrainningInputDocumentPath, 
                strTrainningOutputDocumentPath);
        return model;
    }
}
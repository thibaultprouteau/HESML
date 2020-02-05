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
import java.io.IOException;

/**
 * This class implements a basic client application of 
 * the HESML Sentence Embeddingstrainning methods.
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
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, February 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));

        
        // We read the incoming parameters and load the input and output files.
        
        if ((args.length > 0))
        {
            // Read the arguments and start the training.
            
            String strTrainningInputDocumentPath = args[0];  // Get the input file path
            String strTrainningOutputDocumentPath = args[1];  // Get the output file path

            // Create the paragraph vector model and execute the training.
        
            ILanguageModel model = LanguageModelFactory.executeTraining(
                    LanguageModelMethod.ParagraphVectorDM,
                    strTrainningInputDocumentPath, 
                    strTrainningOutputDocumentPath);
        }
        else
        {
            showUsage = true;
        }
        
        // For a wrong calling to the program, we show the usage.
        
        if (showUsage)
        {
            System.err.println("\nIn order to properly use the HESMLSTSTrainingclient program");
            System.err.println("you should call it using any of the two methods shown below:\n");
            System.err.println("(1) C:> java -jar dist\\HESMLSTSTrainingclient.jar <inputFilePath.txt> <outputFilePath.zip>");
        }
    }
}
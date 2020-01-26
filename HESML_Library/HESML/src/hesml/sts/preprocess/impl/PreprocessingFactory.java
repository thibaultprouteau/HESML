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

package hesml.sts.preprocess.impl;

import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import java.io.IOException;

/**
 * The aim of this class is to instantiate the preprocessing pipeline
 * in order to hide the implementation classes to the client code.
 * 
 * @author alicia
 */

public class PreprocessingFactory
{
    /**
     *  Constructor of the preprocess pipeline factory
     * 
     * @param stopWordFileName
     * @param tokenizerType
     * @param lowercaseNormalization
     * @param charFilteringType
     * @param metamapAnnotation
     * @return 
     * @throws java.io.IOException
     */
    
    public static IWordProcessing getWordProcessing(
            String              stopWordFileName,
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            CharFilteringType   charFilteringType,
            boolean             metamapAnnotation) throws IOException
    {   
        return (new WordProcessing(tokenizerType,lowercaseNormalization,
                stopWordFileName, charFilteringType, metamapAnnotation));
    }
    
    
    /**
     *  Constructor of the preprocess pipeline factory with Python wrapper for BERT.
     * 
     * @param stopWordFileName
     * @param tokenizerType
     * @param lowercaseNormalization
     * @param charFilteringType
     * @param tempDir
     * @param modelDirPath
     * @param pythonVirtualEnvironmentDir
     * @param pythonScriptDir
     * @param metamapAnnotation
     * @return 
     * @throws java.io.IOException
     */
    
    public static IWordProcessing getWordProcessing(
            String              stopWordFileName,
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            CharFilteringType   charFilteringType,
            String              tempDir,
            String              pythonVirtualEnvironmentDir,
            String              pythonScriptDir,
            String              modelDirPath,
            boolean             metamapAnnotation) throws IOException
    {   
        return (new WordProcessing(tokenizerType, lowercaseNormalization,
                stopWordFileName, charFilteringType, tempDir,
                pythonVirtualEnvironmentDir, pythonScriptDir, modelDirPath, metamapAnnotation));
    }    
}
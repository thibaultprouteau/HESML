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

package hesml_umls_benchmark.snomedproviders;

import hesml_umls_benchmark.ISnomedSimilarityLibrary;
import hesml_umls_benchmark.SnomedBasedLibrary;

/**
 * This class implements a factory of SNOMED provider objects which
 * encapsulates a SNOMED-based semantic similarity measure.
 * @author j.lastra
 */

public class SnomedLibraryFactory
{
    /**
     * This fucntion creates a specific library wrapper.
     * @param library
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @return
     * @throws Exception 
     */
    
    public static ISnomedSimilarityLibrary getLibrary(
            SnomedBasedLibrary  libraryType,
            String              strSnomedDir,
            String              strSnomedDBconceptFileName,
            String              strSnomedDBRelationshipsFileName,
            String              strSnomedDBdescriptionFileName,
            String              strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We initialize the output
        
        ISnomedSimilarityLibrary library = null;
        
        // We cretae the warpper for each library being evaliated
        
        switch (libraryType)
        {
            case HESML:
                
                library = new HESMLSimilarityLibrary(strSnomedDir,
                            strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName,
                            strSnomedDBdescriptionFileName,
                            strSNOMED_CUI_mappingfilename);
                
                break;
                
            case SML:
                
                library = new SMLSimilarityLibrary(strSnomedDir,
                            strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName,
                            strSnomedDBdescriptionFileName);
                
                break;
                
            case UMLS_SIMILARITY:
                
                throw (new Exception("Nor impleented"));
        }
        
        // We return the result
        
        return (library);
    }
}

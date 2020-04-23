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

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;
import hesml_umls_benchmark.ISnomedSimilarityLibrary;
import hesml_umls_benchmark.SnomedBasedLibraryType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * * This class implements the SNOMED similarity library based on UMLS::Similarity.
 * @author alicia
 */

class UMLSSimilarityLibrary extends SnomedSimilarityLibrary
        implements ISnomedSimilarityLibrary
{
    /**
     * SNOMED database
     */
    
    private ISnomedCtDatabase   m_hesmlSnomedDatabase;
    
    /**
     * Vertexes contained in the HESML taxonomy encoding SNOMED
     */
    
    private IVertexList m_hesmlVertexes;
   
    private IntrinsicICModelType    m_icModel;
    private SimilarityMeasureType   m_measureType;

    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @throws Exception 
     */
    
    UMLSSimilarityLibrary() throws Exception
    {
        // Initialize base class
        
        super();   
        
        // We initialize the object
        
        m_hesmlSnomedDatabase = null;
    }
    
        /**
     * This function calculates the similarity given a list of CUI pairs. 
     * 
     * @param umlsCuiPairs
     * @param strfirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return
     * @throws Exception 
     */

    @Override
    public double[][] getSimilarity(String[][] umlsCuiPairs) throws Exception 
    {
        // Initialize the result
        
        double[][] similarity = new double[umlsCuiPairs.length][2];
        
        similarity[0][0] = 1;
        similarity[0][1] = 1.5;
        similarity[1][0] = 3;
        similarity[1][1] = 3.5;
        
        String temporalFile = "../UMLS_Similarity_Perl/tempFile.csv";
        this.writeCSVfile(umlsCuiPairs, temporalFile);

        
        
        // Return the result
        
        return similarity;
    }
    
    /**
     * This function returns the degree of similarity between two
     * SNOMED-CT concepts.
     * @param word1
     * @param word2
     * @return 
     */

    @Override
    public double getSimilarity(
            String    str_firstCUID,
            String    str_secondCUID)  throws Exception
    {
        // We evaluate the similarity measure
        
        double similarity = 0.0;
        
//        similarity = test_queryInputStream(str_firstCUID, str_secondCUID);
        similarity = test_perlPredefinedScript(str_firstCUID, str_secondCUID);
//        similarity = test_queryNotInputStream(str_firstCUID, str_secondCUID);
        
        // We return the result
        
        return (similarity);
    }
    
//    /**
//     * FUNCTION FOR TESTING PURPOSES ONLY. REMOVE.
//     * 
//     * Get the similarity using my own script.
//     *  -> The Perl script waits for cuis codes and print similarity
//     *  -> Load at once the Perl dependencies and then calculates the sim.
//     * 
//     * Working: NOTOK - Problems with 
//     * 
//     * 
//     * @param str_firstCUID
//     * @param str_secondCUID
//     * @return 
//     */
//    
//    private double test_queryInputStream(
//            String str_firstCUID,
//            String str_secondCUID) throws InterruptedException, IOException
//    {
//        //Initialize the result
//        
//        double similarity = 0;
//        
//        // The proccess is working, waiting for an user input
//        
//        System.out.print("Sending CUIs to the input buffer..." + str_firstCUID + " " + str_secondCUID);
//        PrintWriter printer = new PrintWriter(m_process.getOutputStream());
//        printer.print(str_firstCUID + "-" + str_secondCUID);
//        printer.close();
//
//        // Wait for the Perl process
//        
//        m_process.waitFor();
//
//        // Read the results    
//       
//        BufferedReader reader = new BufferedReader(new InputStreamReader(m_process.getInputStream()));
//        String line;
//        line = reader.readLine();
//        if (line == null) {
//            System.out.print("ERROR");
//        }
//        if (line.contains(str_firstCUID) && line.contains(str_secondCUID)) 
//        {
//            String[] split = line.split("<>");
//            similarity = Double.parseDouble(split[1]);
//        }
//        else
//        {
//            System.out.print("ERROR");
//        }
//        
//        // Return the result
//        
//        return similarity;
//    }
//    
//    /**
//     * FUNCTION FOR TESTING PURPOSES ONLY. REMOVE.
//     * 
//     * Get the similarity using my own script.
//     *  -> The Perl script waits for cuis codes and print similarity
//     *  -> Load every time the Perl dependencies and then calculates the sim.
//     * 
//     * Working: NOTOK - Problems with 
//     * 
//     * 
//     * @param str_firstCUID
//     * @param str_secondCUID
//     * @return 
//     */
//    
//    private double test_queryNotInputStream(
//            String str_firstCUID,
//            String str_secondCUID) throws InterruptedException, IOException
//    {
//        // Initialize the result
//        
//        double similarity = 0;
//        
//        // Initialize the execution time from Perl
//        
//        String execution_time = "";
//        
//        // Create the command line for Perl
//        
//        String perl_path = "perl "; // default to linux
//
//        String cmd = perl_path + "/home/alicia/HESML_UMLS/HESML_Library/UMLS_Similarity_Perl/getSimilarityFromCUIS.t "
//                + str_firstCUID + " " + str_secondCUID;
//        
//        // System.out.println(cmd);
//        
//        m_process = Runtime.getRuntime().exec(cmd);
//        
//        // Wait for the Perl process result
//        
//        m_process.waitFor();
//        
//        // Read the similarity value from the buffer
//        
//        BufferedReader reader = new BufferedReader(new InputStreamReader(m_process.getInputStream()));
//        String line;
//        line = reader.readLine();
//        if (line == null) {
//            System.out.print("ERROR");
//        }
//        if (line.contains(str_firstCUID) && line.contains(str_secondCUID)) 
//        {
//            String[] split = line.split("<>");
//            similarity = Double.parseDouble(split[1]);
//            // execution_time = split[2];
//        }
//        
//        // Return the result
//        
//        return similarity;
//    }
    
    /**
     * FUNCTION FOR TESTING PURPOSES ONLY. REMOVE.
     * 
     * Get the similarity using the similarity perl script.
     * 
     * - Working OK
     * - Problem:
     *  (a) VERY SLOWLY - Loads every time the configuration...
     * 
     * @param firstConceptSnomedID
     * @param secondConceptSnomedID
     * @return
     * @throws Exception 
     */
    
    private double test_perlPredefinedScript(
            String str_firstCUID,
            String str_secondCUID)  throws Exception
    {
        //Initialize the result
        
        double similarity = 0;
        
        // Initialize and execute the Perl command
        
        String perl_path = "perl /"; // default to linux

        String cmd = perl_path
                + "home/alicia/Desktop/UMLS-Similarity-1.47/utils/umls-similarity.pl "
                + "--measure lin "
                + "--realtime "
                + "--config /home/alicia/HESML_UMLS/HESML_Library/UMLS_Similarity_Perl/measures.config "
                + str_firstCUID + " " + str_secondCUID;
        Process process = Runtime.getRuntime().exec(cmd);
        
        // Wait for the Perl process

        process.waitFor();

        // Read the results

        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            //System.out.println(line);
            if (line.contains(str_firstCUID) && line.contains(str_secondCUID)) {
                String[] split = line.split("<>");
                try {
                    similarity = Double.parseDouble(split[0]);
                    System.out.println(similarity);
                } catch (NumberFormatException e) {
                }
            }
        }
        
        // Return the results 
        
        return similarity;
    }
    
    /**
     * FUNCTION FOR TESTING PURPOSES ONLY. REMOVE.
     * 
     * Get the similarity using the web interface.
     * 
     * - Working OK
     * - Problem:
     *  (a) VERY SLOWLY
     *  (b) Not working with SNOMED-CT only.
     * 
     * @param firstConceptSnomedID
     * @param secondConceptSnomedID
     * @return
     * @throws Exception 
     */
    
    private double test_webInterface(
            long    firstConceptSnomedID,
            long    secondConceptSnomedID)  throws Exception
    {
        // Initialize the result
        
        double similarity = 0;
        
        // Create and execute the perl command
        
        String perl_path = "perl /"; // default to linux

        String cmd = perl_path
                + "home/alicia/Desktop/UMLS-Similarity-1.47/utils/"
                + "query-umls-similarity-webinterface.pl --measure cdist "
                + "head" + " " + "foot";
        Process process = Runtime.getRuntime().exec(cmd);
        
        // Wait the Perl process

        process.waitFor();

        // Read the result
        
        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            if (line.contains("head") && line.contains("foot")) {
                String[] split = line.split("<>");
                try {
                    similarity = Double.parseDouble(split[0]);
                    System.out.println(similarity);
                } catch (NumberFormatException e) {
                }
            }
        }
        
        // Return the result
        
        return similarity;
    }
    
    /**
     * We release the resources associated to this object
     */
    
    @Override
    public void clear()
    {
        unloadSnomed();
    }

    /**
     * This function returns the library type
     * @return 
     */
    
    @Override
    public SnomedBasedLibraryType getLibraryType()
    {
        return (SnomedBasedLibraryType.UMLS_SIMILARITY);
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     */
    
    @Override
    public void setSimilarityMeasure(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType) throws Exception
    {
        m_icModel = icModel;
        m_measureType = measureType;
    }
    
    /**
     * Load the SNOMED database
     */
    
    @Override
    public void loadSnomed() throws Exception
    {
        // We load the SNOMED database and get the vertex list of its taxonomy
        String a = "empty method";
    }
    
    /**
     * Unload the SNOMED databse
     */
    
    @Override
    public void unloadSnomed()
    {
        if (m_hesmlSnomedDatabase != null) m_hesmlSnomedDatabase.clear();
    }
    
    /**
     * This function saves an output data matrix int oa CSV file
     * @param strDataMatrix 
     * @param strOutputFilename 
     */
    
    protected void writeCSVfile(
            String[][]  strDataMatrix,
            String      strOutputFilename) throws IOException
    {
        // We create a writer for the text file
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(strOutputFilename, false));
        
        // We write the info for each taxonomy node
        
        char sep = ';';  // Separator dield
        
        for (int iRow = 0; iRow < strDataMatrix.length; iRow++)
        {
            // Avoid blank in first line
            
            String strLine = "\n" + strDataMatrix[iRow][0];
            
            if(iRow == 0)
            {
                strLine = strDataMatrix[iRow][0];
            }
                
            // We initialize the line
            
            
            
            // We build the row
            
            for (int iCol = 1; iCol < strDataMatrix[0].length; iCol++)
            {
                strLine += (sep + strDataMatrix[iRow][iCol]);
            }
            
            // We write the line
            
            writer.write(strLine);
        }
        
        // We close the file
        
        writer.close();
    }
}
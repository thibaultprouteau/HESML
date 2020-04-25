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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
    
    private final ISnomedCtDatabase   m_hesmlSnomedDatabase;
    
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
    public double[][] getSimilarity(String[][] umlsCuiPairs) throws FileNotFoundException, IOException, InterruptedException 
    {
        // Initialize the result
        
        double[][] similarity = new double[umlsCuiPairs.length][2];
        
        // We write the temporal file with all the CUI pairs
        
        String temporalFile = "../UMLS_Similarity_Perl/tempFile.csv";
        this.writeCSVfile(umlsCuiPairs, temporalFile);
        
        // Get the measure as Perl script input format

        String measure = "";
        
        switch (this.m_measureType) 
        {
            case Lin:
                measure = "lin";
                break;
            case FastRada:
                measure = "cdist";
                break;
            case WuPalmer:
                measure = "wup";
                break;
        }
        
        this.executePerlScript(measure);
        
        // We read the output from the Perl script
        
        String row = "";
        BufferedReader csvReader = new BufferedReader(new FileReader("../UMLS_Similarity_Perl/tempFileOutput.csv"));
        
        for (int i = 0; i < umlsCuiPairs.length; i++)
        {
            row = csvReader.readLine();
            String[] data = row.split(",");
            similarity[i][0] = Double.valueOf(data[2]);
            similarity[i][1] = Double.valueOf(data[3]);
        }
        
        csvReader.close();
        
        // Return the result
        
        return similarity;
    }
   
    /**
     * Execute the Perl script.
     * This function executes the script that call the UMLS::Similarity library 
     * and writes in an output file the result.
     */
    
    private void executePerlScript(
            String measure) throws InterruptedException, IOException
    {
        // Create the command line for Perl
        
        String perl_path = "perl "; // default to linux

        String cmd = perl_path + "../UMLS_Similarity_Perl/getSimilarityFromCUIS.t "
                + measure;
        
        System.out.println("Executing the Perl script for calculating UMLS::Similarity");
        System.out.println(cmd);
        
        // Execute the script
        
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        bw.write("Perlumls2020\n"); 
        bw.flush();
        
        // Wait for the Perl process result
        
        process.waitFor();
        
        System.out.println("Finished the execution of the Perl script");
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

    @Override
    public double getSimilarity(String strfirstUmlsCUI, String strSecondUmlsCUI) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
/*
 * Copyright (C) 2019 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.sts.benchmarks.impl;

import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class implements a sentence similarity benchmark using a normalised
 * dataset with the following line-based file format:
 * first sentence \t second sentence \t human similarity judgement (score)
 * @author j.lastra
 */

class SentenceSimilaritySingleBenchmark implements ISentenceSimilarityBenchmark
{
    /**
     * Output filename.
     */
    
    private String  m_strOutputFilename;
    
    /**
     * Dataset reader
     */
    
    private SentenceSimilarityDataset   m_Dataset;
    
    /**
     * Collection of sentence similarity measures to being evaluated
     */
    
    private ISentenceSimilarityMeasure[] m_Measures;
    
    /**
     * Constructor
     * @param strDatasetFilename
     * @param strOutputFilename 
     */
    
    SentenceSimilaritySingleBenchmark(
            ISentenceSimilarityMeasure[]    measures,
            String                          strDatasetDirectory,
            String                          strDatasetFilename,
            String                          strOutputFilename) throws Exception
    {
        // We store the setup objects
        
        m_Measures = measures;
        m_strOutputFilename = strOutputFilename;
        
        // We create the reader and manager of the dataset
        
        m_Dataset = new SentenceSimilarityDataset(strDatasetDirectory
                        + "/" + strDatasetFilename);
    }
    
    /**
     * This function releases all the resources used by the object.
     */
    
    @Override
    public void clear()
    {
        // We release the resoruces used by the measures
        
        for (ISentenceSimilarityMeasure measure : m_Measures)
        {
            measure.clear();
        }
    }
    
    /**
     * This function returns the output filename of the benchmark.
     * @return Output filename
     */
    
    @Override
    public String  getDefaultOutputFilename()
    {
        return (m_strOutputFilename);
    }
    
    /**
     * This function executes the test and save the raw similarity values into
     * the output CSV file.
     * @param strMatrixResultsFile CSV file path containing the results
     * @param showDebugInfo The benchmark shows the count of the current sentence pair being evaluated.
     * @throws java.lang.Exception 
     */
    
    @Override
    public void evaluateBenchmark(
            boolean showDebugInfo) throws Exception
    {
        // We create the similarity matrix containing the similiarity
        // values returned by each measure for each sentence pair.
        
        double[][] similarityMatrix = new double[m_Dataset.getPairsCount()][m_Measures.length + 1];
        
        // We evaluate all similarity measures
        
        for (int iPair = 0; iPair < m_Dataset.getPairsCount(); iPair++)
        {
            // We copy the human similarity judgement in the first column
            
            similarityMatrix[iPair][0] = m_Dataset.getHumanJudgementAt(iPair);
            
            // We evaluate all measures with the sentence pair
            
            for (int iMeasure = 0; iMeasure < m_Measures.length; iMeasure++)
            {
                // We get the next sentece pair
                
                String[] sentences = m_Dataset.getSentencePairAt(iPair);
                
                // We evaluate the measure
                
                similarityMatrix[iPair][iMeasure + 1] =
                        m_Measures[iMeasure].getSimilarityValue(sentences[0], sentences[1]);
            }
        }
        
        // We save the raw similairy values into the output file
        
        writeCSVfile(similarityMatrix, m_strOutputFilename);
    }
        
    /**
     * This function writes the raw similarity matrix into a CSV file
     * @param strMatrix 
     */
    
    protected static void writeCSVfile(
            double[][]  strMatrix,
            String      strOutputFile) throws IOException
    {
        // We open for writing the file
        
        FileWriter writer = new FileWriter(strOutputFile, false);

        // We write the matrix in row mode
        
        for (int i = 0; i < strMatrix.length; i++)
        {
            // We write a full row
            
            for (int j = 0; j < strMatrix[0].length; j++)
            {
                if (j > 0) writer.write(";");
                
                writer.write(Double.toString(strMatrix[i][j]));
            }
            
            // Write the end of line
            
            writer.write("\n");
        }
        
        // We close the file
        
        writer.close();
    }
}

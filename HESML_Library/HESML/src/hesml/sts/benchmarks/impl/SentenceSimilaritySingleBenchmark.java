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
        
    }
}

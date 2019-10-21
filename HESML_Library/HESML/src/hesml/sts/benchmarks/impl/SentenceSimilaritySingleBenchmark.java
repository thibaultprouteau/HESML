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

/**
 * This class implements a sentence similarity benchmark using a normalized
 * dataset.
 * @author j.lastra
 */

class SentenceSimilaritySingleBenchmark implements ISentenceSimilarityBenchmark
{
    /**
     * Output filename.
     */
    
    private String  m_strOutputFilename;
    
    /**
     * Constructor
     * @param strNormalizedBenchmarkFilename
     * @param strOutputFilename 
     */
    
    SentenceSimilaritySingleBenchmark(
            String  strNormalizedBenchmarkFilename,
            String  strOutputFilename)
    {
        
    }
    
    /**
     * This function releases all the resources used by the object.
     */
    
    @Override
    public void clear()
    {
        
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

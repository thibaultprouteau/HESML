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
 * This class builds the instances of sentence similarity benchmarks.
 * @author j.lastra
 */

public class SentenceSimBenchmarkFactory
{
    /**
     * This function returns an instance of a single-dataset sentence
     * similarity benchmark.
     * @param strNormalizedBenchmarkFilename
     * @param strOutputFilename
     * @return 
     */
    
    public static ISentenceSimilarityBenchmark getSingleDatasetBenchmark(
            String  strNormalizedBenchmarkFilename,
            String  strOutputFilename)
    {
        return (new SentenceSimilaritySingleBenchmark(
                strNormalizedBenchmarkFilename, strOutputFilename));
    }
}

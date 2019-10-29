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
package hesml.sts.preprocess;

/**
 * Enumeration for selecting the different pipelines in the preprocessing step.
 * 
 * @author Alicia Lara-Clares
 */

public enum PreprocessType {
    
    /**
     *  Implements the default preprocessing method
     */
    
    DefaultJava,
    
    /**
     * Preprocessing described in BIOSSES:
     * 
     * Sogancioglu, Gizem, Hakime Öztürk, and Arzucan Özgür. 2017. 
     * “BIOSSES: A Semantic Sentence Similarity Estimation System 
     * for the Biomedical Domain.” Bioinformatics  33 (14): i49–58.
     */
    
    Biosses2017,
    
    /**
     * Preprocessing described in BIOSSES2017 *:
     * 
     * Sogancioglu, Gizem, Hakime Öztürk, and Arzucan Özgür. 2017. 
     * “BIOSSES: A Semantic Sentence Similarity Estimation System 
     * for the Biomedical Domain.” Bioinformatics  33 (14): i49–58.
     * 
     * * Preprocessing from the original code - evaluation tasks: They do NOT
     * remove the stop words
     */
    
    Biosses2017_withStopWords,
    
    /**
     *  Preprocessing as described in Blagec2019:
     * 
     * Blagec, Kathrin, Hong Xu, Asan Agibetov, and Matthias Samwald. 2019. 
     * “Neural Sentence Embedding Models for Semantic Similarity 
     * Estimation in the Biomedical Domain.” BMC Bioinformatics 20 (1): 178.
     */
    
    Blagec2019
    

    
}

/*
 * Copyright (C) 2019 j.lastra
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

package hesml.sts.measures;

/**
 * This enumeration groups all of sentence similarity methods.
 * 
 * @author j.lastra
 */

public enum SentenceSimilarityMethod
{
    /**
     * Simple Word Embedding Model Measure by AveragePooling
     */
    
    SWEM_AveragePooling,

    /**
     * Simple Word Embedding Model Measure by MaxPooling
     */
    
    SWEM_MaxPooling,

    /**
     * Simple Word Embedding Model Measure by MinPooling
     */
    
    SWEM_MinPooling,

    /**
     * Simple Word Embedding Model Measure by SumPooling
     */
    
    SWEM_SumPooling,
    
    /**
     * JACCARD, and P. 1908. “Nouvelles Recherches Sur La Distribution Florale.” 
     * Bulletin de La SociÃ©tÃ© Vaudoise Des Sciences Naturelles 44: 223–70.
     */
    
    Jaccard,
    
    /**
     * Ukkonen, Esko. 1992. “Approximate String-Matching with Q-Grams 
     * and Maximal Matches.” Theoretical Computer Science 92 (1): 191–211.
     */
    
    Qgram,
    
    /**
     * Krause, Eugene F. 1986. Taxicab Geometry: 
     * An Adventure in Non-Euclidean Geometry. Courier Corporation.
     */
    
    BlockDistance,
    
    /**
     * Lawlor, Lawrence R. 1980. “Overlap, Similarity, 
     * and Competition Coefficients.” Ecology 61 (2): 245–51.
     */
    
    OverlapCoefficient,
    
    /**
     * Levenshtein, Vladimir I. 1966. “Binary Codes Capable of 
     * Correcting Deletions, Insertions, and Reversals.” 
     * In Soviet Physics Doklady, 10:707–10. nymity.ch.
     */
    
    Levenshtein,
    
    /**
     * Peng, Yifan, Shankai Yan, and Zhiyong Lu. 2019. 
     * “Transfer Learning in Biomedical Natural Language Processing: 
     * An Evaluation of BERT and ELMo on Ten Benchmarking Datasets.” 
     * arXiv [cs.CL]. arXiv. http://arxiv.org/abs/1906.05474.
     */
    
    BertEmbeddingModelMeasure,
    
    /**
     * Le, Quoc, and Tomas Mikolov. 2014. 
     * “Distributed Representations of Sentences and Documents.” 
     * In International Conference on Machine Learning, 1188–96. jmlr.org.
     */
    
    ParagraphVector,
    
    /**
     * Sogancioglu, Gizem, Hakime Öztürk, and Arzucan Özgür. 2017. 
     * “BIOSSES: A Semantic Sentence Similarity Estimation System 
     * for the Biomedical Domain.” Bioinformatics  33 (14): i49–58.
     */
    
    WBSMMeasure
}

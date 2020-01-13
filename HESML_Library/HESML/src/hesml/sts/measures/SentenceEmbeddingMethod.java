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
 * This enumeration groups all sentence embedding methods.
 * @author j.lastra
 */

public enum SentenceEmbeddingMethod
{
    /**
     * This method evaluates all the pre-trained BERT-based models.
     */
    
    BERTEmbeddingModel,
    
    /**
     * Cer, Daniel, Yinfei Yang, Sheng-Yi Kong, Nan Hua, Nicole Limtiaco, 
     * Rhomni St. John, Noah Constant, et al. 2018. 
     * “Universal Sentence Encoder.” arXiv [cs.CL]. 
     * arXiv. http://arxiv.org/abs/1803.11175.
     */
    
    USEModel,
    
    /**
     * Le, Quoc, and Tomas Mikolov. 2014. 
     * “Distributed Representations of Sentences and Documents.” 
     * In International Conference on Machine Learning, 1188–96. jmlr.org.
     */
    
    ParagraphVector
}

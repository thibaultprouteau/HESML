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
 *  Enumeration class for selecting the different methods 
 * for tokenizing the sentences.
 * @author alicia
 */

public enum TokenizerType
{
    /**
     * Tokenize a text splitting by whitespaces.
     */
    
    WhiteSpace,
    
    /**
     * Tokenize a text using the external Stanford CoreNLP library.
     * 
     * Stanford Core NLP use an extended PTB-style tokenizer
     * (A fast, rule-based tokenizer implementation, 
     * which produces Penn Treebank style tokenization of English text)
     * 
     * Manning, Christopher, Mihai Surdeanu, John Bauer, Jenny Finkel, 
     * Steven Bethard, and David McClosky. 2014. 
     * “The Stanford CoreNLP Natural Language Processing Toolkit.” 
     * In Proceedings of 52nd Annual Meeting of the Association 
     * for Computational Linguistics: System Demonstrations, 55–60. aclweb.org.
     * 
     * Stanford CoreNLP can detect NER entities in general domain.
     */
    
    StanfordCoreNLPv3_9_1,
    
    /**
     * Tokenize text using the BERT approximation.
     * 
     * Devlin, Jacob, Ming-Wei Chang, Kenton Lee, and Kristina Toutanova. 2018. 
     * “BERT: Pre-Training of Deep Bidirectional Transformers for 
     * Language Understanding.” arXiv [cs.CL]. arXiv. 
     * http://arxiv.org/abs/1810.04805.
     */
    
    WordPieceTokenizer,
    
    /**
     * Tokenize the text using the BioC NLP Tokenizer.
     * 
     * Comeau, Donald C., Rezarta Islamaj Doğan, Paolo Ciccarese, 
     * Kevin Bretonnel Cohen, Martin Krallinger, Florian Leitner, 
     * Zhiyong Lu, et al. 2013. “BioC: A Minimalist Approach to Interoperability 
     * for Biomedical Text Processing.” Database: The Journal of Biological 
     * Databases and Curation 2013 (September): bat064.
     */
    
    BioCNLPTokenizer
}

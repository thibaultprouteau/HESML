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

package hesmlstsclient;

import hesml.HESMLversion;
import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesml.sts.measures.BERTpoolingMethod;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceEmbeddingMethod;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.measures.impl.SentenceSimilarityFactory;
import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import hesml.sts.preprocess.impl.PreprocessingFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.parser.ParseException;

/**
 * This class implements a basic client application of the HESML similarity
 * measures library introduced in the paper below.
 * 
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * Submitted for publication in Information Systems Journal.
 * 
 * @author j.lastra
 */

public class HESMLSTSMeasuresEvaluatorclient
{
    /**
     * Resources directories.
     * 
     * m_strBaseDir: the base directory with the resources
     * m_strStopWordsDir: Subdirectory with all the stop words files
     * m_BERTDir: Subdirectory for all the BERT-based experiments 
     * m_strBERTPretrainedModelsDir: Subdirectory with all the pretrained models for BERT
     * m_PythonVenvExecutable: Path to the Python executable virtual environment
     * m_PythonBERTWrapperScript: Path to the Python script that extracts the inferred vectors for a list of sentences
     * m_PythonWordPieceTokenizerWrapperScript: Path to the Python script for word piece tokenize the texts.
     */
    
    private static final String  m_strBaseDir = "../";
    private static final String  m_strStopWordsDir = m_strBaseDir + "StopWordsFiles/";
    private static final String  m_BERTDir = m_strBaseDir + "BERTExperiments/";
    
    private static final String  m_strBERTPretrainedModelsDir = m_BERTDir + "PretrainedModels/";
    
    private static final String  m_PythonVenvExecutable = m_BERTDir + "venv/bin/python3";
    private static final String  m_PythonBERTWrapperScript = m_BERTDir + "extractBERTvectors.py";
    private static final String  m_PythonWordPieceTokenizerWrapperScript = m_BERTDir + "WordPieceTokenization.py";
    
    /**
     * Subdirectories with the NCBI BERT existing pretrained models.
     * 
     * Peng, Yifan, Shankai Yan, and Zhiyong Lu. 2019. 
     * “Transfer Learning in Biomedical Natural Language Processing: 
     * An Evaluation of BERT and ELMo on Ten Benchmarking Datasets.” 
     * arXiv [cs.CL]. arXiv. http://arxiv.org/abs/1906.05474.
     */
    
    private static final String  NCBI_BERT_NCBI_BERT_Base_Pubmed        = m_strBERTPretrainedModelsDir 
            + "NCBI_BERT_pubmed_uncased_L-12_H-768_A-12/";
    private static final String  NCBI_BERT_NCBI_BERT_Base_Pubmed_Mimic  = m_strBERTPretrainedModelsDir 
            + "NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12/";
    private static final String  NCBI_BERT_NCBI_BERT_Large_Pubmed       = m_strBERTPretrainedModelsDir 
            + "NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16/";
    private static final String  NCBI_BERT_NCBI_BERT_Large_Pubmed_Mimic = m_strBERTPretrainedModelsDir 
            + "NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16/";
    
    /**
     * Subdirectories with the BioBERT existing pretrained models.
     * 
     * Lee, Jinhyuk, Wonjin Yoon, Sungdong Kim, Donghyeon Kim, 
     * Sunkyu Kim, Chan Ho So, and Jaewoo Kang. 2019. 
     * “BioBERT: A Pre-Trained Biomedical Language Representation Model 
     * for Biomedical Text Mining.” arXiv [cs.CL]. arXiv. 
     * http://arxiv.org/abs/1901.08746.
     */
    
    private static final String BioBert_Base_Pubmed     = m_strBERTPretrainedModelsDir 
            + "biobert_v1.0_pubmed/";
    private static final String BioBert_Large_Pubmed    = m_strBERTPretrainedModelsDir 
            + "biobert_v1.1_pubmed/";
    private static final String BioBert_Base_PMC        = m_strBERTPretrainedModelsDir 
            + "biobert_v1.0_pmc/";
    private static final String BioBert_Base_Pubmed_PMC = m_strBERTPretrainedModelsDir 
            + "biobert_v1.0_pubmed_pmc/";
    
    /**
     * This function loads an input XML file detailing a
     * set of reproducible experiments on sentence similarity.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        boolean showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, October 2019) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
                + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file.
        
        if ((args.length > 0) && args[0].endsWith(".stsexp"))
        {
            // Running of a reproducible STS experiment file

            File inputFile = new File(args[0]);  // Get the file path

            // We check the existence of the file

            if (inputFile.exists())
            {
                // We get the start time

                long startFileProcessingTime = System.currentTimeMillis();

                // Loading message

                System.out.println("Loading and running the experiments defined in "
                        + inputFile.getName());

                // We set the Schema file for the *.stsexp
                
                // We parse the input file in order to recover all
                // STS experiments. Schema file is assumed to be in
                // same folder that the experiments file.

                ISentenceSimilarityBenchmark[] reproExperiments = 
                        SentenceSimBenchmarkFactory.loadXmlBenchmarksFile(inputFile.getPath());

                // We execute all the experiments defined in the input file

                for (ISentenceSimilarityBenchmark experiment : reproExperiments)
                {
                    experiment.evaluateBenchmark(false);
                    experiment.clear();
                }

                // We measure the elapsed time to run the experiments

                long endTime = System.currentTimeMillis();
                long minutes = (endTime - startFileProcessingTime) / 60000;
                long seconds = (endTime - startFileProcessingTime) / 1000;

                System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
                System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
            }
        }
        else
        {
            showUsage = true;
        }
        
        // For a wrong calling to the program, we show the usage.
        
        if (showUsage)
        {
            System.err.println("\nIn order to properly use the HESMLSTSClient program");
            System.err.println("you should call it using any of the two methods shown below:\n");
            System.err.println("(1) C:> java -jar dist\\HESMLSTSClient.jar <reproexperiment.stsexp>");
        }
        
        // We call the SampleExperiments() function
        
        SampleExperiments();
    }
    
    /**
     * This function runs some basic experiments to show the functionality
     * of HEMSL-STS library.
     */
    
    private static void SampleExperiments() throws IOException, InterruptedException, ParseException
    {
        // Initialize the sentences to be tested.
        // sentences1 are first sentences
        // sentences2 are second sentences
        
        String[] sentences1 = { "It it it has recently been shown that Craf is essential for Kras G12D-induced NSCLC.",
                                "The Bcl-2 inhibitor ABT-737 induces regression of solid tumors  and its derivatives "
                                + "are in the early clinical phase as cancer therapeutics; however, it targets Bcl-2, Bcl-XL, "
                                + "and Bcl-w, but not Mcl-1, which induces resistance against apoptotic cell death triggered by ABT-737."};
        String[] sentences2 = { "It has recently become evident that Craf is essential for the onset of Kras-driven "
                                + "non-small cell lung cancer.",
                                "Recently, it has been reported that ABT-737 is not cytotoxic to all tumors cells, and "
                                + "that chemoresistance to ABT-737 is dependent on appreciable levels of Mcl-1 expression, "
                                + "the one Bcl-2 family member it does not effectively inhibit."};

        // Execute the tests
        
        testStringMeasures(sentences1, sentences2);
        testBertMeasures(sentences1, sentences2);
        testWBSMMeasures(sentences1, sentences2);
    }
    
    /**
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static void testStringMeasures(
            String[] sentences1,
            String[] sentences2) throws IOException, InterruptedException
    {
        // Initialize the preprocessing method and measures
        
        IWordProcessing wordPreprocessing = null;
        ISentenceSimilarityMeasure measure = null;
        
        // Initialize the hashmap for testing
        
        HashMap<String, StringBasedSentenceSimilarityMethod> tests = new HashMap<>();
        
        // Create a Wordpreprocessing object as Blagec2019 does.
        
        wordPreprocessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv3_9_1, 
                        true, 
                        CharFilteringType.Blagec2019);
        
        // Add the string based methods to test
        
        tests.put("Jaccard Measure",            StringBasedSentenceSimilarityMethod.Jaccard);
        tests.put("Block Distance Measure",     StringBasedSentenceSimilarityMethod.BlockDistance);
        tests.put("Levenshtein Measure",        StringBasedSentenceSimilarityMethod.Levenshtein);
        tests.put("Overlap Coefficient Measure",StringBasedSentenceSimilarityMethod.OverlapCoefficient);
        tests.put("Qgram Measure",              StringBasedSentenceSimilarityMethod.Qgram);
        
        // Iterate the map 
        
        for (Map.Entry<String, StringBasedSentenceSimilarityMethod> testMeasure : tests.entrySet())
        {
            // Get the name of the measure and the type
            
            String strMeasureName = testMeasure.getKey();
            StringBasedSentenceSimilarityMethod similarityMethod = testMeasure.getValue();
            
            // Initialize the measure
            
            measure = SentenceSimilarityFactory.getStringBasedMeasure(
                            similarityMethod, 
                            wordPreprocessing);
            
            // Get the similarity scores for the lists of sentences
            
            double[] simScores = measure.getSimilarityValues(sentences1, sentences2);
            
            // Print the results - For testing purposes
            
            System.out.println("Scores for " + strMeasureName + ": ");
            for (int i = 0; i < simScores.length; i++)
            {
                double score = simScores[i];
                System.out.println("---- Sentence " + i + " : " + score);
            }
        }
    }
    
    /**
     * Test a single BERT model evaluation
     * 
     * BioBert model evaluation using the BERT FullTokenizer (WordPiece Tokenizer)
     * 
     * @param sentences1
     * @param sentences2
     * @throws IOException 
     */
    
    private static void testBertMeasures(
            String[] sentences1,
            String[] sentences2) throws IOException, InterruptedException, ParseException
    {
        // Initialize the preprocessing method and measures
        
        IWordProcessing wordPreprocessing = null;
        ISentenceSimilarityMeasure measure = null;
        
        // Create a Wordpreprocessing object using WordPieceTokenizer
        
        wordPreprocessing = PreprocessingFactory.getWordProcessing(
                        "", 
                        TokenizerType.WordPieceTokenizer, 
                        true, 
                        CharFilteringType.None,
                        m_BERTDir,
                        m_PythonVenvExecutable,
                        m_PythonWordPieceTokenizerWrapperScript,
                        BioBert_Base_PMC);
        
        // Initialize the measure
        // Test a BioBert model measure
        
        String[] poolingLayers = {"-2"};
        
        measure = SentenceSimilarityFactory.getSentenceEmbeddingMethod(
                SentenceEmbeddingMethod.BERTEmbeddingModel, 
                wordPreprocessing, 
                BioBert_Base_PMC,
                m_BERTDir,
                m_PythonVenvExecutable,
                m_PythonBERTWrapperScript,
                BERTpoolingMethod.REDUCE_MEAN,
                poolingLayers);
        
        // Get the similarity scores for the lists of sentences
            
        double[] simScores = measure.getSimilarityValues(sentences1, sentences2);

        // Print the results - For testing purposes

        System.out.println("Scores for BERT model: ");
        for (int i = 0; i < simScores.length; i++)
        {
            double score = simScores[i];
            System.out.println("---- Sentence " + i + " : " + score);
        }
    }
    
    /**
     * Test WBSM Measures from BIOSSES2017
     * 
     * @param sentences1
     * @param sentences2 
     */
    
    private static void testWBSMMeasures(
            String[] sentences1,
            String[] sentences2) throws IOException
    {
         // Initialize the preprocessing method and measures
        
        IWordProcessing wordPreprocessing = null;
        ISentenceSimilarityMeasure measure = null;
        
        // Create a Wordpreprocessing object using WordPieceTokenizer
        
        wordPreprocessing = PreprocessingFactory.getWordProcessing(
                        "", 
                        TokenizerType.WhiteSpace, 
                        true, 
                        CharFilteringType.None);
        
//        measure = SentenceSimilarityFactory. @TODO!!
    }
}

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
import hesml.measures.WordEmbeddingFileType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SWEMpoolingMethod;
import hesml.sts.measures.SentenceEmbeddingMethod;
import hesml.sts.measures.StringBasedSentSimilarityMethod;
import hesml.sts.measures.impl.SentenceSimilarityFactory;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.PreprocessType;
import hesml.sts.preprocess.impl.PreprocessFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * This function loads an input XML file detailing a
     * set of reproducible experiments on sentence similarity.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        
        boolean   showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, October 2019) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // Initialize the sentences to be tested.
        // sentences1 are first sentences
        // sentences2 are second sentences
        
        String[] sentences1 = { "It has recently been shown that Craf is essential for Kras G12D-induced NSCLC.",
                                "The Bcl-2 inhibitor ABT-737 induces regression of solid tumors  and its derivatives "
                                + "are in the early clinical phase as cancer therapeutics; however, it targets Bcl-2, Bcl-XL, "
                                + "and Bcl-w, but not Mcl-1, which induces resistance against apoptotic cell death triggered by ABT-737."};
        String[] sentences2 = { "It has recently become evident that Craf is essential for the onset of Kras-driven "
                                + "non-small cell lung cancer.",
                                "Recently, it has been reported that ABT-737 is not cytotoxic to all tumors cells, and "
                                + "that chemoresistance to ABT-737 is dependent on appreciable levels of Mcl-1 expression, "
                                + "the one Bcl-2 family member it does not effectively inhibit."};

        // Execute the tests
        
        testAllStringBasedMeasures(sentences1, sentences2);
        
        testAllBertModelsMeasures(sentences1, sentences2);
        
        testEmbeddingModelsMeasures(sentences1, sentences2);
        
        

    }
    
    /**
     * This function tests all string-based similarity measures 
     * 
     * @param sentences1
     * @param sentences2 
     */
    
    private static void testAllStringBasedMeasures(
            String[] sentences1,
            String[] sentences2) throws IOException, ParseException, InterruptedException
    {
        HashMap<String, ISentenceSimilarityMeasure> tests = new HashMap<>();
        
        tests.put("Jaccard Measure",                        testJaccardMeasure());
        tests.put("Qgram Measure",                          testQgramMeasure());
        tests.put("Block Distance Measure",                 testBlockDistanceMeasure());
        tests.put("Overlap Coefficient Measure",            testOverlapCoefficientMeasure());
        tests.put("Levenshtein Measure",                    testLevenshteinMeasure());
        tests.put("Jaccard Measure Biosses Tokenizer",      testJaccardMeasureBiossesTokenizer());
        tests.put("Jaccard Measure Blagec2019 Preprocess",  testJaccardMeasureBlagec2019Preprocess());
        tests.put("Jaccard Measure Custom Preprocess",      testJaccardMeasureCustomPreprocess());
        
        for (Map.Entry<String, ISentenceSimilarityMeasure> testMeasure : tests.entrySet())
        {
            String strMeasureName = testMeasure.getKey();
            ISentenceSimilarityMeasure measure = testMeasure.getValue();
            double[] simScores = measure.getSimilarityValues(sentences1, sentences2);
            System.out.println("Scores for " + strMeasureName + ": ");
            for (int i = 0; i < simScores.length; i++)
            {
                double score = simScores[i];
                System.out.println("---- Sentence " + i + " : " + score);
            }
        }
    }
    
    /**
     * This function test all BERT models with a simple cosine similarity measure.
     * 
     * @param sentences1
     * @param sentences2
     * @throws InterruptedException
     * @throws Exception 
     */
    
    private static void testAllBertModelsMeasures(
            String[] sentences1,
            String[] sentences2) throws InterruptedException, Exception
    {
        HashMap<String, ISentenceSimilarityMeasure> tests = new HashMap<>();
        
        tests.put("Bert Embedding Model Measure - "
                + "NCBI_BERT_pubmed_uncased_L-12_H-768_A-12", 
                testBertEmbeddingModelMeasure("NCBI_BERT_pubmed_uncased_L-12_H-768_A-12"));
        tests.put("Bert Embedding Model Measure -"
                + " NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12", 
                testBertEmbeddingModelMeasure("NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12"));
        tests.put("Bert Embedding Model Measure - "
                + "NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16", 
                testBertEmbeddingModelMeasure("NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16"));
        tests.put("Bert Embedding Model Measure - "
                + "NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16", 
                testBertEmbeddingModelMeasure("NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16"));
        
        for (Map.Entry<String, ISentenceSimilarityMeasure> testMeasure : tests.entrySet())
        {
            String strMeasureName = testMeasure.getKey();
            ISentenceSimilarityMeasure measure = testMeasure.getValue();
            double[] simScores = measure.getSimilarityValues(sentences1, sentences2);
            System.out.println("Scores for " + strMeasureName + ": ");
            for (int i = 0; i < simScores.length; i++)
            {
                double score = simScores[i];
                System.out.println("---- Sentence " + i + " : " + score);
            }
        }
    }
    
    /**
     * This function test all some embedding models with a simple cosine similarity measure.
     * 
     * @param sentences1
     * @param sentences2
     * @throws InterruptedException
     * @throws Exception 
     */
    
    private static void testEmbeddingModelsMeasures(
            String[] sentences1,
            String[] sentences2) throws InterruptedException, Exception
    {
        HashMap<String, ISentenceSimilarityMeasure> tests = new HashMap<>();
        
        tests.put("SWEMMeasure", testSWEMMeasure());
        tests.put("BioC-trained Paragraph vector with DM", testParagraphVectorDMModelMeasure());
        
        for (Map.Entry<String, ISentenceSimilarityMeasure> testMeasure : tests.entrySet())
        {
            String strMeasureName = testMeasure.getKey();
            ISentenceSimilarityMeasure measure = testMeasure.getValue();
            double[] simScores = measure.getSimilarityValues(sentences1, sentences2);
            System.out.println("Scores for " + strMeasureName + ": ");
            for (int i = 0; i < simScores.length; i++)
            {
                double score = simScores[i];
                System.out.println("---- Sentence " + i + " : " + score);
            }
        }
    }
    
    /**
     * Test function for SWEM Measure
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testSWEMMeasure() throws IOException, ParseException
    {
        String strBioWordVecfile = "../BioWordVec_models/bio_embedding_intrinsic";
        // String strBioWordVecfile = "C:\\HESML_GitHub\\HESML_Library\\WordEmbeddings\\bio_embedding_intrinsic";

        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getSWEMMeasure(
                SWEMpoolingMethod.Average, WordEmbeddingFileType.BioWordVecBinaryFile,
                preprocess, strBioWordVecfile);
        return measure;
    }

    /**
     * Test function for SWEM Measure
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testBertEmbeddingModelMeasure(
            String model) throws IOException, InterruptedException, Exception
    {

        String modelDirPath = "../BERTExperiments/BertPretrainedModels/" + model;
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(
                PreprocessType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getSentenceEmbeddingMethod(
                                                SentenceEmbeddingMethod.BERT, preprocess, modelDirPath);
        return measure;
    }
    
    /**
     * Test function for Paragraph Vector DM measure
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testParagraphVectorDMModelMeasure() throws IOException, InterruptedException, Exception
    {

        String strModelDirPath = "/home/alicia/Desktop/HESML/HESML_Library/STSTrainedModels/ParagraphVectorDM/vectors.zip";
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(
                PreprocessType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getSentenceEmbeddingMethod(
                                                SentenceEmbeddingMethod.Paragraph, preprocess, strModelDirPath);
        
        return measure;
    }
    
    /**
     * Test function for Jaccard Measure 
     * 
     * Important: In BIOSSES2017 lowercaseNormalization has to be true.
     *              BIOSSES2017 implementation do NOT remove the stop words for the evaluation!.
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testJaccardMeasure() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Biosses2017_withStopWords);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                                                StringBasedSentSimilarityMethod.Jaccard, preprocess);
        return measure;
    }
    
    /**
     * Test function for Qgram Measure 
     * 
     * Important: 
     *      In BIOSSES2017 lowercaseNormalization has to be true.
     *      BIOSSES2017 implementation do NOT remove the stop words for the evaluation!.
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testQgramMeasure() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Biosses2017_withStopWords);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                                                StringBasedSentSimilarityMethod.Qgram, preprocess);
        return measure;
    }
    
    /**
     * Test function for Block distance Measure 
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testBlockDistanceMeasure() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Biosses2017_withStopWords);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                                                    StringBasedSentSimilarityMethod.BlockDistance, preprocess);
        
        return measure;
    }

    /**
     * Test function for Overlap coefficient Measure 
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testOverlapCoefficientMeasure() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Biosses2017_withStopWords);
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                StringBasedSentSimilarityMethod.OverlapCoefficient, preprocess);
        
        return measure;
    }    
    
    /**
     * Test function for Levenshtein Measure 
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testLevenshteinMeasure() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Biosses2017_withStopWords);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                                                StringBasedSentSimilarityMethod.Levenshtein, preprocess);
        return measure;
    }   
    
    
    /**
     * Test function for Jaccard Measure using BIOSSES tokenizer
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testJaccardMeasureBiossesTokenizer() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(
                PreprocessType.Biosses2017);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                                                StringBasedSentSimilarityMethod.Jaccard, preprocess);
        return measure;
    }
    
    /**
     * Test function for Jaccard Measure using Blagec2019 preprocessing
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testJaccardMeasureBlagec2019Preprocess() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Blagec2019);
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                                                StringBasedSentSimilarityMethod.Jaccard, preprocess);
        return measure;
    }
    
    /**
     * Test function for Jaccard Measure using a custom tokenizer
     * 
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testJaccardMeasureCustomPreprocess() throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(
                true, 
                hesml.sts.preprocess.TokenizerType.WhiteSpace,
                "../StopWordsFiles/Biosses2017StopWords.txt",
                hesml.sts.preprocess.CharFilteringType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getStringBasedMeasure(
                                                 StringBasedSentSimilarityMethod.Jaccard, preprocess);
        return measure;
    }
    
    /**
     * Preprocess the datasets for evaluate BERT models.
     * @throws Exception 
     */
    
    private static void PreprocessDatasets() throws Exception
    {
        List<File> filesInFolder = Files.walk(Paths.get("../SentenceSimDatasets"))
                                .filter(Files::isRegularFile)
                                .map(Path::toFile)
                                .collect(Collectors.toList());
        
        for(File file : filesInFolder)
        {
            String fileName = file.getName();
            if(!fileName.contains("CTRNormalized_3scores"))
            {
                String strInputDatasetPath = "../SentenceSimDatasets/" + fileName;
                String strOutputDatasetPath = "../SentenceSimDatasets/preprocessedDatasets/" + fileName;
                PreprocessFactory.preprocessDataset(PreprocessType.DefaultJava, strInputDatasetPath, strOutputDatasetPath);     
            }

        }

    }
}

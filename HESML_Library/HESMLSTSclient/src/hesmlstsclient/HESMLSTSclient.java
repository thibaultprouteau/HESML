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

public class HESMLSTSclient
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
        
        
        // Preprocess the datasets before the testing. 
        // Only for BERT similarity calculation.
        
//         PreprocessDatasets();
        
        // Initialize the sentences to be tested.
        
        String sentence1 = "It has recently been shown that Craf is essential for Kras G12D-induced NSCLC.";
        String sentence2 = "It has recently become evident that Craf is essential for the onset of Kras-driven non-small cell lung cancer.";

//        String sentence1 = "My brother has a dog with four legs.";
//        String sentence2 = "My brother has four legs.";

        // Init of testing functions
        
        // Create a hashmap to include the testing methods.

        HashMap<String, ISentenceSimilarityMeasure> tests = new HashMap<>();
        
//        tests.put("SWEMMeasure", testSWEMMeasure());
        tests.put("BioC-trained Paragraph vector with DM", testParagraphVectorModelMeasure());
//        tests.put("Bert Embedding Model Measure - NCBI_BERT_pubmed_uncased_L-12_H-768_A-12", testBertEmbeddingModelMeasure("vectors_NCBI_BERT_pubmed_uncased_L-12_H-768_A-12"));
//        tests.put("Bert Embedding Model Measure - NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12", testBertEmbeddingModelMeasure("vectors_NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12"));
//        tests.put("Bert Embedding Model Measure - NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16", testBertEmbeddingModelMeasure("vectors_NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16"));
//        tests.put("Bert Embedding Model Measure - NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16", testBertEmbeddingModelMeasure("vectors_NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16"));
//
//        tests.put("Jaccard Measure", testJaccardMeasure());
//        tests.put("Qgram Measure", testQgramMeasure());
//        tests.put("Block Distance Measure", testBlockDistanceMeasure());
//        tests.put("Overlap Coefficient Measure", testOverlapCoefficientMeasure());
//        tests.put("Levenshtein Measure", testLevenshteinMeasure());
//        tests.put("Jaccard Measure Biosses Tokenizer", testJaccardMeasureBiossesTokenizer());
//        tests.put("Jaccard Measure Blagec2019 Preprocess", testJaccardMeasureBlagec2019Preprocess());
//        tests.put("Jaccard Measure Custom Preprocess", testJaccardMeasureCustomPreprocess());
        
        for (Map.Entry<String, ISentenceSimilarityMeasure> testMeasure : tests.entrySet())
        {
            String strMeasureName = testMeasure.getKey();
            ISentenceSimilarityMeasure measure = testMeasure.getValue();
            double simScore = measure.getSimilarityValue(sentence1, sentence2);
            System.out.println("Score for " + strMeasureName + ": " + simScore);
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
//        String strBioWordVecfile = "C:\\HESML_GitHub\\HESML_Library\\WordEmbeddings\\bio_embedding_intrinsic";

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
    
    private static ISentenceSimilarityMeasure testBertEmbeddingModelMeasure(String model) throws IOException, InterruptedException, Exception
    {

        String strEmbeddingsDirPath = "../BERTExperiments/generatedEmbeddings/BIOSSESNormalizedtsv/" + model + "/embeddings.json";
        String  strPreprocessedDatasetPath = "../SentenceSimDatasets/preprocessedDatasets/BIOSSESNormalized.tsv";
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getBertEmbeddingModelMeasure(
                strEmbeddingsDirPath,
                preprocess);
        return measure;
    }
    
    /**
     * Test function for SWEM Measure
     * @throws IOException
     * @throws ParseException 
     */
    
    private static ISentenceSimilarityMeasure testParagraphVectorModelMeasure() throws IOException, InterruptedException, Exception
    {

        String strModelDirPath = "/home/alicia/Desktop/HESML/HESML_Library/STSTrainedModels/ParagraphVectorDBOW/vectors.zip";
//        String  strPreprocessedDatasetPath = "../SentenceSimDatasets/preprocessedDatasets/BIOSSESNormalized.tsv";
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getParagraphVectorModelMeasure(
                strModelDirPath, 
                preprocess);
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
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getJaccardMeasure(preprocess);
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
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getQgramMeasure(preprocess);
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
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getBlockDistanceMeasure(preprocess);
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
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getOverlapCoefficientMeasure(preprocess);
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
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getLevenshteinMeasure(preprocess);
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
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Biosses2017);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getJaccardMeasure(preprocess);
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
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getJaccardMeasure(preprocess);
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
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getJaccardMeasure(preprocess);
        return measure;
    }
    
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

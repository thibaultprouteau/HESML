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
import java.io.IOException;
import java.text.ParseException;

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
     * @throws java.text.ParseException
     */
    
    public static void main(String[] args) throws IOException, ParseException
    {
        boolean   showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, October 2019) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
    
        // Init of testing functions
        
        String sentence1 = "It has recently been shown that Craf is essential for Kras G12D-induced NSCLC.";
        String sentence2 = "It has recently become evident that Craf is essential for the onset of Kras-driven non-small cell lung cancer.";
//        String sentence1 = "My brother has a dog with four legs.";
//        String sentence2 = "My brother has four legs.";

        testSWEMMeasure(sentence1, sentence2);
        testJaccardMeasure(sentence1, sentence2);
        testJaccardMeasureBiossesTokenizer(sentence1, sentence2);
        testJaccardMeasureCustomTokenizer(sentence1, sentence2);
    }
    
    /**
     * Test function for SWEM Measure
     * @param sentence1
     * @param sentence2
     * @throws IOException
     * @throws ParseException 
     */
    
    private static void testSWEMMeasure(
            String sentence1, 
            String sentence2) throws IOException, ParseException
    {
        String strBioWordVecfile = "/home/alicia/HESML/HESML_Library/BioWordVec_models/bio_embedding_intrinsic";
//        String strBioWordVecfile = "C:\\HESML_GitHub\\HESML_Library\\WordEmbeddings\\bio_embedding_intrinsic";

        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getSWEMMeasure(
                SWEMpoolingMethod.Average, WordEmbeddingFileType.BioWordVecBinaryFile,
                preprocess, strBioWordVecfile);
        double simScore = measure.getSimilarityValue(sentence1, sentence2);

        System.out.println("Score: " + simScore);
    }
    
    /**
     * Test function for Jaccard Measure
     * 
     * Important: In BIOSSES2017 lowercaseNormalization has to be true.
     * @param sentence1
     * @param sentence2
     * @throws IOException
     * @throws ParseException 
     */
    
    private static void testJaccardMeasure(
            String sentence1, 
            String sentence2) throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.DefaultJava);
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getJaccardMeasure(preprocess);
        double simScore = measure.getSimilarityValue(sentence1, sentence2);
        System.out.println("Score: " + simScore);
    }
    
    
    /**
     * Test function for Jaccard Measure using BIOSSES tokenizer
     * @param sentence1
     * @param sentence2
     * @throws IOException
     * @throws ParseException 
     */
    
    private static void testJaccardMeasureBiossesTokenizer(
            String sentence1, 
            String sentence2) throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(PreprocessType.Biosses2017);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getJaccardMeasure(preprocess);
        double simScore = measure.getSimilarityValue(sentence1, sentence2);
        System.out.println("Score: " + simScore);
    }
    
        /**
     * Test function for Jaccard Measure using a custom tokenizer
     * @param sentence1
     * @param sentence2
     * @throws IOException
     * @throws ParseException 
     */
    
    private static void testJaccardMeasureCustomTokenizer(
            String sentence1, 
            String sentence2) throws IOException, ParseException
    {
        IWordProcessing preprocess = PreprocessFactory.getPreprocessPipeline(
                true, 
                hesml.sts.preprocess.TokenizerType.WhiteSpace,
                "../StopWordsFiles/Biosses2017StopWords.txt",
                hesml.sts.preprocess.CharFilteringType.DefaultJava);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getJaccardMeasure(preprocess);
        double simScore = measure.getSimilarityValue(sentence1, sentence2);
        System.out.println("Score: " + simScore);
    }
}

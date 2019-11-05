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
package hesml.sts.languagemodels.impl;

import hesml.sts.languagemodels.LanguageModelMethod;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.iter.NdIndexIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import hesml.sts.languagemodels.ILanguageModel;

/**
 *
 * @author alicia
 */
class ParagraphVectorModel implements ILanguageModel
{
    private LanguageModelMethod m_trainingMethod;
    private ParagraphVectors m_paragraphVectors;
    
    /**
     * Constructor by default
     */
    
    ParagraphVectorModel()
    {
        m_paragraphVectors = null;
    }
    
    /**
     * Constructor
     * @param trainingMethod 
     */
    
    ParagraphVectorModel(
            LanguageModelMethod trainingMethod)
    {
        m_trainingMethod = trainingMethod;
        m_paragraphVectors = null;
    }
    
    @Override
    public LanguageModelMethod getTrainingMethod(){return m_trainingMethod;}

    @Override
    public void train(
            String strTrainningInputDocumentPath,
            String strTrainningOutputDocumentPath) throws FileNotFoundException, IOException
    {
//        ClassPathResource resource = new ClassPathResource(strTrainningInputDocumentPath);
        File file = new File(strTrainningInputDocumentPath);
        SentenceIterator iter = new BasicLineIterator(file);
        AbstractCache<VocabWord> cache = new AbstractCache<>();
        
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        
        /*
             if you don't have LabelAwareIterator handy, you can use synchronized labels generator
              it will be used to label each document/sequence/line with it's own label.
              But if you have LabelAwareIterator ready, you can can provide it, for your in-house labels
        */
        
        LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = new ParagraphVectors.Builder()
                .minWordFrequency(1)
                .iterations(100)
                .epochs(1)
                .layerSize(50)
                .learningRate(0.02)
                .labelsSource(source)
                .windowSize(5)
                .iterate(iter)
                .trainWordVectors(true)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .sampling(0)
//                .sequenceLearningAlgorithm("org.deeplearning4j.models.embeddings.learning.impl.sequence.DBOW") 
                .sequenceLearningAlgorithm("org.deeplearning4j.models.embeddings.learning.impl.sequence.DM") 
                .build();

        vec.fit();
        
        File tempFile = new File(strTrainningOutputDocumentPath);
//        tempFile.deleteOnExit();
        
        WordVectorSerializer.writeParagraphVectors(vec, tempFile);
    }
    
    @Override
    public ParagraphVectors loadVectors(
            String strParagraphVectorModelPath) throws IOException 
    {
        m_paragraphVectors = WordVectorSerializer.readParagraphVectors(strParagraphVectorModelPath);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        m_paragraphVectors.setTokenizerFactory(t);
        m_paragraphVectors.getConfiguration().setIterations(10); // please note, we set iterations to 1 here, just to speedup inference
        return m_paragraphVectors;
    }
    
    @Override
    public double[] getVectorFromStrSentence(
            String strSentence)
    {
        INDArray inferredVector = null;
        if (m_paragraphVectors != null) {
            inferredVector = m_paragraphVectors.inferVector(strSentence);
            double[] vector = transformINDArraytoDoubleArray(inferredVector);
            return vector;
        }
        return null;
    }

    @Override
    public void setTrainingMethod(LanguageModelMethod method)
    {
        m_trainingMethod = method;
    }
    
    
    private double[] transformINDArraytoDoubleArray(
            INDArray iNDArray)
    {
        int nRows = iNDArray.rows();
        int nCols = iNDArray.columns();
        double[] vector = new double[nCols];
        NdIndexIterator iter = new NdIndexIterator(nRows, nCols);
        int i = 0;
        while (iter.hasNext()) {
            int[] nextIndex = iter.next();
            double nextVal = iNDArray.getDouble(nextIndex);
            //do something with the value
            vector[i] = nextVal;
            i++;
        }
        return vector;
    }
}

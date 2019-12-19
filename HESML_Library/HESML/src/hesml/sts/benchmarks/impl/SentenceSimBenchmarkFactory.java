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

import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.WordEmbeddingFileType;
import hesml.measures.impl.MeasureFactory;
import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.measures.BERTpoolingMethod;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SWEMpoolingMethod;
import hesml.sts.measures.SentenceEmbeddingMethod;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.measures.impl.SentenceSimilarityFactory;
import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import hesml.sts.preprocess.impl.PreprocessingFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.impl.WordNetFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class builds the instances of sentence similarity benchmarks.
 * @author j.lastra
 */

public class SentenceSimBenchmarkFactory
{
    /**
     * This function returns an instance of a single-dataset sentence
     * similarity benchmark.
     * @param measures
     * @param strDatasetDirectory
     * @param strDatasetFilename
     * @param strOutputFilename
     * @return 
     * @throws java.lang.Exception 
     */
    
    private static ISentenceSimilarityBenchmark getSingleDatasetBenchmark(
            ISentenceSimilarityMeasure[]    measures,
            String                          strDatasetDirectory,
            String                          strDatasetFilename,
            String                          strOutputFilename) throws Exception
    {
        return (new SentenceSimilaritySingleBenchmark(measures,
                strDatasetDirectory, strDatasetFilename, strOutputFilename));
    }
    
    /**
     * This function loads a collection of sentence similarity benchmarks
     * specified into a XML--based experiment file (*.sexp). This later
     * file format is defined in the SentenceSimilarityExperiments.xsd
     * schema file contained in the HESML_Library/ReproducibleExperiments folder.
     * @param strXmlBenchmarksFile
     * @return 
     */
    
    public static void runXmlBenchmarksFile(
            String  strXmlBenchmarksFile) throws FileNotFoundException, Exception
    {
        // We get the File information of the XML benchamrk file
        
        File fileInfo = new File(strXmlBenchmarksFile);
        
        // We check the existence of the file
        
        if (!fileInfo.exists()) throw (new FileNotFoundException(strXmlBenchmarksFile));
        
        // We get the directory containg the benchmark file
        
        String strOutputDirectory = fileInfo.getParent();
        
        if (strOutputDirectory == null) strOutputDirectory = "";

        // We configure the Xml parser in order to validate the Xml file
        // by using the schema that describes the Xml file format
        // for the reproducible experiments.
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        
        // We parse the input document
        
        Document xmlDocument = docBuilder.parse(fileInfo);
        
        // We get the root node and checks its identity
        
        Element rootNode = xmlDocument.getDocumentElement();

        if (!rootNode.getNodeName().equals("SentenceSimilarityExperiments"))
        {
            String strError = "Wrong file format";
            throw (new Exception(strError));
        }
        
        // We get the node with collection of experiments 
        
        NodeList experimentCollection = rootNode.getChildNodes();
        
        // We traverse the collection of experiments parsing them
        
        for (int i = 0; i < experimentCollection.getLength(); i++)
        {
            // We get the next experiment in the Xml node collection
            
            if (experimentCollection.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the experiment root node
                
                Element experimentRoot = (Element) experimentCollection.item(i);

                // We parse the next experiment in the XML file
                
                if (experimentRoot.getNodeName().equals("SingleDatasetSimilarityValuesExperiment"))
                {
                    // We reda the next benchmark
                    
                    ISentenceSimilarityBenchmark benchmark = readBenchmark(experimentRoot, strOutputDirectory);
                    
                    // We ptint a Debug message
                    
                    System.out.println(benchmark.getOutputFilename());
                    
                    // We run and destroy the benchmark
                    
                    benchmark.evaluateBenchmark(true);
                    benchmark.clear();
                }
            }
        }
    }

    /**
     * This function parses a XML node encoding an experiment.
     * @param experimentRoot
     * @return 
     */
    
    private static ISentenceSimilarityBenchmark readBenchmark(
            Element experimentRoot,
            String  strOutputDirectory) throws IOException, Exception
    {
        // We read the configuration of the experiment
        
        String strOutputFileName = readStringField(experimentRoot, "OutputFilename");
        String strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String strDatasetFileName = readStringField(experimentRoot, "DatasetFilename");
        
        // We compute the full path of the output file
        
        if (!strOutputDirectory.equals(""))
        {
            strOutputFileName = strOutputDirectory + "/" + strOutputFileName;
        }
        
        // We get the coollection of measure nodes
        
        NodeList measureNodes = experimentRoot.getElementsByTagName("SentenceSimilarityMeasures").item(0).getChildNodes();
        
        // We create a temporary collection of sentene similarity measures
        
        ArrayList<ISentenceSimilarityMeasure> tempMeasureList = new ArrayList<>();
        
        // We parse all measures

        for (int i = 0; i < measureNodes.getLength(); i++)
        {
            // We filter all non-element nodes
            
            if (measureNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the next measure node in the list

                Element measureNode = (Element) measureNodes.item(i);

                // We read the measure
                
                switch (measureNode.getTagName())
                {
                    case "StringBasedSentenceSimilarityMeasure":
                    
                        // We loads and register a string-based measurs from the XML file 
                        
                        tempMeasureList.add(SentenceSimilarityFactory.getStringBasedMeasure(
                                convertToStringBasedSentenceSimilarityMethod(readStringField(measureNode, "Method")),
                                readWordProcessing(measureNode)));

                        break;
                        
                    case "SWEMMeasure":
                        
                        // We load and register a SWEM measurs from the XML file 
                        
                        String strPretrainedModelFilename = readStringField(measureNode, "PretrainedModelFilename");
                        String strPretrainedModelDir = readStringField(measureNode, "PretrainedModelDirectory");
                        String strLabel = readStringField(measureNode, "Label");
                        
                        tempMeasureList.add(SentenceSimilarityFactory.getSWEMMeasure(strLabel,
                                convertToSWEMpoolingMethod(readStringField(measureNode, "Pooling")),
                                convertToWordEmbeddingFileType(readStringField(measureNode, "WordEmbeddingFileFormat")),
                                readWordProcessing(measureNode),
                                strPretrainedModelDir + "/" + strPretrainedModelFilename));
                        
                        break;
                    
                    case "BertEmbeddingModelMeasure":
                        
                        // We load and register a BERT measure from the XML file 
                        
                        String strBERTPretrainedModelFilename = readStringField(measureNode, "PretrainedModelName");
                        String strBERTPretrainedModelDir = readStringField(measureNode, "PretrainedModelDirectory");
                        String strPythonScriptsDirectory = readStringField(measureNode, "PythonScriptsDirectory");
                        String strPythonVirtualEnvironmentDir = readStringField(measureNode, "PythonVirtualEnvironmentDir");
                        String strPythonScript = readStringField(measureNode, "PythonScriptFilename");
                        String strBERTLabel = readStringField(measureNode, "Label");
                        String strPoolingLayers = readStringField(measureNode, "PoolingLayers");
                        
                        // Convert the pooling layers to an array splitted by comma's
                        
                        String[] poolingLayers = strPoolingLayers.split(",");
                        
                        tempMeasureList.add(SentenceSimilarityFactory.getBERTSentenceEmbeddingMethod(
                                strBERTLabel, 
                                convertToSentenceEmbeddingMethod(readStringField(measureNode, "Method")),
                                readBERTWordProcessing(measureNode), 
                                strBERTPretrainedModelDir + strBERTPretrainedModelFilename, 
                                strPythonScriptsDirectory, 
                                strPythonVirtualEnvironmentDir, 
                                strPythonScriptsDirectory + strPythonScript, 
                                convertToBERTpoolingMethod(readStringField(measureNode, "Pooling")), 
                                poolingLayers));
                        
                        break;
                    
                    case "WBSMMeasure":
                        
                        // We load and register a WBSM measure from the XML file
                                
                        String strWordNetDbDir = readStringField(measureNode, "WordNetDbDir");
                        String strWordNetDbFilename = readStringField(measureNode, "WordNetDbFilename");
                        String strWordSimilarityMeasureType = readStringField(measureNode, "WordSimilarityMeasureType");
                        String strWBSMLabel = readStringField(measureNode, "Label");
                        
                        // Create the word similarity measure
                        // Get the word similarity measure type
                        
                        SimilarityMeasureType wordMeasureType = convertToWordSimilarityMeasureType(strWordSimilarityMeasureType);
                        
                        // Create the WordnetDB and Wordnet taxonomy instance
                        // We load the WordNet database
                        
                        IWordNetDB wordnet = WordNetFactory.loadWordNetDatabase(strWordNetDbDir, strWordNetDbFilename);    
                        
                        // We build the taxonomy
                        
                        ITaxonomy wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);  
                        
                        // We pre-process the taxonomy to compute all the parameters
                        // used by the intrinsic IC-computation methods

                        wordnetTaxonomy.computesCachedAttributes();
                        
                        // We obtain an instance of the Seco et al. (2004) IC model,
                        // then the model is computed on the taxonomy. All the IC models
                        // computes and store the IC values in the own taxonomy vertexes.

                        // secoICmodel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Seco);
                        // secoICmodel.setTaxonomyData(wordnetTaxonomy);
                        
                        ISimilarityMeasure wordSimilarityMeasure = MeasureFactory.getMeasure(
                                                                    wordnetTaxonomy, wordMeasureType);
                        
                        // Add the WBSM measure to the list
                        
                        tempMeasureList.add(SentenceSimilarityFactory.getWBSMMeasure(
                                strWBSMLabel, readWordProcessing(measureNode), 
                                wordSimilarityMeasure, wordnet, wordnetTaxonomy));
                }
            }
        }
        
        // We create the vector to return the collection of senntence similarity measures
         
        ISentenceSimilarityMeasure[] measures = new ISentenceSimilarityMeasure[tempMeasureList.size()];
        
        // We copy yhr measures to the vector and release the temporrary list
        
        tempMeasureList.toArray(measures);
        tempMeasureList.clear();
        
        // We create the benchmark for all measuers and dataset
        
        ISentenceSimilarityBenchmark benchmark = new SentenceSimilaritySingleBenchmark(
                                                    measures, strDatasetDir,
                                                    strDatasetFileName, strOutputFileName);
                
        // We return the result
        
        return (benchmark);
    }
    
    /**
     * This function parses a word processing object from a XML-based experiment file.
     * @param measureRootNode
     * @return 
     */
    
    private static IWordProcessing readWordProcessing(
            Element measureRootNode) throws IOException
    {
        // We get the word processing node

        Element wordProcessingNode = (Element) measureRootNode.getElementsByTagName("WordProcessing").item(0);
        
        // We read the word processing attributes
        
        String strStopWordsFileDir = readStringField(wordProcessingNode, "StopWordsFileDir");
        String strStopWordsFilename = readStringField(wordProcessingNode, "StopWordsFilename"); 

        // We parse the chracter filtering method
        
        IWordProcessing processer = PreprocessingFactory.getWordProcessing(
                                        strStopWordsFileDir + "/" + strStopWordsFilename,
                                        convertToTokenizerType(readStringField(wordProcessingNode, "TokenizerType")),
                                        readBooleanField(wordProcessingNode, "LowercaseNormalization"),
                                        convertToCharFilteringType(readStringField(wordProcessingNode, "CharFilteringType")));
        
        // We return the result
        
        return (processer);
    }
    
        /**
     * This function parses a word processing object from a XML-based experiment file.
     * @param measureRootNode
     * @return 
     */
    
    private static IWordProcessing readBERTWordProcessing(
            Element measureRootNode) throws IOException
    {
        // We get the word processing node

        Element wordProcessingNode = (Element) measureRootNode.getElementsByTagName("WordProcessing").item(0);
        
        // We read the word processing attributes
        
        String strStopWordsFileDir = readStringField(wordProcessingNode, "StopWordsFileDir");
        String strStopWordsFilename = readStringField(wordProcessingNode, "StopWordsFilename"); 
        
        // We read the Python wrapper data
        
        String strBERTPretrainedModelFilename = readStringField(wordProcessingNode, "PretrainedModelFilename");
        String strBERTPretrainedModelDir = readStringField(wordProcessingNode, "PretrainedModelDirectory");
        String strPythonScriptsDirectory = readStringField(wordProcessingNode, "PythonScriptsDirectory");
        String strPythonVirtualEnvironmentDir = readStringField(wordProcessingNode, "PythonVirtualEnvironmentDir");
        String strPythonScript = readStringField(wordProcessingNode, "PythonWordPieceTokenizerScript");

        // We parse the chracter filtering method
        
        IWordProcessing processer = PreprocessingFactory.getWordProcessing(
                                        strStopWordsFileDir + "/" + strStopWordsFilename,
                                        convertToTokenizerType(readStringField(wordProcessingNode, "TokenizerType")),
                                        readBooleanField(wordProcessingNode, "LowercaseNormalization"),
                                        convertToCharFilteringType(readStringField(wordProcessingNode, "CharFilteringType")),
                                        strPythonScriptsDirectory,
                                        strPythonVirtualEnvironmentDir,
                                        strPythonScriptsDirectory + strPythonScript,
                                        strBERTPretrainedModelDir + strBERTPretrainedModelFilename);
        
        // We return the result
        
        return (processer);
    }
    
    /**
     * This function reads a text value of a child element.
     * @param parent
     * @param strFieldName
     * @return 
     */
    
    private static String readStringField(
            Element parent,
            String  strFieldName)
    {
        // We get the child node matching the input name

        Element child = getFirstChildWithTagName(parent, strFieldName);
        
        // We check the existence of the child node
        
        if (child == null) throw (new IllegalArgumentException(strFieldName));
        
        // We get the output value
        
        String strText = child.getFirstChild().getNodeValue();
        
        // We return the result
        
        return (strText);
    }

    /**
     * This function reads a Boolean value of a child element.
     * @param parent
     * @param strFieldName
     * @return 
     */
    
    private static boolean readBooleanField(
            Element parent,
            String  strFieldName)
    {
        // We get the child node matching the input name

        Element child = getFirstChildWithTagName(parent, strFieldName);
        
        // We check the existence of the child node
        
        if (child == null) throw (new IllegalArgumentException(strFieldName));
        
        // We get the output value
        
        boolean value = Boolean.parseBoolean(child.getFirstChild().getNodeValue());
        
        // We return the result
        
        return (value);
    }
    
    /**
     * This function returns the first child element whose tag name matches
     * the input tag name.
     * @param parent
     * @param strChildTagName
     * @return 
     */
    
    private static Element getFirstChildWithTagName(
            Element parent,
            String  strChildTagName)
    {
        // We initiliza the output value
        
        Element selectedChild = null;   // Returned value
        
        // We get the collection of XML child nodes
        
        NodeList children = parent.getChildNodes();
        
        // We traverse the direct child nodes
        
        for (int i = 0; i < children.getLength(); i++)
        {
            // We get the next child node
            
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the element
                
                Element child = (Element)children.item(i);
                
                // We look for the required child node

                if (child.getTagName().equals(strChildTagName))
                {
                    selectedChild = child;
                    break;
                }
            }
        }
        
        // We return the result
        
        return (selectedChild);
    }
    
    /**
     * This function converts the input string into a
     * StringBasedSentenceSimilarityMethod value.
     * @param strICmodelType
     * @return 
     */
    
    private static StringBasedSentenceSimilarityMethod convertToStringBasedSentenceSimilarityMethod(
            String  strMethod)
    {
        // We initialize the output
        
        StringBasedSentenceSimilarityMethod recoveredMethod = StringBasedSentenceSimilarityMethod.Qgram;
        
        // We look for the matching value
        
        for (StringBasedSentenceSimilarityMethod methodType: StringBasedSentenceSimilarityMethod.values())
        {
            if (methodType.toString().equals(strMethod))
            {
                recoveredMethod = methodType;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredMethod);
    }
    

    /**
     * This function converts the input string into a WordEmbeddingFileType value.
     * @param strICmodelType
     * @return 
     */
    
    private static WordEmbeddingFileType convertToWordEmbeddingFileType(
            String  strEmbeddingFileType)
    {
        // We initialize the output
        
        WordEmbeddingFileType recoveredType = WordEmbeddingFileType.BioWordVecBinaryWordEmbedding;
        
        // We look for the matching value
        
        for (WordEmbeddingFileType fileType: WordEmbeddingFileType.values())
        {
            if (fileType.toString().equals(strEmbeddingFileType))
            {
                recoveredType = fileType;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredType);
    }

    /**
     * This function converts the input string into a SWEMpoolingMethod value.
     * @param strICmodelType
     * @return 
     */
    
    private static SWEMpoolingMethod convertToSWEMpoolingMethod(
            String  strPoolingMethod)
    {
        // We initialize the output
        
        SWEMpoolingMethod recoveredPooling = SWEMpoolingMethod.Average;
        
        // We look for the matching value
        
        for (SWEMpoolingMethod poolingType: SWEMpoolingMethod.values())
        {
            if (poolingType.toString().equals(strPoolingMethod))
            {
                recoveredPooling = poolingType;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredPooling);
    }
    
        /**
     * This function converts the input string into a BERTpoolingMethod value.
     * @param strICmodelType
     * @return 
     */
    
    private static BERTpoolingMethod convertToBERTpoolingMethod(
            String  strPoolingMethod)
    {
        // We initialize the output
        
        BERTpoolingMethod recoveredPooling = BERTpoolingMethod.REDUCE_MEAN;
        
        // We look for the matching value
        
        for (BERTpoolingMethod poolingType: BERTpoolingMethod.values())
        {
            if (poolingType.toString().equals(strPoolingMethod))
            {
                recoveredPooling = poolingType;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredPooling);
    }

    /**
     * This function converts the input string into a TokenizerType value.
     * @param strICmodelType
     * @return 
     */
    
    private static TokenizerType convertToTokenizerType(
            String  strMethod)
    {
        // We initialize the output
        
        TokenizerType recoveredType = TokenizerType.StanfordCoreNLPv3_9_1;
        
        // We look for the matching value
        
        for (TokenizerType tokenType: TokenizerType.values())
        {
            if (tokenType.toString().equals(strMethod))
            {
                recoveredType = tokenType;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredType);
    }

    /**
     * This function converts the input string into a CharFilteringType value.
     * @param strICmodelType
     * @return 
     */
    
    private static CharFilteringType convertToCharFilteringType(
            String  strMethod)
    {
        // We initialize the output
        
        CharFilteringType recoveredType = CharFilteringType.BIOSSES;
        
        // We look for the matching value
        
        for (CharFilteringType tokenType: CharFilteringType.values())
        {
            if (tokenType.toString().equals(strMethod))
            {
                recoveredType = tokenType;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredType);
    }
    
    /**
     * This function converts the input string into a SentenceEmbeddingMethod value.
     * @param strICmodelType
     * @return 
     */
    
    private static SentenceEmbeddingMethod convertToSentenceEmbeddingMethod(
            String  strEmbeddingMethod)
    {
        // We initialize the output
        
        SentenceEmbeddingMethod recoveredMethod = SentenceEmbeddingMethod.BERTEmbeddingModel;
        
        // We look for the matching value
        
        for (SentenceEmbeddingMethod method: SentenceEmbeddingMethod.values())
        {
            if (method.toString().equals(strEmbeddingMethod))
            {
                recoveredMethod = method;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredMethod);
    }
    
        /**
     * This function converts the input string into a
     * StringBasedSentenceSimilarityMethod value.
     * @param strICmodelType
     * @return 
     */
    
    private static SimilarityMeasureType convertToWordSimilarityMeasureType(
            String  strMethod)
    {
        // We initialize the output
        
        SimilarityMeasureType recoveredMethod = SimilarityMeasureType.Lin;
        
        // We look for the matching value
        
        for (SimilarityMeasureType methodType: SimilarityMeasureType.values())
        {
            if (methodType.toString().equals(strMethod))
            {
                recoveredMethod = methodType;
                break;
            }
        }
        
        // We return the result
        
        return (recoveredMethod);
    }
}

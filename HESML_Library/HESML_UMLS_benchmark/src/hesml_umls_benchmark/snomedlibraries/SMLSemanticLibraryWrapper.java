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

package hesml_umls_benchmark.snomedlibraries;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.SemanticLibraryType;
import java.util.HashMap;
import java.util.HashSet;
import org.openrdf.model.URI;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.loader.bio.snomedct.GraphLoaderSnomedCT_RF2;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import hesml_umls_benchmark.ISemanticLibrary;

/**
 * * This class implementes the SNOMED similarity library based on SML.
 * @author j.lastra
 */

class SMLSemanticLibraryWrapper extends SnomedSimilarityLibrary
        implements ISemanticLibrary
{
    /**
     * SML factory object
     */
    
    private URIFactory m_factory;
    
    /**
     * Graph structure encoding the SNOMED taxonomy
     */
    
    private G m_graph;
    
    /**
     * Object responsible of computing the similarity measures
     */
    
    private SM_Engine m_engine;
    
    /**
     * SNOMED URI
     */
    
    private URI m_snomedctURI;

    /**
     * SML IC model and similarity measure
     */
    
    private ICconf m_icConf;
    private SMconf m_smConf;

    /**
     * IndexedSNOMED-CT ids by CUI
     */
    
    private HashMap<String, HashSet<Long>>  m_indexedSnomedIDsByCUI;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strCUIconceptsFilename
     * @throws Exception 
     */
    
    SMLSemanticLibraryWrapper(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strCUIconceptsFilename) throws Exception
    {
        // Inicializamos la clase base
        
        super(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strCUIconceptsFilename);
        
        // We initialize the object
        
        m_graph = null;
        m_factory = null;
        m_engine = null;
        m_indexedSnomedIDsByCUI = null;
    }
    
    /**
     * This fucntion returns the degree of similarity between two
     * UMLS concepts.
     * @param strFirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */
    
    @Override
    public double getSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We initilizae the output
        
        double similarity = 0.0;
        
        // We get the SNOMED concepts evoked by each CUI
        
        HashSet<Long> firstSnomedConcepts = m_indexedSnomedIDsByCUI.containsKey(strFirstUmlsCUI) ?
                                            m_indexedSnomedIDsByCUI.get(strFirstUmlsCUI) : null;

        HashSet<Long> secondSnomedConcepts = m_indexedSnomedIDsByCUI.containsKey(strSecondUmlsCUI) ?
                                            m_indexedSnomedIDsByCUI.get(strSecondUmlsCUI) : null;
        
        // We check the existence oif SNOMED concepts associated to the CUIS
        
        if ((firstSnomedConcepts != null)
                && (secondSnomedConcepts != null))
        {
            // We initialize the maximum similarity
            
            double maxSimilarity = Double.NEGATIVE_INFINITY;
            
            // We compare all pairs of evoked SNOMED concepts
            
            for (Long snomedId1: firstSnomedConcepts)
            {
                for (long snomedId2: secondSnomedConcepts)
                {
                    // We get the URI for both concepts

                    URI concept1 = m_factory.getURI(m_snomedctURI.stringValue() + snomedId1);
                    URI concept2 = m_factory.getURI(m_snomedctURI.stringValue() + snomedId2);

                    // We evaluate the similarity measure
        
                    double snomedSimilarity = m_engine.compare(m_smConf, concept1, concept2);
                    
                    // We update the maximum similarity
                    
                    if (snomedSimilarity > maxSimilarity) maxSimilarity = snomedSimilarity;
                }
            }
            
            // We assign the output similarity value
            
            similarity = maxSimilarity;
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * We release the resources associated to this object
     */
    
    @Override
    public void clear()
    {
        unloadOntology();
    }  
    
    /**
     * Load the ontology
     */
    
    @Override
    public void loadOntology() throws Exception
    {
        // We load the mapping from CUI to SNOMED-CT ids
        
        m_indexedSnomedIDsByCUI = readConceptsUmlsCUIs(m_strSnomedDir,
                                    m_strSnomedDBconceptFileName,
                                    m_strUmlsDir, m_strUmlsCuiMappingFilename);
        
        // We create an in-memory graph in which we will load Snomed-CT.
        // Notice that Snomed-CT is quite large (e.g. version 20120731 contains 296433 concepts and872318 relationships ).
        // You will need to allocate extra memory to the JVM e.g add -Xmx3000m parameter to allocate 3Go.
        
        m_factory = URIFactoryMemory.getSingleton();
        m_snomedctURI = m_factory.getURI("http://snomedct/");
        m_graph = new GraphMemory(m_snomedctURI);

        GDataConf dataConf = new GDataConf(GFormat.SNOMED_CT_RF2);
        
        dataConf.addParameter(GraphLoaderSnomedCT_RF2.ARG_CONCEPT_FILE,
                m_strSnomedDir + "/" + m_strSnomedDBconceptFileName);
        
        dataConf.addParameter(GraphLoaderSnomedCT_RF2.ARG_RELATIONSHIP_FILE,
                m_strSnomedDir + "/" + m_strSnomedDBRelationshipsFileName);

        GraphLoaderGeneric.populate(dataConf, m_graph);
        System.out.println(m_graph.toString());
        
        // We define the engine used to compute the similarity
        
        m_engine = new SM_Engine(m_graph);
    }
    
    /**
     * This function returns the library type
     * @return 
     */
    
    @Override
    public SemanticLibraryType getLibraryType()
    {
        return (SemanticLibraryType.SML);
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     * * @return true if the measure is allowed
     */
    
    @Override
    public boolean setSimilarityMeasure(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType) throws Exception
    {
        // SML library is unable to run any path-based measure in a reasonable time,
        // it takes hours on a moderated-size ontology because its shortest-path
        // algortihm is very inneficcient. For this reason,
        // we skip the evaluation of the measures below
        
        boolean isAccepted = (measureType != SimilarityMeasureType.AncSPLRada)
                            && (measureType != SimilarityMeasureType.Rada)
                            && (measureType != SimilarityMeasureType.WuPalmer)
                            && (measureType != SimilarityMeasureType.WuPalmerFast);
        
        if (isAccepted)
        {
            // We convert the measure type to the SML measure types

            String strMeasure = convertHesmlMeasureTypeToSML(measureType);

            // We force the Seco IC model

            m_icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SECO_2004);

            // Then we configure the pairwise measure to use, we here choose to use Lin formula

            m_smConf = new SMconf(strMeasure, m_icConf);
        }
        
        // We reutnr the result
        
        return (isAccepted);
    }
    
    /**
     * This function converts a HESML similarity measure type to the
     * closest measure type in UMLS::Similarity.
     * @param hesmlMeasureType
     * @return
     * @throws Exception 
     */
    
    private String convertHesmlMeasureTypeToSML(
            SimilarityMeasureType   hesmlMeasureType) throws Exception
    {
        // We fill a hasmMap with the conversion
        
        HashMap<SimilarityMeasureType, String> conversionMap = new HashMap<>();
        
        // We fill the conversion map
        
        conversionMap.put(SimilarityMeasureType.Lin, SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998);
        conversionMap.put(SimilarityMeasureType.Resnik, SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_RESNIK_1995);
        conversionMap.put(SimilarityMeasureType.JiangConrath, SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_JIANG_CONRATH_1997_NORM);
        conversionMap.put(SimilarityMeasureType.CosineNormJiangConrath, SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_JIANG_CONRATH_1997_NORM);
        conversionMap.put(SimilarityMeasureType.CosineNormWeightedJiangConrath, SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_JIANG_CONRATH_1997_NORM);
        conversionMap.put(SimilarityMeasureType.WeightedJiangConrath, SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_JIANG_CONRATH_1997_NORM);
        conversionMap.put(SimilarityMeasureType.Rada, SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_RADA_1989);
        conversionMap.put(SimilarityMeasureType.LeacockChodorow, SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_LEACOCK_CHODOROW_1998);
        conversionMap.put(SimilarityMeasureType.PedersenPath, SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_RADA_1989);
        conversionMap.put(SimilarityMeasureType.PekarStaab, SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_PEKAR_STAAB_2002);
        conversionMap.put(SimilarityMeasureType.WuPalmerFast, SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_WU_PALMER_1994);
        conversionMap.put(SimilarityMeasureType.WuPalmer, SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_WU_PALMER_1994);

        // We check that the measure is implemented by thius library
        
        if (!conversionMap.containsKey(hesmlMeasureType))
        {
            throw (new Exception(hesmlMeasureType.toString() +
                    " is not implemented by UMLS::Similarity"));
        }
        
        // We get the output measure tyoe
        
        String strUMLSimMeasureType = conversionMap.get(hesmlMeasureType);
        
        // We release the conversion table
        
        conversionMap.clear();
        
        // We return the result
        
        return (strUMLSimMeasureType);
    }
   
    /**
     * Unload the ontology
     */
    
    @Override
    public void unloadOntology()
    {
        // Werelease the CUI mapping table
        
        if (m_indexedSnomedIDsByCUI != null)
        {
            for (HashSet<Long> snomedIDs: m_indexedSnomedIDsByCUI.values())
            {
                snomedIDs.clear();
            }
        }
        
        // We only disconnect this objects because SML does not provide
        // functios to clear its objects
        
        m_graph = null;
        m_factory = null;
        m_engine = null;
        
        // We force the release of memory
        
        System.gc();
    }
}

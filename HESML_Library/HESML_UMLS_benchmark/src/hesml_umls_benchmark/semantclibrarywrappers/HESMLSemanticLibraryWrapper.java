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

package hesml_umls_benchmark.semantclibrarywrappers;

import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHDescriptor;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.ISemanticLibrary;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class implements the SNOMED similarity library based on HESML.
 * @author j.lastra
 */

public class HESMLSemanticLibraryWrapper extends SimilarityLibraryWrapper
        implements ISemanticLibrary
{
    /**
     * SNOMED ontology implemented into HESML
     */
    
    private ISnomedCtOntology   m_hesmlSnomedOntology;
    
    /**
     * MeSH ontoogy loaded into HESML
     */
    
    private IMeSHOntology   m_hesmlMeshOntology;
    
    /**
     * Active semantic similarity measure
     */
    
    private ISimilarityMeasure  m_hesmlSimilarityMeasure; 
    
    /**
     * Taxonomy and vertexes contained in the HESML taxonomy encoding SNOMED
     */
    
    private IVertexList m_hesmlVertexes;
    private ITaxonomy   m_taxonomy;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsCuiMappingFilename
     * @throws Exception 
     */
    
    HESMLSemanticLibraryWrapper(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strUmlsCuiMappingFilename) throws Exception
    {
        // Inicializamos la clase base
        
        super(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strUmlsCuiMappingFilename);
        
        // We initialize the class
        
        m_hesmlSimilarityMeasure = null;
        m_hesmlSnomedOntology = null;
        m_hesmlMeshOntology = null;
        m_hesmlVertexes = null;
        m_taxonomy = null;
    }

    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsCuiMappingFilename
     * @throws Exception 
     */
    
    HESMLSemanticLibraryWrapper(
            String  strMeSHDir,
            String  strMeSHXmlFileName,
            String  strUmlsDir,
            String  strUmlsCuiFilename) throws Exception
    {
        // Inicializamos la clase base
        
        super(strMeSHDir, strMeSHXmlFileName,
                strUmlsDir, strUmlsCuiFilename);
        
        // We initialize the class
        
        m_hesmlSimilarityMeasure = null;
        m_hesmlSnomedOntology = null;
        m_hesmlMeshOntology = null;
        m_hesmlVertexes = null;
        m_taxonomy = null;
    }
    
    /**
     * This function returns the SNOMED taxonomy
     * @return 
     */
    
    public ITaxonomy getTaxonomy()
    {       
        return (m_taxonomy);
    }
    
    /**
     * This function returns the library type
     * @return 
     */
    
    @Override
    public SemanticLibraryType getLibraryType()
    {
        return (SemanticLibraryType.HESML);
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
     * This function returns the degree of similarity between two CUI concepts.
     * @param strFirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */

    @Override
    public double getSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We compute the similarity using SNOMED or MeSH
        
        double similarity = (m_hesmlSnomedOntology != null) ?
                            getSnomedSimilarity(strFirstUmlsCUI, strSecondUmlsCUI) :
                            getMeSHSimilarity(strFirstUmlsCUI, strSecondUmlsCUI);
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function returns the degree of similarity between two CUI concepts
     * evaluated on SNOMED.
     * @param strFirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */

    private double getSnomedSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We initilizae the output
        
        double similarity = 0.0;
        
        // We get the SNOMED concepts evoked by each CUI
        
        ISnomedConcept[] firstSnomedConcepts = m_hesmlSnomedOntology.getConceptsForUmlsCUI(strFirstUmlsCUI);
        ISnomedConcept[] secondSnomedConcepts = m_hesmlSnomedOntology.getConceptsForUmlsCUI(strSecondUmlsCUI);
        
        // We check the existence oif SNOMED concepts associated to the CUIS
        
        if ((firstSnomedConcepts.length > 0)
                && (secondSnomedConcepts.length > 0))
        {
            // We initialize the maximum similarity
            
            double maxSimilarity = Double.NEGATIVE_INFINITY;
            
            // We compare all pairs of evoked SNOMED concepts
            
            for (int i = 0; i < firstSnomedConcepts.length; i++)
            {
                Long snomedId1 = firstSnomedConcepts[i].getSnomedId();
                
                for (int j = 0; j < secondSnomedConcepts.length; j++)
                {
                    Long snomedId2 = secondSnomedConcepts[j].getSnomedId();
                    
                    // We evaluate the similarity measure
        
                    double snomedSimilarity = m_hesmlSimilarityMeasure.getSimilarity(
                                            m_hesmlVertexes.getById(snomedId1),
                                            m_hesmlVertexes.getById(snomedId2));
                    
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
     * This function returns the degree of similarity between two CUI concepts
     * evaluated on MeSH.
     * @param strFirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */

    private double getMeSHSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We initilizae the output
        
        double similarity = 0.0;
        
        // We get the SNOMED concepts evoked by each CUI
        
        IMeSHDescriptor[] firstMeshConcepts = m_hesmlMeshOntology.getConceptsForUmlsCUI(strFirstUmlsCUI);
        IMeSHDescriptor[] secondMeshConcepts = m_hesmlMeshOntology.getConceptsForUmlsCUI(strSecondUmlsCUI);
        
        // We check the existence of MeSH concepts associated to the CUIS
        
        if ((firstMeshConcepts.length > 0)
                && (secondMeshConcepts.length > 0))
        {
            // We initialize the maximum similarity
            
            double maxSimilarity = Double.NEGATIVE_INFINITY;
            
            // We compare all pairs of evoked MeSH tree nodes. Note that a UMLS
            // CUI evokes multiple MeSH concepts (descriptors), and every MeSH
            // descriptor has multiples tree nodes concepts. Thus, we consider
            // that a CUI concept evokes the full merge of all tree nodes
            // evoked by all its evoked MeSH descriptors.
            
            // We get all tree nodes associated to the first CUI
            
            HashSet<Long> firstTreeNodes = new HashSet<>();
            
            for (int i = 0; i < firstMeshConcepts.length; i++)
            {
                // We get all tree nodes associtaed to the MeSH descriptor
                
                Long[] nodeIds1 = firstMeshConcepts[i].getTaxonomyNodesId();
                
                // We register all node Ids
                
                firstTreeNodes.addAll(Arrays.asList(nodeIds1));
            }

            // We get all tree nodes associated to the second CUI
            
            HashSet<Long> secondTreeNodes = new HashSet<>();
            
            for (int i = 0; i < secondMeshConcepts.length; i++)
            {
                // We get all tree nodes associtaed to the MeSH descriptor
                
                Long[] nodeIds2 = secondMeshConcepts[i].getTaxonomyNodesId();
                
                // We register all node Ids
                
                secondTreeNodes.addAll(Arrays.asList(nodeIds2));
            }
            
            // We compute the similarity for each pair of tree nodes
            
            for (Long node1: firstTreeNodes)
            {
                for (Long node2: secondTreeNodes)
                {
                    double snomedSimilarity = m_hesmlSimilarityMeasure.getSimilarity(
                                        m_hesmlVertexes.getById(node1),
                                        m_hesmlVertexes.getById(node2));
                
                    // We update the maximum similarity

                    if (snomedSimilarity > maxSimilarity) maxSimilarity = snomedSimilarity;
                }
            }
            
            // We release the auxiliary sets
            
            firstTreeNodes.clear();
            secondTreeNodes.clear();
            
            // We assign the output similarity value
            
            similarity = maxSimilarity;
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     * @return true if the measure is allowed
     */
    
    @Override
    public boolean setSimilarityMeasure(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType) throws Exception
    {
        // We force the loading of the SNOMED database
        
        loadOntology();
        
        // We set the IC model in the taxonomy
        
        if (measureType != SimilarityMeasureType.Rada)
        {
            System.out.println("Setting the " + icModel.toString() + " IC model into the SNOMED-CT  taxonomy");
            
            ICModelsFactory.getIntrinsicICmodel(icModel).setTaxonomyData(m_taxonomy);
        }
        
        // We get the Lin similarity measure
        
        m_hesmlSimilarityMeasure = MeasureFactory.getMeasure(m_taxonomy, measureType);
        
        // We return the result
        
        return (true);
    }
    
    /**
     * Load the ontology
     */
    
    @Override
    public void loadOntology() throws Exception
    {
        // We load the SNOMED ontology and get the vertex list of its taxonomy
     
        if ((m_strSnomedDir != "") && (m_hesmlSnomedOntology == null))
        {
            m_hesmlSnomedOntology = SnomedCtFactory.loadSnomedDatabase(m_strSnomedDir,
                                    m_strSnomedDBconceptFileName,
                                    m_strSnomedDBRelationshipsFileName,
                                    m_strSnomedDBdescriptionFileName,
                                    m_strUmlsDir, m_strUmlsCuiMappingFilename);
            
            m_taxonomy = m_hesmlSnomedOntology.getTaxonomy();
            m_hesmlVertexes = m_taxonomy.getVertexes();
        }
        
        // We load the MeSH ontology and get the vertex list of its taxonomy
     
        if ((m_strMeSHDir != "") && (m_hesmlMeshOntology == null))
        {
            m_hesmlMeshOntology = MeSHFactory.loadMeSHOntology(
                                    m_strMeSHDir + "/" + m_strMeSHXmlFilename,
                                    m_strUmlsDir + "/" + m_strUmlsCuiMappingFilename);
            
            m_taxonomy = m_hesmlMeshOntology.getTaxonomy();
            m_hesmlVertexes = m_taxonomy.getVertexes();
        }
    }
    
    /**
     * Unload the ontology
     */
    
    @Override
    public void unloadOntology()
    {
        // We unload the ontologies
        
        if (m_hesmlSnomedOntology != null) m_hesmlSnomedOntology.clear();
        if (m_hesmlMeshOntology != null) m_hesmlMeshOntology.clear();
    }
}

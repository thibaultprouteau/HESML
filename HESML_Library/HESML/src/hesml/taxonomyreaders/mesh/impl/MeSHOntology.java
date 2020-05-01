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
 */

package hesml.taxonomyreaders.mesh.impl;

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertexList;
import hesml.taxonomy.impl.TaxonomyFactory;
import hesml.taxonomyreaders.mesh.IMeSHDescriptor;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;

/**
 * This class implements an in-memory representation of the whole MeSH taxonomy
 * @author Juan J. Lastra-Díaz <jlastra@invi.uned.es>
 */

class MeSHOntology implements IMeSHOntology
{
    /**
     * MeSH descriptors (concepts) indexed by MeSH ID
     */
    
    private HashMap<String, MeSHDescriptor> m_conceptsByKeyname;
    
    /**
     * MeSH descriptos indexed by its preferred name
     */
    
    private HashMap<String, MeSHDescriptor> m_conceptsByPreferredName;
    
    /**
     * MeSH concepts indexed by UMLS CUI.
     */
    
    private HashMap<String, IMeSHDescriptor[]>  m_conceptsByUmlsCUI;
    
    /**
     * Array with a BFS ordereing of the all MeSH concepts (descriptrs)
     */
    
    private IMeSHDescriptor[]   m_meshConcepts;
    
    /**
     * MeSH taxonomy
     */
    
    private ITaxonomy   m_Taxonomy;
    
    /**
     * This hashmap sets the mapping from a MeSH tree node to a vertex in the
     * taxonomy.
     */
    
    private HashMap<String, Long>   m_TreeNodeToTaxonomyVertexMap;
    
    /**
     * Constructor
     * @param descriptors 
     */
    
    MeSHOntology()
    {
        m_meshConcepts = null;
        m_Taxonomy = null;
        m_conceptsByKeyname = new HashMap<>();
        m_TreeNodeToTaxonomyVertexMap = new HashMap<>();
        m_conceptsByPreferredName = new HashMap<>();
        m_conceptsByUmlsCUI = new HashMap<>();
    }
    
    /**
     * This function inserts a MeSH descriptor (concept) into the ontology
     */
    
    public void addDescriptor(
            String      strDescriptorId,
            String      strPreferredName,
            String[]    strTreeNodeIds)
    {
        // We insert a new concept in the ontology
        
        m_conceptsByKeyname.put(strDescriptorId,
                new MeSHDescriptor(strDescriptorId, strPreferredName, strTreeNodeIds));
    }
    
    /**
     * This function populates the taxonomy by traversiong the current
     * collection of MeSH descriptors.
     */
    
    public void buildOntology(
        boolean useAncestorsCaching) throws Exception
    {
        // We create the final array of concepts
        
        m_meshConcepts = new IMeSHDescriptor[m_conceptsByKeyname.size()];
        m_conceptsByKeyname.values().toArray(m_meshConcepts);
        
        // We create the taxonomy
        
        m_Taxonomy = TaxonomyFactory.createBlankTaxonomy(m_conceptsByKeyname.size());
        
        // We insert the root node becasue all MeSH trees are independent
        
        m_Taxonomy.addVertex(0L, new Long[0]);
        m_TreeNodeToTaxonomyVertexMap.put("", 0L);
        
        // We traverse the collection of descriptors
        
        for (IMeSHDescriptor descriptor: m_meshConcepts)
        {
            // We get all tree nodes
            
            for (String strTreeNode: descriptor.getTreeNodeIds())
            {
                // We retrieve all nodes in the path from the tree node to the root
                
                String[] strPathNodes = getDecomposedPathNodes(strTreeNode);
                
                // We insert all nodes in the path from the root
                
                for (int i = 0; i < strPathNodes.length; i++)
                {
                    // We chekc if the node is not currently included in the taxonomy
                    
                    if (!m_TreeNodeToTaxonomyVertexMap.containsKey(strPathNodes[i]))
                    {
                        // We get the node keyname and the keyname of its parent

                        String strParentKeyname = (i > 0) ? strPathNodes[i-1] : "";
                        
                        // We set hte parents list
                        
                        Long[] parentIds = {m_TreeNodeToTaxonomyVertexMap.get(strParentKeyname)};
                        
                        // We set the ID of the new vertex in the taxonomy
                        
                        int newNodeId = m_Taxonomy.getVertexes().getCount();
                        
                        // We insert the new vertex in the taxonomy
                        
                        m_Taxonomy.addVertex((long) newNodeId, parentIds);
                        
                        // We register the vertex ID and insert the vertex
                        
                        m_TreeNodeToTaxonomyVertexMap.put(strPathNodes[i], (long) newNodeId);
                    }
                }
            }
        }
        
        // We compute all cached information
        
        m_Taxonomy.computesCachedAttributes();
        
        // From HESML V1R5, we support the caching of the ancestor sets in
        // each vertex of the taxonomy with the aim of speeding up the 
        // computation of MICA vertex in high-complexity ontologies as
        // SNOMED-CT.
        
        if (useAncestorsCaching) m_Taxonomy.computeCachedAncestorSet();
        
        // Debugging message
        
        System.out.println("MeSH ontology has been loaded ("
                + m_Taxonomy.getVertexes().getCount() + ") nodes");
    }
    
    /**
     * This function decomposes the keyname of a tree node for recovering
     * all ndos in the path by using the hierchical naming rule.
     * @return 
     */
    
    private String[] getDecomposedPathNodes(
        String  strTreeNodeId)
    {
        // We decompose the node keyname to extract all nods in the pàth.
        
        String[] strNodes = strTreeNodeId.split("\\.");
        
        // We creaete the output vector containing all nodes in the path
        
        for (int i = 1; i < strNodes.length; i++)
        {
            strNodes[i] = strNodes[i - 1] + "." + strNodes[i];
        }
        
        // We return the output
        
        return (strNodes);
    }
    /**
     * This function returns the vertex Ids in the HESML taxonomy corresponding
     * to the input tree nodes in the MeSH taxonomy.
     * @param strTreeNodes
     * @return 
     */
    
    @Override
    public Long[] getVertexIdsForTreeNodes(
        String[]    strTreeNodes)
    {
        // We create the output array
        
        Long[] vertexesIds = new Long[strTreeNodes.length];
        
        // We extract the vertexes Ids
        
        for (int i = 0; i < strTreeNodes.length; i++)
        {
            vertexesIds[i] = m_TreeNodeToTaxonomyVertexMap.containsKey(strTreeNodes[i]) ?
                            m_TreeNodeToTaxonomyVertexMap.get(strTreeNodes[i]) : -1L;
        }
        
        // We return the result
        
        return (vertexesIds);
    }
    
    /**
     * This function 
     * @param strUmlsCui
     * @return 
     */
    
    @Override
    public IVertexList getVertexesForUMLSCui(
            String strUmlsCui) throws Exception
    {
        // We ghet the MeSH concepts evoked by the UMLS CUI
        
        IMeSHDescriptor[] cuiEvokedConcepts = getConceptsForUmlsCUI(strUmlsCui);
        
        // We create an axuliary set to extract the tree node Ids
        
        HashSet<Long> auxVertexesId = new HashSet<>();
        
        for (IMeSHDescriptor concept: cuiEvokedConcepts)
        {
            for (String strTreeNodeId: concept.getTreeNodeIds())
            {
                auxVertexesId.add(m_TreeNodeToTaxonomyVertexMap.get(strTreeNodeId));
            }
        }
        
        // We convert the set of vertex Ids into an array
        
        Long[] vertexesId = new Long[auxVertexesId.size()];
        
        auxVertexesId.toArray(vertexesId);
        auxVertexesId.clear();
        
        // We get the output vector
        
        IVertexList cuiVertexesInTaxonomy = m_Taxonomy.getVertexes().getByIds(vertexesId);
        
        // We return the result
        
        return (cuiVertexesInTaxonomy);
    }

    /**
     * This functions determines if there is a descriptor with this keyname.
     * @param strPreferredName
     * @return True if thre is a MeSH descriptor with this keyname
     */
    
    @Override
    public boolean containsDescriptorByID(
            String strDescriptorID)
    {
        return (m_conceptsByKeyname.containsKey(strDescriptorID));
    }

    /**
     * This function implements the Iterable interface.
     * @return Iterator
     */
    
    @Override
    public Iterator<IMeSHDescriptor> iterator()
    {
        return (new MeSHconceptArrayIterator(m_meshConcepts));
    }
    
    /**
     * This function returns the number of concepts in the ontology.
     * @return 
     */
    
    @Override
    public int getConceptCount()
    {
        return (m_conceptsByKeyname.size());
    }
    
    /**
     * This function returns the MeSH concepts associated to the CUI
     * or an empty array if it is not found in the MeSH database.
     * @param strUmlsCUI
     * @return 
     */
    
    @Override
    public IMeSHDescriptor[] getConceptsForUmlsCUI(
            String strUmlsCUI)
    {
        // We retrieve the MeSH concepts associated to the input UMLS CUI
        
        IMeSHDescriptor[] evokedConcepts = m_conceptsByUmlsCUI.containsKey(strUmlsCUI) ?
                (IMeSHDescriptor[]) m_conceptsByUmlsCUI.get(strUmlsCUI).clone() :
                new IMeSHDescriptor[0];
        
        // We return the result
        
        return (evokedConcepts);
    }

    /**
     * This function returns the MeSH concepts associated to the CUIs
     * or null if they are not found in the MeSH ontology.
     * @param strUmlsCUIs
     * @return 
     */
    
    @Override
    public IMeSHDescriptor[] getConceptsForUmlsCUIs(
            String[] strUmlsCUIs)
    {
        // We create the temporary set
        
        HashSet<IMeSHDescriptor> evokedConceptSet = new HashSet<>();
        
        // We retrieve the concepts for each CUI and merge them
        
        for (String strCui : strUmlsCUIs)
        {
            IMeSHDescriptor[] concepts = getConceptsForUmlsCUI(strCui);
            
            for (IMeSHDescriptor descriptor: concepts)
            {
                evokedConceptSet.add(descriptor);
            }
        }
     
        // We create the output vector
        
        IMeSHDescriptor[] evokedConcepts = new IMeSHDescriptor[evokedConceptSet.size()];
        
        // We copy the list of evoked MeSH conceots and release the auxilairy set
        
        evokedConceptSet.toArray(evokedConcepts);
        evokedConceptSet.clear();
        
        // We return the result
        
        return (evokedConcepts);
    }

    /**
     * This function returns an ordered array with all descriptor in the
     * MeSH ontology. Concepts are totally ordered from the root in a BFS mode.
     * @return 
     */
    
    @Override
    public IMeSHDescriptor[] getAllDescriptors()
    {
        return (m_meshConcepts);
    }
    
    /**
     * This function returns all MeSH descriptors associated to the input term.
     * @param strPreferredName The term whose concepts will be retrieved
     * @return An array of MeSH descriptors
     * @throws java.lang.Exception Unexpected error
     */

    @Override
    public IMeSHDescriptor getConceptByPreferredName(
        String  strPreferredName) throws Exception
    {
        return (m_conceptsByPreferredName.get(strPreferredName));
    }
    
    /**
     * This function returns a vector with the CUID values of the
     * concepts evoked by the input term.
     * @param strTerm Input term
     * @return A sequence of MeSH descriptor Ids values corresponding to
     * the MeSH descriptors (concepts) evoked by the input term
     * @throws Exception Unexpected error
     */
    
    @Override
    public String[] getMeSHConceptsEvokedByTerm(
        String  strTerm) throws Exception
    {
        return (null);
    }
       
    /**
     * This function returns the concept associated to the input CUID
     * @param meshDescriptorId
     * @return The concept for this meshDescriptorId
     */
    
    @Override
    public IMeSHDescriptor getConceptById(String meshDescriptorId)
    {
        return (m_conceptsByKeyname.get(meshDescriptorId));
    }
    
    /**
     * This function returns the HESML taxonomy encoding the MeSH 'is-a' ontology.
     * @return In-memory HESML taxonomy encoding the 'is-a' SNOMED-CT graph
     */
    
    @Override
    public ITaxonomy getTaxonomy()
    {
        return (m_Taxonomy);
    }
    
    /**
     * Clear the database
     */
    
    @Override
    public void clear()
    {
        m_TreeNodeToTaxonomyVertexMap.clear();
        m_conceptsByPreferredName.clear();
        m_conceptsByUmlsCUI.clear();
        m_conceptsByKeyname.clear();
        
        if (m_Taxonomy != null) m_Taxonomy.clear();
    }
}

/**
 * This class implements an iterator on an array of concepts
 * @author j.lastra
 */

class MeSHconceptArrayIterator implements Iterator<IMeSHDescriptor>
{
    /**
     * MeSH concept array
     */
    
    private final IMeSHDescriptor[] m_Concepts;
    
    /**
     * Reading cursor
     */
    
    private int m_ReadingPosition;
    
    /**
     * Constructor
     * @param concepts 
     */
    
    MeSHconceptArrayIterator(IMeSHDescriptor[] concepts)
    {
        m_Concepts = concepts;
        m_ReadingPosition = 0;
    }
    
    /**
     * This function checks wheter there is pending concepts for reading.
     * @return 
     */
    
    @Override
    public boolean hasNext()
    {
        return (m_ReadingPosition < m_Concepts.length);
    }

    /**
     * This function returns the next concept in tue collection
     * @return 
     */
    
    @Override
    public IMeSHDescriptor next()
    {
        return (m_Concepts[m_ReadingPosition++]);
    }
}
/*
 * Copyright (C) 2020 alicia
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
package hesml.sts.preprocess.impl;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Position;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;
import hesml.sts.preprocess.IAnnotationProcess;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 *  This class annotates the sentences using the METAMAP annotator.
 * 
 *  @author alicia
 */

class AnnotationProcess implements IAnnotationProcess
{
    // Array with the metamap semantic types excluded from the annotation.
    // The existing semantic types can be downloaded from the original resource:
    // https://metamap.nlm.nih.gov/SemanticTypesAndGroups.shtml
    
    private final String m_semanticTypesExcluded;
    
    private final MetaMapApi m_apiJavaMetamap;
    
    /**
     * Constructor with parameters
     * @param semanticTypesExcluded 
     */
    
    AnnotationProcess(String semanticTypesExcluded)
    {
        // Initialize the variables
        
        m_semanticTypesExcluded = semanticTypesExcluded;
        
        // Initialize the metamap api
        
        m_apiJavaMetamap = new MetaMapApiImpl("localhost");
    }
    
    /**
     * Annotate a sentence with METAMAP
     * 
     * MetaMap options available in the api:

        -@ --WSD <hostname>                   : Which WSD server to use.
        -8 --dynamic_variant_generation       : dynamic variant generation
        -A --strict_model                     : use strict model 
        -C --relaxed_model                    : use relaxed model 
        -D --all_derivational_variants        : all derivational variants
        -J --restrict_to_sts <semtypelist>    : restrict to semantic types
        -K --ignore_stop_phrases              : ignore stop phrases.
        -R --restrict_to_sources <sourcelist> : restrict to sources
        -S --tagger <sourcelist>              : Which tagger to use.
        -V --mm_data_version <name>           : version of MetaMap data to use.
        -X --truncate_candidates_mappings     : truncate candidates mapping
        -Y --prefer_multiple_concepts         : prefer multiple concepts
        -Z --mm_data_year <name>              : year of MetaMap data to use.
        -a --all_acros_abbrs                  : allow Acronym/Abbreviation variants
        -b --compute_all_mappings             : compute/display all mappings
        -d --no_derivational_variants         : no derivational variants
        -e --exclude_sources <sourcelist>     : exclude sources
        -g --allow_concept_gaps               : allow concept gaps
        -i --ignore_word_order                : ignore word order
        -k --exclude_sts <semtypelist>        : exclude semantic types
        -l --allow_large_n                    : allow Large N
        -o --allow_overmatches                : allow overmatches 
        -r --threshold <integer>              : Threshold for displaying candidates. 
        -y --word_sense_disambiguation        : use WSD 
        -z --term_processing                  : use term processing 
        
        api.setOptions("-yk dsyn");  // turn on Word Sense Disambiguation and
                                     // exclude concepts with dsyn
                                     // (disease or syndrome)
        
     * 
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String annotate(String strRawSentence) throws Exception
    {
        // Initialize the result
        
        StringBuilder annotatedSentence = new StringBuilder(strRawSentence);
        
        // Initialize the hashmap that will contain the CUI codes to be annotated and their positions.
        
        TreeMap<Integer, String> annotatedCUIPositions = new TreeMap(Collections.reverseOrder());
        
        // Add the configuration for Metamap
        
        String optionsApiJavaMetamap = "-dtyI";
        
        // Add the excluded semantic types (if exists)
        
        optionsApiJavaMetamap = (m_semanticTypesExcluded != "")? optionsApiJavaMetamap + "-k " + m_semanticTypesExcluded: optionsApiJavaMetamap;
        
        // Set the options to the Java API
        
        m_apiJavaMetamap.setOptions(optionsApiJavaMetamap);  
        
        // Parse the text with Metamap and iterate the results
        
        List<Result> resultList = m_apiJavaMetamap.processCitationsFromString(strRawSentence);
        
        for (Result result: resultList) 
        {
            // The instance method Result.getUtteranceList() produces a list of the utterances present in the result
            
            for (Utterance utterance: result.getUtteranceList()) 
            {
                // To get the list of phrases, candidates, and mappings associated with an utterance use the instance method Utterance.getPCMList
                
                for (PCM pcm: utterance.getPCMList()) 
                {
                    // One can get the mappings list from the PCM instance using PCM.getMappingList
                    
                    for (Mapping map: pcm.getMappingList()) 
                    {
                        // Iterate each keyphrase for each utterance
                        
                        for (Ev mapEv: map.getEvList()) 
                        {
                            // Get the CUI concept
                            
                            String conceptId = mapEv.getConceptId();
                            
                            // Get the Semantic Type
                            
                            List<String> semanticTypes = mapEv.getSemanticTypes();
                            
                            // Get the positions of the matches
                            
                            List<Position> positionalInfo = mapEv.getPositionalInfo();
                            String strCuiAndTotalChars = "";
                            Collections.reverse(positionalInfo);
                            
                            for(Position position: positionalInfo)
                            {
                                // Get the position information for each match and annotate the new sentence.
                                
                                Integer pos_init = position.getX();
                                Integer totalChars = position.getY();
                                strCuiAndTotalChars = conceptId + "-" + totalChars.toString();
                                
                                // Add the annotated CUI and the positional info
                                
                                if(!annotatedCUIPositions.containsKey(pos_init))
                                {
                                    annotatedCUIPositions.put(pos_init, strCuiAndTotalChars);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Iterate the selected CUI codes in reverse order and annotate them in the sentence.

        for (java.util.Map.Entry<Integer, String> entry : annotatedCUIPositions.entrySet())
        {
            // Get the position in the sentence and the lenght of the keyphrase to replace
            
            int position_init = entry.getKey();
            
            String[] strCuiAndTotalChars = entry.getValue().split("-");
            String strCui = strCuiAndTotalChars[0];
            int total_chars = Integer.parseInt(strCuiAndTotalChars[1]);
            
            // Annotate the CUI code in the sentence removing the keyphrase.

            annotatedSentence = annotatedSentence.replace(position_init, position_init+total_chars, strCui);
        }
        
//        System.out.println("Before: " + strRawSentence);
//        System.out.println("After: " + annotatedSentence.toString());

        // Return the result
        
        return annotatedSentence.toString();
    }
    
    /**
     * This function releases all resources used by the object.
     */

    public void clear()
    {
        m_apiJavaMetamap.disconnect();
    }
    
}
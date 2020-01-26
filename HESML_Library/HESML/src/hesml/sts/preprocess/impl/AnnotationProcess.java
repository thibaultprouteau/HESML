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

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Result;
import hesml.sts.preprocess.IAnnotationProcess;
import java.util.List;

/**
 *  This class annotates the sentences (ex. METAMAP annotator).
 * 
 *  @author alicia
 */

public class AnnotationProcess implements IAnnotationProcess
{

    /**
     * Annotate with metamap
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
        
        String annotatedSentence = strRawSentence;
        
        MetaMapApi apiJavaMetamap = new MetaMapApiImpl("localhost");
        apiJavaMetamap.setOptions("-yb");  
        
        List<Result> resultList = apiJavaMetamap.processCitationsFromString(strRawSentence);
        
//        Result result = resultList.get(0);
//        List<AcronymsAbbrevs> aaList = result.getAcronymsAbbrevs();
//        if (aaList.size() > 0) {
//          System.out.println("Acronyms and Abbreviations:");
//          for (AcronymsAbbrevs e: aaList) {
//            System.out.println("Acronym: " + e.getAcronym());
//            System.out.println("Expansion: " + e.getExpansion());
//            System.out.println("Count list: " + e.getCountList());
//            System.out.println("CUI list: " + e.getCUIList());
//          }
//        } else {
//          System.out.println(" None.");
//        }

        // Return the result

        return annotatedSentence;
    }
}
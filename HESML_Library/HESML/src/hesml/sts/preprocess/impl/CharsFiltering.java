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
package hesml.sts.preprocess.impl;

import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.ICharsFiltering;
import java.util.HashMap;

/**
 *  This class implements the punctuation marks 
 *  filtering methods used in the papers.
 * @author alicia
 */

public class CharsFiltering implements ICharsFiltering
{
    /**
     * Mapping holding the pairs (input chain, output chain) for all
     * detailed string replacements.
     */
    
    private HashMap<String, String> m_ReplacingMap;
    
    /**
     *  Constructor
     * @param charFilteringType
     */
    
    public CharsFiltering(
            CharFilteringType charFilteringType)
    {
        // We create the replcament mapping
        
        m_ReplacingMap = new HashMap<>();
        
        // We set the replcament patterns for each pre-defined method.
        
        switch (charFilteringType)
        {
            case Default:
                
                // Remove all the punctuation marks using the Java preexisting regex.
                // 	Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
                // strFilteredSentence = strRawSentence.replaceAll("\\p{Punct}",""); 
                
                m_ReplacingMap.put("[^\\p{Alpha}\\p{Digit}]+"," ");
                
                break;
                
            case BIOSSES:
                
                setBiosssesFilteringPatterns();
                
                break;
                
            case Blagec2019:
                
                setBlagecFilteringPatterns();
                
                break;
        }
        
        // We also register the last extra words applied as last filtering
        
        m_ReplacingMap.put("\\s{2,}", " ");
    }
    
    /**
     * Filter the sentence punctuation marks
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String filter(
            String strRawSentence)
    {
        // We apply the first triming
        
        String strFilteredSentence = strRawSentence.trim(); 
        
        // We apply all replacements by substituting all search patterns
        // registered in the global mapping.
        
        for (String strToBeReplaced : m_ReplacingMap.keySet())
        {
            strFilteredSentence = strFilteredSentence.replaceAll(strToBeReplaced,
                                    m_ReplacingMap.get(strToBeReplaced));
        }
        
        // We apply the last triming. Extra words are removed by the last replcament pattern.
                
        strFilteredSentence = strFilteredSentence.trim();
        
        // Return the result
        
        return (strFilteredSentence);
    }
    
    /**
     * Replace the punctuation marks as the BIOSSES2017 original code does.
     * @param strRawSentence
     * @return 
     */
    
    private void setBiosssesFilteringPatterns()
    {
        // We register all string replcaments
        
        m_ReplacingMap.put("\\.","");
        m_ReplacingMap.put(";","");
        m_ReplacingMap.put("-","");
        m_ReplacingMap.put(":","");
        m_ReplacingMap.put(",","");
        m_ReplacingMap.put("_","");
        m_ReplacingMap.put("!", "");
        m_ReplacingMap.put("\\(", "");
        m_ReplacingMap.put("\\)", "");
        m_ReplacingMap.put("\\[", "");
        m_ReplacingMap.put("\\]", "");
        m_ReplacingMap.put("\\*", "");
        m_ReplacingMap.put("/", "");
        m_ReplacingMap.put("\\?", "");
    }
    
    /**
     * Replace the punctuation marks for string measures described in Blagec2019.
     * @param strRawSentence
     * @return 
     */
    
    private void setBlagecFilteringPatterns()
    {
        // We set the filtering chain
        
        m_ReplacingMap.put("\\.","");
        m_ReplacingMap.put(",","");
        m_ReplacingMap.put(":","");
        m_ReplacingMap.put(";","");
        m_ReplacingMap.put("\\?", "");
        m_ReplacingMap.put("!", "");
        m_ReplacingMap.put("/", "");     
        m_ReplacingMap.put("-","");
    }
}

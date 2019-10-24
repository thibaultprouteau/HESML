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

/**
 *  This class implements the punctuation marks filtering methods used in the papers.
 * @author alicia
 */

public class CharsFiltering implements ICharsFiltering
{

    /**
     * Type of filtering method
     */
    
    private CharFilteringType m_charFilteringType;

    /**
     *  Constructor
     * @param charFilteringType
     */
    
    public CharsFiltering(CharFilteringType charFilteringType)
    {
        m_charFilteringType = charFilteringType;
    }
    
    /**
     * Filter the sentence punctuation marks
     * @param strRawSentence
     * @return 
     */
    
    @Override
    public String filter(String strRawSentence)
    {
        String strFilteredSentence = null; // Returned value
        
        // Filter the punctuation marks by the selected type
        
        switch (m_charFilteringType)
        {
            case None:
                
                // Skip the filtering 
                
                strFilteredSentence = strRawSentence;
                break;
                
            case DefaultJava:
                
                //Remove all the punctuation marks using the Java preexisting regex.
                
                strFilteredSentence = strRawSentence.replaceAll("\\p{Punct}",""); 
                break;
                
            case BIOSSES2017:
                strFilteredSentence = replacePunctuations(strRawSentence);
                break;
        }
            
        return strFilteredSentence;
    }
    
    
    /**
     * Replace the punctuation marks as the BIOSSES2017 original code does.
     * @param strRawSentence
     * @return 
     */
    
    public static String replacePunctuations(String strRawSentence)
    {
        strRawSentence = strRawSentence.trim();
        strRawSentence = strRawSentence.replaceAll("\\.","");
        strRawSentence = strRawSentence.replaceAll(";","");
        strRawSentence = strRawSentence.replaceAll("-","");
        strRawSentence = strRawSentence.replaceAll(":","");
        strRawSentence = strRawSentence.replaceAll(",","");
        strRawSentence = strRawSentence.replaceAll("_","");
        strRawSentence = strRawSentence.replaceAll("!", "");
        // strRawSentence = strRawSentence.replace(" " , "");
        strRawSentence = strRawSentence.replaceAll("\\(", "");
        strRawSentence = strRawSentence.replaceAll("\\)", "");
        strRawSentence = strRawSentence.replaceAll("\\[", "");
        strRawSentence = strRawSentence.replaceAll("\\]", "");
        strRawSentence = strRawSentence.replaceAll("\\*", "");
        strRawSentence = strRawSentence.replaceAll("/", "");
        strRawSentence = strRawSentence.replaceAll("\\?", "");
        return strRawSentence.toLowerCase();
    }
    
    /**
     * Getters and setters
     * @return 
     */
    
    @Override
    public CharFilteringType getCharFilteringType()
    {
        return m_charFilteringType;
    }

    @Override
    public void setCharFilteringType(CharFilteringType charFilteringType)
    {
        m_charFilteringType = charFilteringType;
    }
}

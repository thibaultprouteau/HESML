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
package hesml.sts.documentreader.impl;


import java.util.ArrayList;
import java.util.Iterator;
import hesml.sts.documentreader.HSTSISentence;
import hesml.sts.documentreader.HSTSISentenceList;

/**
 * A HSTSSentenceList is an object for iterating between sentences.
 * @author Alicia Lara-Clares
 */

public class HSTSSentenceList implements HSTSISentenceList{
    
    private ArrayList<HSTSISentence> m_sentences;

    public HSTSSentenceList() 
    {
        this.m_sentences = new ArrayList<>();
    }
    
    @Override
    public void addSentence(HSTSISentence sentence)
    {
        m_sentences.add(sentence);
    }
    
    @Override
    public HSTSISentence getSentence(int idSentence)
    {
        return this.m_sentences.get(idSentence);
    }
    
    @Override
    public int getCount()
    {
        return this.m_sentences.size();
    }
    
    @Override
    public Iterator<HSTSISentence> iterator() 
    {
        return (m_sentences.iterator());
    }
    
    
}

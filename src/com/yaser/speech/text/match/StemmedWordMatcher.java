/*
 * Copyright 2011 Greg Milette and Adam Stroud
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yaser.speech.text.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tartarus.snowball.ext.TurkishStemmer;

/**
 * encode strings using a stemmer
 *
 */
public class StemmedWordMatcher extends WordMatcher
{
    public StemmedWordMatcher(String... wordsIn)
    {
        this(Arrays.asList(wordsIn));
    }

    public StemmedWordMatcher(List<String> wordsIn)
    {
        super(encode(wordsIn));
    }
    
    private static List<String> encode(List<String> input)
    {
        List<String> encoded = new ArrayList<String>();
        for (String in : input)
        {
            encoded.add(stem(in));
        }
        return encoded;
    }

    @Override
    public boolean isIn(String word)
    {
        return super.isIn(stem(word));
    } 

    /**
     * run the stemmer from Lucene
     */
    private static String stem(String word)
    {
        TurkishStemmer stemmer = new TurkishStemmer();
        stemmer.setCurrent(word);
        boolean result = stemmer.stem();
        if (!result) 
        {
            return word;
        }
        return stemmer.getCurrent(); 
    }
}

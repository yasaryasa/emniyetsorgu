package com.yaser.speech.text.index;

import org.tartarus.snowball.ext.TurkishStemmer;

/**
 *
 *
 * @author <a href="mailto:gregorym@gmail.com">Greg Milette</a>
 */
public class StemmerLucene
{
    private static TurkishStemmer stemmer;
    
    /**
     * run the stemmer from Lucene
     */
    public static String stem(String word)
    {
        stemmer = new TurkishStemmer();
        stemmer.setCurrent(word);
        boolean result = stemmer.stem();
        if (!result) 
        {
            return word;
        }
        return stemmer.getCurrent(); 
    }
}
/*
 * Copyright 2012 Greg Milette and Adam Stroud
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
package com.yaser.speech.activation;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.yaser.MultiTurnDemo;
import com.yaser.speech.core.SpeechRecognitionUtil;
import com.yaser.speech.core.SpeechRecognizingAndSpeakingActivity;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;

/**
 * Uses direct speech recognition to activate when the user speaks
 * one of the target words
 */
public class WordActivator extends SpeechRecognizingAndSpeakingActivity implements SpeechActivator, RecognitionListener
{
    private static final String TAG = "WordActivator";

    private Context context;
    private SpeechRecognizer recognizer;
    private StemmedWordMatcher matcher;

    private SpeechActivationListener resultListener;

    public WordActivator(Context context,
            SpeechActivationListener resultListener, String... targetWords)
    {
        this.context = context;
        this.matcher = new StemmedWordMatcher(targetWords);
        this.resultListener = resultListener;
    }

    @Override
    public void detectActivation()
    {
        recognizeSpeechDirectly();
        
    }

    private void recognizeSpeechDirectly()
    {
        Intent recognizerIntent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        // accept partial results if they come
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        SpeechRecognitionUtil.recognizeSpeechDirectly(context,
                recognizerIntent, this, getSpeechRecognizer());
        
    }

    public void stop()
    {
        if (getSpeechRecognizer() != null)
        {
            getSpeechRecognizer().stopListening();
            getSpeechRecognizer().cancel();
            getSpeechRecognizer().destroy();
        }
    }

    @Override
    public void onResults(Bundle results)
    {
        Log.d(TAG, "full results");
        receiveResults(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(TAG, "partial results");
        receiveResults(partialResults);
    }

    /**
     * common method to process any results bundle from {@link SpeechRecognizer}
     */
    private void receiveResults(Bundle results)
    {
        if ((results != null)
                && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION))
        {
            List<String> heard =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] scores =
                    results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            receiveWhatWasHeard(heard, scores);
        }
        else
        {
            Log.d(TAG, "no results");
        }
    }

    public void receiveWhatWasHeard(List<String> heard, float[] scores)
    {
        boolean heardTargetWord = false;
        // find the target word
        for (String possible : heard)
        {
            WordList wordList = new WordList(possible);
            if (matcher.isIn(wordList.getWords()))
            {
                Log.d(TAG, "HEARD IT!");
//                getTts().speak("Dinliyorum sahip.", TextToSpeech.QUEUE_ADD, null);
                heardTargetWord = true;
                break;
            }
        }

        if (heardTargetWord)
        {
            stop();
            resultListener.activated(true);
//        	detectActivation();
        }
        else
        {
            // keep going
            recognizeSpeechDirectly();
        }
    }

    @Override
    public void onError(int errorCode)
    {
        if ((errorCode == SpeechRecognizer.ERROR_NO_MATCH)
                || (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT))
        {
            Log.d(TAG, "didn't recognize anything");
            // keep going
            recognizeSpeechDirectly();
        }
        else
        {
            Log.d(TAG,
                    "FAILED "
                            + SpeechRecognitionUtil
                                    .diagnoseErrorCode(errorCode));
        }
    }

    /**
     * lazy initialize the speech recognizer
     */
    private SpeechRecognizer getSpeechRecognizer()
    {
        if (recognizer == null)
        {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
        }
        return recognizer;
    }

    // other unused methods from RecognitionListener...

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(TAG, "ready for speech " + params);
    }

    @Override
    public void onEndOfSpeech()
    {
    	Log.d(TAG, "end for speech " );
    }

    /**
     * @see android.speech.RecognitionListener#onBeginningOfSpeech()
     */
    @Override
    public void onBeginningOfSpeech()
    {
    	System.out.println("beginning");
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {
    }
}

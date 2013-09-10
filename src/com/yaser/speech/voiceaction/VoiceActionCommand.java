package com.yaser.speech.voiceaction;

import com.yaser.speech.text.WordList;


public interface VoiceActionCommand
{
    boolean interpret(WordList heard, float [] confidenceScores);
}

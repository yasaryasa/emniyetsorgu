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
package com.yaser.speech.voiceaction.commands;

import android.content.Context;

import com.yaser.speech.command_utils.AllCommands;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.text.match.WordMatcher;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;

public class PlakaSorguMenu implements VoiceActionCommand
{
    private VoiceActionExecutor executor;
    private Context context;
    private WordMatcher matcher;

    public PlakaSorguMenu(Context context, VoiceActionExecutor executor)
    {
        this.context = context;
        this.executor = executor;
        this.matcher = new StemmedWordMatcher("plaka","araç");
    }

    public boolean interpret(WordList heard, float[] confidence)
    {
        boolean success = false;
        if (matcher.isIn(heard.getWords()))
        {
            executor.execute(AllCommands.INSTANCE.makePlakaMenu(context, executor));
            success = true;
        }
        return success;
    }
}

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

import com.yaser.speech.activation.movement.MovementDetectionListener;
import com.yaser.speech.activation.movement.MovementDetector;

import android.content.Context;

public class MovementActivator implements SpeechActivator,
        MovementDetectionListener
{
    private MovementDetector detector;

    private SpeechActivationListener resultListener;

    public MovementActivator(Context context,
            SpeechActivationListener resultListener)
    {
        detector = new MovementDetector(context);
        this.resultListener = resultListener;
    }

    @Override
    public void detectActivation()
    {
        detector.startReadingAccelerationData(this);
    }

    @Override
    public void stop()
    {
        detector.stopReadingAccelerationData();
    }

    @Override
    public void movementDetected(boolean success)
    {
        stop();
        resultListener.activated(success);
    }
}

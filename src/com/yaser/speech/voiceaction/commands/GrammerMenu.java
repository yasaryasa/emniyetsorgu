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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.yaser.Constants;
import com.yaser.service.RetreiveJsonGrammerTask;
import com.yaser.service.RetreivePlakaGrammerJsonTask;
import com.yaser.service.RetreivePlakaJsonTask;
import com.yaser.speech.activation.SpeechActivationService;
import com.yaser.speech.command_utils.AllCommands;
import com.yaser.speech.text.WordList;
import com.yaser.speech.text.match.StemmedWordMatcher;
import com.yaser.speech.text.match.WordMatcher;
import com.yaser.speech.voiceaction.MultiCommandVoiceAction;
import com.yaser.speech.voiceaction.OnNotUnderstoodListener;
import com.yaser.speech.voiceaction.OnUnderstoodListener;
import com.yaser.speech.voiceaction.VoiceAction;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.VoiceAlertDialog;
import com.yaser.speech.voiceaction.WhyNotUnderstoodListener;
import com.yaser.thesis.R;

public class GrammerMenu implements VoiceActionCommand {
	private VoiceActionExecutor executor;
	private Context context;
	private WordMatcher kimlikMatcher;
	private WordMatcher plakaMatcher;

	private String[] words = { "kimlik", "kiþi" };
	private String[] plakaWords = { "plaka", "araç" };

	public GrammerMenu(Context context, VoiceActionExecutor executor) {
		this.context = context;
		this.executor = executor;
		if (Constants.useStemming) {
			this.kimlikMatcher = new StemmedWordMatcher(words);
			this.plakaMatcher = new StemmedWordMatcher(plakaWords);
		} else {
			this.kimlikMatcher = new WordMatcher(words);
			this.plakaMatcher = new WordMatcher(plakaWords);
		}
	}

	public boolean interpret(WordList heard, float[] confidence) {
		boolean success = false;

		String command = null;

		List<String> l = new ArrayList<String>();

		for (String string : heard.getWords()) {
			if (command == null) {
				command = string;
			} else {
				l.add(string);
			}
		}

		if (kimlikMatcher.isIn(command)) {
			StringBuilder sb = new StringBuilder();

			for (String string : l) {
				// TODO 11 degeri grammer kurallarýnýn belirlendiði property
				// dosyasýndan okunacak
				if (sb.length() == 11) {
					break;
				}
				sb.append(string).append(" ");
			}

			kimlikSorgu(sb.toString());

			success = true;
		} else if (plakaMatcher.isIn(command)) {

			StringBuilder sb = new StringBuilder();

			for (String string : l) {
				// TODO 11 degeri grammer kurallarýnýn belirlendiði property
				// dosyasýndan okunacak
				// if (sb.length() == 11) {
				// break;
				// }
				sb.append(string).append(" ");
			}

			// executor.speak("sorgulamak istediðiniz plaka numarasý "
			// + sb.toString());

			plakaSorgu(l);

			success = true;
		}

		return success;
	}

	private void plakaSorgu(final List<String> l) {

		// TODO burada yes-no confirmation olmasý lazým
		final VoiceAlertDialog confirmDialog = new VoiceAlertDialog();

		String plakaNo = null;
		String toSpeak = null;
		for (int i = 0; i < l.size(); i++) {

			try {
				Integer.parseInt(l.get(i));
				if (plakaNo == null) {
					plakaNo = l.get(i).concat("");
					toSpeak = l.get(i).concat(" ");
				} else {
					plakaNo += l.get(i).concat("");
					toSpeak += l.get(i).concat(" ");
				}
			} catch (NumberFormatException e) {
				if (plakaNo == null) {
					plakaNo = l.get(i).substring(0, 1).concat("");
					toSpeak = l.get(i).concat(" ");
				} else {
					plakaNo += l.get(i).substring(0, 1).concat("");
					toSpeak += l.get(i).concat(" ");
				}
			}
		}

		final String tcknoFormat = plakaNo.replaceAll(" ", "");

		// add listener for positive response
		// use relaxed matching to increase chance of understanding user
		confirmDialog.addRelaxedPositive(new OnUnderstoodListener() {
			@Override
			public void understood() {
				RetreivePlakaGrammerJsonTask task = new RetreivePlakaGrammerJsonTask(
						GrammerMenu.this);
				task.execute(tcknoFormat);
			}
		});

		confirmDialog.addRelaxedNegative(new OnUnderstoodListener() {
			@Override
			public void understood() {
				executor.speak("plaka sorgulama iptal edildi.");
				try {
					Thread.sleep(2200);
				} catch (InterruptedException e) {
				}
				SpeechActivationService.activator.detectActivation();
			}
		});

		confirmDialog.addRelaxedNeutral(new OnUnderstoodListener() {
			@Override
			public void understood() {
				executor.execute(makePlakaMenu(context, executor));
			}
		}, new String[] { "düzelt" });

		// if the user says anything else besides the yes words cancel
		confirmDialog.setNotUnderstood(new OnNotUnderstoodListener() {
			@Override
			public void notUnderstood(List<String> heard, int reason) {
				String toSayCancelled = context.getResources().getString(
						R.string.voiceaction_dontunderstand);
				executor.reExecute(toSayCancelled);
			}
		});

		String toSay = "Sorgulamak istediðiniz plaka numarasý. " + toSpeak
				+ ". Devam etmek istiyor musunuz?";
		confirmDialog.setPrompt(toSay);
		confirmDialog.setSpokenPrompt(toSay);

		executor.execute(confirmDialog);
	}

	private void kimlikSorgu(final String tckno) {

		// TODO burada yes-no confirmation olmasý lazým
		final VoiceAlertDialog confirmDialog = new VoiceAlertDialog();

		final String tcknoFormat = tckno.replaceAll(" ", "");

		// add listener for positive response
		// use relaxed matching to increase chance of understanding user
		confirmDialog.addRelaxedPositive(new OnUnderstoodListener() {
			@Override
			public void understood() {
				RetreiveJsonGrammerTask task = new RetreiveJsonGrammerTask(
						GrammerMenu.this);
				task.execute(tcknoFormat);
			}
		});

		confirmDialog.addRelaxedNegative(new OnUnderstoodListener() {
			@Override
			public void understood() {
				executor.speak("kimlik sorgulama iptal edildi.");
				try {
					Thread.sleep(2200);
				} catch (InterruptedException e) {
				}
				SpeechActivationService.activator.detectActivation();
			}
		});

		confirmDialog.addRelaxedNeutral(new OnUnderstoodListener() {
			@Override
			public void understood() {
				executor.execute(makeKimlikMenu(context, executor));
			}
		}, new String[] { "düzelt" });

		// if the user says anything else besides the yes words cancel
		confirmDialog.setNotUnderstood(new OnNotUnderstoodListener() {
			@Override
			public void notUnderstood(List<String> heard, int reason) {
				String toSayCancelled = context.getResources().getString(
						R.string.voiceaction_dontunderstand);
				executor.reExecute(toSayCancelled);
			}
		});

		String toSay = "Sorgulamak istediðiniz kimlik numarasý. " + tckno
				+ ". Devam etmek istiyor musunuz?";
		confirmDialog.setPrompt(toSay);
		confirmDialog.setSpokenPrompt(toSay);

		executor.execute(confirmDialog);
	}

	public static VoiceAction makePlakaMenu(Context context,
			VoiceActionExecutor executor) {
		String prompt = "Lütfen sorgulamak istediðiniz plakayý söyleyin.";
		VoiceActionCommand lookup = new PlakaSorgu(context, executor);
		VoiceActionCommand cancel = new CancelCommand(context, executor);
		VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(
				cancel, lookup));
		voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context,
				executor, true));
		voiceAction.setSpokenPrompt(prompt);
		voiceAction.setPrompt(prompt);

		return voiceAction;
	}

	public static VoiceAction makeKimlikMenu(Context context,
			VoiceActionExecutor executor) {
		String prompt = "Lütfen 11 haneli TC kimlik numarasýný söyleyin.";
		VoiceActionCommand lookup = new KimlikSorgu(context, executor);
		VoiceActionCommand cancel = new CancelCommand(context, executor);
		VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(
				cancel, lookup));
		voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context,
				executor, true));

		voiceAction.setSpokenPrompt(prompt);
		voiceAction.setPrompt(prompt);

		return voiceAction;
	}

	public VoiceActionExecutor getExecutor() {
		return executor;
	}

	public Context getContext() {
		return context;
	}

}

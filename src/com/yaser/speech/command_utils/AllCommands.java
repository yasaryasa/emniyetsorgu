package com.yaser.speech.command_utils;

import java.util.Arrays;

import android.content.Context;

import com.yaser.speech.voiceaction.MultiCommandVoiceAction;
import com.yaser.speech.voiceaction.VoiceAction;
import com.yaser.speech.voiceaction.VoiceActionCommand;
import com.yaser.speech.voiceaction.VoiceActionExecutor;
import com.yaser.speech.voiceaction.WhyNotUnderstoodListener;
import com.yaser.speech.voiceaction.commands.CancelCommand;
import com.yaser.speech.voiceaction.commands.KimlikSorgu;
import com.yaser.speech.voiceaction.commands.KimlikSorguMenu;
import com.yaser.speech.voiceaction.commands.PlakaSorgu;
import com.yaser.speech.voiceaction.commands.PlakaSorguMenu;

public enum AllCommands {

	
	INSTANCE;
	
	//TODO burada voiceactionþar singleton olarak ayarlanabilirler.
	private VoiceAction startMenuAction = null;
	private VoiceAction kimlikMenuAction = null;
	
	public static VoiceAction makeStartMenu(Context context, VoiceActionExecutor executor) {
//		final String LOOKUP_PROMPT = "Kimlik sorgulama için Kimlik. Plaka sorgulamak için plaka komutlarýný kullanýn." +
//				" Çýkýþ yapmak için iptal demeniz yeterli.";
		final String LOOKUP_PROMPT = "Sorgu baþladý.";
        VoiceActionCommand kimlikSorgu = new KimlikSorguMenu(context, executor);
        VoiceActionCommand plakaSorgu = new PlakaSorguMenu(context, executor);
        VoiceActionCommand cancel = new CancelCommand(context, executor);
        VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(cancel, kimlikSorgu, plakaSorgu));
        voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context, executor, true));
		
        voiceAction.setPrompt(LOOKUP_PROMPT);
        voiceAction.setSpokenPrompt(LOOKUP_PROMPT);
		return voiceAction;
	}
	
	public static VoiceAction makeKimlikMenu(Context context, VoiceActionExecutor executor) {
		String prompt = "Lütfen 11 haneli TC kimlik numarasýný söyleyin.";
        VoiceActionCommand lookup = new KimlikSorgu(context, executor);
        VoiceActionCommand cancel = new CancelCommand(context, executor);
        VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(cancel, lookup));
        voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context, executor, true));
        
        voiceAction.setSpokenPrompt(prompt);
        voiceAction.setPrompt(prompt);
        
        return voiceAction;
	}
	
	public static VoiceAction makePlakaMenu(Context context, VoiceActionExecutor executor) {
		String prompt = "Lütfen araç plakasýný söyleyin.";
        VoiceActionCommand lookup = new PlakaSorgu(context, executor);
        VoiceActionCommand cancel = new CancelCommand(context, executor);
        VoiceAction voiceAction = new MultiCommandVoiceAction(Arrays.asList(cancel, lookup));
        voiceAction.setNotUnderstood(new WhyNotUnderstoodListener(context, executor, true));
        
        voiceAction.setSpokenPrompt(prompt);
        voiceAction.setPrompt(prompt);
        
		return voiceAction;
	}
	
}

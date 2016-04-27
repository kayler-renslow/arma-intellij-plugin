package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.JBColor;

import javax.swing.*;

/**
 * @author Kayler
 * Simple utility class to make a quick message dialog
 * Created on 04/26/2016.
 */
public class SimpleMessageDialog {
	public static DialogBuilder newDialog(String title, String message){
		DialogBuilder db = new DialogBuilder();
		db.addOkAction();
		db.setTitle(title);
		String messageCondensed = "";
		final int MAX_LINE_WIDTH = 15; //number of words before a new line character
		String[] tokens = message.split("[ ]");
		int word = 1;
		for(String token : tokens){
			messageCondensed += token + " ";
			if(word % MAX_LINE_WIDTH == 0){
				messageCondensed+="\n";
			}
			word++;
		}
		JTextArea field = new JTextArea(messageCondensed);
		field.setBorder(null);
		field.setEditable(false);
		field.setBackground(JBColor.background());
		db.centerPanel(field);
		return db;
	}
}

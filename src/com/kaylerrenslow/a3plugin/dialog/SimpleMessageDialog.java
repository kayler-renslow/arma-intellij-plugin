package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple utility class to make a quick message dialog
 *
 * @author Kayler
 * @since 04/26/2016
 */
public class SimpleMessageDialog {
	public static DialogBuilder newDialog(String title, String message) {
		DialogBuilder db = new DialogBuilder();
		db.addOkAction();
		db.setTitle(title);
		String messageCondensed = "";
		final int MAX_LINE_WIDTH = 15; //number of words before a new line character
		String[] tokens = message.split("[ ]");
		int word = 1;
		for (String token : tokens) {
			messageCondensed += token + " ";
			if (word % MAX_LINE_WIDTH == 0) {
				messageCondensed += "\n";
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

	/**
	 * Does the same thing as newDialog except that this will automatically show the dialog
	 *
	 * @param title   title of the title
	 * @param message message to show
	 * @return the dialog
	 */
	public static DialogBuilder showNewDialog(String title, String message) {
		DialogBuilder db = newDialog(title, message);
		db.show();
		return db;
	}

	public static String getExceptionString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return "An error occurred. Please report this message to the developer(s).\n" + sw.toString();
	}
}

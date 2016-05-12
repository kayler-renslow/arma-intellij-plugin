package com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 05/11/2016.
 */
public class PaintedRegion extends Rectangle{
	protected Paint myPaint;
	protected Color textColor = Color.BLACK;
	protected Border border;

	private Text textObj = new Text();

	public PaintedRegion(int x, int y, int width, int height) {
		super(x, y, x + width, y + height);
	}

	public Font getFont() {
		return textObj.getFont();
	}

	public void setFont(@NotNull Font font) {
		this.textObj.setFont(font);
	}

	public void setText(String textObj){
		this.textObj.setText(textObj);
	}

	public String getText(){
		return this.textObj.getText();
	}

	public int getTextX(){
		int textWidth = (int)textObj.getLayoutBounds().getWidth();
		int x = Math.max(getX1(), getY1());
		return x + (getWidth() - textWidth) / 2;
	}

	public int getTextY(){
		int textHeight = (int)textObj.getLayoutBounds().getHeight();
		int y = Math.min(getY1(), getY2());
		return y + (getHeight() - textHeight) / 2;
	}


	@NotNull
	public Paint getPaint() {
		return myPaint;
	}

	public void setPaint(@NotNull Paint paint) {
		this.myPaint = paint;
	}

	@Nullable
	public Border getBorder() {
		return border;
	}

	public void setBorder(@Nullable Border border){
		this.border = border;
	}

	@NotNull
	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(@NotNull Color color){
		this.textColor = color;
	}
}

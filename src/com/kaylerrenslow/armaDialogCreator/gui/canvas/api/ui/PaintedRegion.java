package com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 05/11/2016.
 */
public class PaintedRegion extends Region {
	private Paint myPaint;
	private Color textColor = Color.BLACK;
	private Border border;

	private Text textObj = new Text();

	protected PaintedRegion(int x, int y, int width, int height) {
		super(x, y, x + width, y + height);
	}

	private int getTextX() {
		int textWidth = (int) textObj.getLayoutBounds().getWidth();
		return getLeftX() + (getWidth() - textWidth) / 2;
	}

	private int getTextY() {
		int textHeight = (int) textObj.getLayoutBounds().getHeight();
		return getTopY() + (getHeight() - textHeight) / 2;
	}

	public void paint(GraphicsContext gc) {
		if (border != null) {
			gc.save();
			gc.setStroke(border.getColor());
			gc.setLineWidth(border.getThickness());
			paintRegion(gc);
			gc.restore();
		}
		gc.setFill(myPaint);
		gc.setStroke(myPaint);
		paintRegion(gc);
		gc.setFont(getFont());
		gc.setFill(textColor);
		gc.fillText(getText(), getTextX(), getTextY());
	}

	public Font getFont() {
		return textObj.getFont();
	}

	public void setFont(@NotNull Font font) {
		this.textObj.setFont(font);
	}

	public void setText(String text) {
		this.textObj.setText(text);
	}

	public String getText() {
		return this.textObj.getText();
	}

	private void paintRegion(GraphicsContext gc) {
		drawRectangle(gc);
		gc.fill();
	}

	public void setPaint(@NotNull Paint paint) {
		this.myPaint = paint;
	}

	public void setBorder(@Nullable Border border) {
		this.border = border;
	}

	public void setTextColor(@NotNull Color color) {
		this.textColor = color;
	}
}

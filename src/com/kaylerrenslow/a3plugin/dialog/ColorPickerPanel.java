package com.kaylerrenslow.a3plugin.dialog;

import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.util.ResourceGetter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @author Kayler
 *         This is a JavaFX VBox panel that contains the all Color Picker controls and canvas.
 *         Created on 03/29/2016.
 */
public class ColorPickerPanel extends VBox {
	private final String ROOT = "/com/kaylerrenslow/a3plugin/img/";

	private final String[] imagePaths = {ROOT + "img1.png", ROOT + "img2.png", ROOT + "img3.png"};

	private final Font font = new Font(20);

	private final DecimalFormat df = new DecimalFormat("#.###");

	private final int CANVAS_WIDTH = 800;
	private final int CANVAS_HEIGHT = 450;

	private final String CANVAS_FOREGROUND_TEXT = Plugin.resources.getString("plugin.color_picker.foreground_text");
	private final double CANVAS_TEXT_WIDTH = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(CANVAS_FOREGROUND_TEXT, font);
	private final double CANVAS_TEXT_HEIGHT = font.getSize();

	private final int CANVAS_BACKGROUND_WIDTH = CANVAS_WIDTH / 10 * 5;
	private final int CANVAS_BACKGROUND_HEIGHT = CANVAS_HEIGHT / 10 * 5;

	private final int CANVAS_BACKGROUND_X_POS = (CANVAS_WIDTH - CANVAS_BACKGROUND_WIDTH) / 2;
	private final int CANVAS_BACKGROUND_Y_POS = (CANVAS_HEIGHT - CANVAS_BACKGROUND_HEIGHT) / 2;
	private final int CANVAS_FOREGROUND_X_POS = CANVAS_BACKGROUND_X_POS + (int) (CANVAS_BACKGROUND_WIDTH - CANVAS_TEXT_WIDTH) / 2;
	private final int CANVAS_FOREGROUND_Y_POS = CANVAS_BACKGROUND_Y_POS + (int) (CANVAS_BACKGROUND_HEIGHT - CANVAS_TEXT_HEIGHT) / 2 + (int)CANVAS_TEXT_HEIGHT;

	private final Color DEFAULT_FOREGROUND = new Color(1, 1, 1, 1);
	private final Color DEFAULT_BACKGROUND = new Color(0, 0, 0, 0.7);

	private final ColorPicker pickerForeground = new ColorPicker();
	private final ColorPicker pickerBackground = new ColorPicker();
	private final Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

	private final double[] armaColorForeground = {DEFAULT_FOREGROUND.getRed(), DEFAULT_FOREGROUND.getGreen(), DEFAULT_FOREGROUND.getBlue(), DEFAULT_FOREGROUND.getOpacity()};
	private final double[] armaColorBackground = {DEFAULT_BACKGROUND.getRed(), DEFAULT_BACKGROUND.getGreen(), DEFAULT_BACKGROUND.getBlue(), DEFAULT_BACKGROUND.getOpacity()};

	private final TextField tfForeground = new TextField();
	private final TextField tfBackground = new TextField();

	private final HBox controls = new HBox();
	private final HBox controls2 = new HBox();

	private Image currentImage;

	public ColorPickerPanel() {
		initialize();
	}

	private void initialize() {
		loadUI();
	}

	private void loadUI() {
		this.setPadding(new Insets(10, 10, 10, 10));
		pickerBackground.valueProperty().addListener(new PickerListener(true));
		pickerForeground.valueProperty().addListener(new PickerListener(false));
		pickerBackground.setValue(DEFAULT_BACKGROUND);
		pickerForeground.setValue(DEFAULT_FOREGROUND);

		setTextFieldText(tfForeground, armaColorForeground);
		setTextFieldText(tfBackground, armaColorBackground);

		canvas.getGraphicsContext2D().setFont(font);

		ComboBox<CanvasImage> cb = new ComboBox<>();
		cb.getItems().addAll(new CanvasImage("Image 1", imagePaths[0]), new CanvasImage("Image 2", imagePaths[1]), new CanvasImage("Image 3", imagePaths[2]));
		cb.getSelectionModel().selectedItemProperty().addListener(new CanvasImageComboBoxAction(cb));
		cb.getSelectionModel().select(0);

		Label lblforeground = new Label("Foreground:");
		Label lblbackground = new Label("Background:");

		controls.getChildren().addAll(lblforeground, pickerForeground, tfForeground, lblbackground, pickerBackground, tfBackground);

		final Insets margin_15right = new Insets(0, 15, 0, 0);
		final Insets margin_5right = new Insets(0, 5, 0, 0);
		final Insets margin_15bottom = new Insets(0, 0, 15, 0);

		HBox.setMargin(lblforeground, margin_5right);
		HBox.setMargin(pickerForeground, margin_15right);
		HBox.setMargin(tfForeground, margin_15right);
		HBox.setMargin(lblbackground, margin_5right);
		HBox.setMargin(pickerBackground, margin_15right);
		HBox.setMargin(tfBackground, margin_15right);

		Button updateFromTf = new Button(Plugin.resources.getString("plugin.color_picker.update_from_tf"));
		updateFromTf.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				double[] bg = arrayFromText(tfBackground);
				double[] fg = arrayFromText(tfForeground);
				if (bg != null) {
					pickerBackground.setValue(Color.color(bg[0], bg[1], bg[2], bg[3]));
				}
				if (fg != null) {
					pickerForeground.setValue(Color.color(fg[0], fg[1], fg[2], fg[3]));
				}
			}
		});

		controls2.getChildren().addAll(updateFromTf, cb);
		HBox.setMargin(updateFromTf, margin_15right);


		VBox.setMargin(controls, margin_15bottom);
		VBox.setMargin(controls2, margin_15bottom);

		this.getChildren().addAll(controls, controls2, canvas);
	}

	private void loadArmaColorValue(Color c, double[] arr) {
		arr[0] = truncate(c.getRed());
		arr[1] = truncate(c.getGreen());
		arr[2] = truncate(c.getBlue());
		arr[3] = truncate(c.getOpacity());
	}

	private double[] arrayFromText(TextField tf) {
		try {
			String[] split = tf.getText().replaceAll("\\[|\\]", "").split(",");

			double[] d = new double[4];
			for (int i = 0; i < d.length; i++) {
				d[i] = Double.parseDouble(split[i].trim());
				if (d[i] < 0 || d[i] > 1) {
					throw new Exception(); //to set the error color below
				}
			}
			tf.setStyle("");
			return d;
		} catch (Exception e) {
			tf.setStyle("-fx-background-color:red");
			return null;
		}
	}

	private double truncate(double d) {
		return Double.parseDouble(df.format(d));
	}

	private class CanvasImage {
		private final String name;
		private final String imagePath;

		public CanvasImage(String name, String imagePath) {
			this.name = name;
			this.imagePath = imagePath;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private class CanvasImageComboBoxAction implements ChangeListener<CanvasImage> {
		private final ComboBox<CanvasImage> cb;

		public CanvasImageComboBoxAction(ComboBox<CanvasImage> cb) {
			this.cb = cb;
		}


		@Override
		public void changed(ObservableValue<? extends CanvasImage> observable, CanvasImage oldValue, CanvasImage newValue) {
			currentImage = new Image(ResourceGetter.getResourceAsStream(newValue.imagePath));
			redrawCanvas();
		}
	}

	private void redrawCanvas() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.drawImage(currentImage, 0, 0);
		gc.setFill(pickerBackground.getValue());
		gc.fillRect(CANVAS_BACKGROUND_X_POS, CANVAS_BACKGROUND_Y_POS, CANVAS_BACKGROUND_WIDTH, CANVAS_BACKGROUND_HEIGHT);
		gc.setFill(pickerForeground.getValue());

		gc.fillText(CANVAS_FOREGROUND_TEXT, CANVAS_FOREGROUND_X_POS, CANVAS_FOREGROUND_Y_POS);
	}

	private void updateBackgroundTextField(Color color) {
		loadArmaColorValue(color, armaColorBackground);
		setTextFieldText(tfBackground, armaColorBackground);
		redrawCanvas();
	}

	private void setTextFieldText(TextField tf, double[] arr) {
		tf.setText(Arrays.toString(arr));
		tf.setStyle("");
	}

	private void updateForegroundTextField(Color color) {
		loadArmaColorValue(color, armaColorForeground);
		setTextFieldText(tfForeground, armaColorForeground);
		redrawCanvas();
	}

	private class PickerListener implements ChangeListener<Color> {
		private final boolean updateBackground;

		public PickerListener(boolean updateBackground) {
			this.updateBackground = updateBackground;
		}

		@Override
		public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
			if (updateBackground) {
				updateBackgroundTextField(newValue);
			} else {
				updateForegroundTextField(newValue);
			}
		}
	}
}

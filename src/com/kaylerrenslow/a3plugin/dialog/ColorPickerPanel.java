package com.kaylerrenslow.a3plugin.dialog;

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
 * Created by Kayler on 03/29/2016.
 */
public class ColorPickerPanel extends VBox {
	private static final String ROOT = "/com/kaylerrenslow/a3plugin/img/";

	private static final String[] imagePaths = {ROOT + "img1.png", ROOT + "img2.png", ROOT + "img3.png"};
	private static final String IMAGE_1 = "Image 1";
	private static final String IMAGE_2 = "Image 2";
	private static final String IMAGE_3 = "Image 3";

	private static final Font font = new Font(20);

	private static final DecimalFormat df = new DecimalFormat("#.###");

	private static final int CANVAS_WIDTH = 800;
	private static final int CANVAS_HEIGHT = 450;

	private static final String CANVAS_FOREGROUND_TEXT = "Hallo from the other side.";
	private final double CANVAS_TEXT_WIDTH = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(CANVAS_FOREGROUND_TEXT, font);
	private final double CANVAS_TEXT_HEIGHT = font.getSize();

	private final int CANVAS_BACKGROUND_WIDTH = CANVAS_WIDTH / 10 * 5;
	private final int CANVAS_BACKGROUND_HEIGHT = CANVAS_HEIGHT / 10 * 5;

	private final int CANVAS_BACKGROUND_X_POS = (CANVAS_WIDTH - CANVAS_BACKGROUND_WIDTH) / 2;
	private final int CANVAS_BACKGROUND_Y_POS = (CANVAS_HEIGHT - CANVAS_BACKGROUND_HEIGHT) / 2;
	private final int CANVAS_FOREGROUND_X_POS = CANVAS_BACKGROUND_X_POS + (int) (CANVAS_BACKGROUND_WIDTH - CANVAS_TEXT_WIDTH) / 2;
	private final int CANVAS_FOREGROUND_Y_POS = CANVAS_BACKGROUND_Y_POS + (int) (CANVAS_BACKGROUND_HEIGHT - CANVAS_TEXT_HEIGHT) / 2;

	private static final Color DEFAULT_FOREGROUND = new Color(1, 1, 1, 1);
	private static final Color DEFAULT_BACKGROUND = new Color(0, 0, 0, 0.7);

	private ColorPicker pickerForeground = new ColorPicker();
	private ColorPicker pickerBackground = new ColorPicker();
	private Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

	private double[] armaColorForeground = {DEFAULT_FOREGROUND.getRed(), DEFAULT_FOREGROUND.getGreen(), DEFAULT_FOREGROUND.getBlue(), DEFAULT_FOREGROUND.getOpacity()};
	private double[] armaColorBackground = {DEFAULT_BACKGROUND.getRed(), DEFAULT_BACKGROUND.getGreen(), DEFAULT_BACKGROUND.getBlue(), DEFAULT_BACKGROUND.getOpacity()};

	private TextField tfForeground = new TextField();
	private TextField tfBackground = new TextField();

	private HBox controls = new HBox();

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
		cb.getItems().addAll(new CanvasImage(IMAGE_1, imagePaths[0]), new CanvasImage(IMAGE_2, imagePaths[1]), new CanvasImage(IMAGE_3, imagePaths[2]));
		cb.getSelectionModel().selectedItemProperty().addListener(new CanvasImageComboBoxAction(cb));
		cb.getSelectionModel().select(0);

		Label lblforeground = new Label("Foreground:");
		Label lblbackground = new Label("Background:");

		controls.getChildren().addAll(lblforeground, pickerForeground, tfForeground, lblbackground, pickerBackground, tfBackground, cb);

		final Insets margin_15right = new Insets(0, 15, 0, 0);
		final Insets margin_5right = new Insets(0, 5, 0, 0);
		HBox.setMargin(lblforeground, margin_5right);
		HBox.setMargin(pickerForeground, margin_15right);
		HBox.setMargin(tfForeground, margin_15right);
		HBox.setMargin(lblbackground, margin_5right);
		HBox.setMargin(pickerBackground, margin_15right);
		HBox.setMargin(tfBackground, margin_15right);

		Button updateFromTf = new Button("Set color from Text Fields");
		updateFromTf.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				double[] bg = arrayFromText(tfBackground);
				double[] fg = arrayFromText(tfForeground);
				if(bg != null){
					pickerBackground.setValue(Color.color(bg[0], bg[1], bg[2], bg[3]));
				}
				if(fg != null){
					pickerForeground.setValue(Color.color(fg[0], fg[1], fg[2], fg[3]));
				}
			}
		});


		VBox.setMargin(controls, new Insets(0, 0, 15, 0));
		VBox.setMargin(updateFromTf, new Insets(15, 0, 0, 0));

		this.getChildren().addAll(controls, canvas, updateFromTf);
	}

	private static void loadArmaColorValue(Color c, double[] arr) {
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
				System.out.println(split[i]);
				d[i] = Double.parseDouble(split[i].trim());
				if(d[i] < 0 || d[i] > 1){
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

	private static double truncate(double d) {
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

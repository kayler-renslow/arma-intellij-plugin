package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.IPositionCalculator;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.IGraphicCreator;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.Label;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView.EditableTreeView;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView.TreeViewMenuItemBuilder;
import com.kaylerrenslow.armaDialogCreator.gui.img.ImagePaths;
import com.kaylerrenslow.armaDialogCreator.main.Lang;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 05/15/2016.
 */
class CanvasControls extends VBox implements IPositionCalculator {

	private final EditableTreeView treeView = new EditableTreeView(null);

	private final Label lblBackgroundColor = new Label(Lang.CanvasControls.BACKGROUND_COLOR);
	private final ColorPicker cpBackgroundColor = new ColorPicker();

	private final Label lblTextColor = new Label(Lang.CanvasControls.TEXT_COLOR);
	private final ColorPicker cpTextColor = new ColorPicker();

	private final Label lblOpacity = new Label(Lang.CanvasControls.OPACITY);
	private final ComboBox<Integer> cbOpacity = new ComboBox<>();

	private final Label lblLocateBG = new Label(Lang.CanvasControls.LOCATE_BACKGROUND_IMAGE);
	private final Button btnLocateBG = new Button();
	private final TextField tfBG = new TextField();

	private final Label lblAltStep = new Label(Lang.CanvasControls.ALT_STEP);
	private final ChoiceBox<Percentage> cbAltStep = new ChoiceBox<>();

	private final Label lblStep = new Label(Lang.CanvasControls.STEP);
	private final ChoiceBox<Percentage> cbStep = new ChoiceBox<>();

	private final CanvasView canvasView;


	private MenuItem newFolder = new MenuItem("New Folder", createFolderIcon());
	private MenuItem newItem = new MenuItem("New Item");
	private MenuItem newComp = new MenuItem("New Composite");

	private ContextMenu cm = new ContextMenu(newFolder, newItem, newComp);


	CanvasControls(CanvasView canvasView) {
		super(5);
		this.canvasView = canvasView;
		initializeStepChoiceboxes();
		HBox hboxStep = new HBox(5);
		hboxStep.getChildren().addAll(lblStep, cbStep, lblAltStep, cbAltStep);

		getChildren().addAll(hboxStep, treeView);
		doThings();
		this.setPadding(new Insets(5, 5, 5, 5));
		VBox.setVgrow(treeView, Priority.ALWAYS);
	}

	private void initializeStepChoiceboxes() {
		cbStep.getItems().add(new Percentage(1));
		for (int i = 5; i <= 20; i += 5) {
			cbStep.getItems().add(new Percentage(i));
		}
		cbAltStep.getItems().addAll(new Percentage(1), new Percentage(2), new Percentage(3), new Percentage(4), new Percentage(5));
		cbStep.getSelectionModel().select(0);
		cbAltStep.getSelectionModel().select(0);
		cbStep.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Percentage>() {
			@Override
			public void changed(ObservableValue<? extends Percentage> observable, Percentage oldValue, Percentage newValue) {
				canvasView.repaintCanvas();
			}
		});
	}

	private void doThings() {
		treeView.setContextMenu(cm);
		TreeViewMenuItemBuilder.setNewFolderAction(treeView, newFolder, "New Folder", new Object(), new IGraphicCreator() {
			@Nullable
			@Override
			public Node createGraphic() {
				return createFolderIcon();
			}
		});
		TreeViewMenuItemBuilder.setNewItemAction(treeView, newItem, "New Item", new Object(), new IGraphicCreator() {
			@Nullable
			@Override
			public Node createGraphic() {
//				HBox hBox = new HBox(3);
//				ImageView eye = new ImageView("/com/kaylerrenslow/armaDialogCreator/icons/eye.png");
//				eye.setOnMouseClicked(new EventHandler<MouseEvent>() {
//					boolean visible = true;
//					Image imgVisible = new ImageView("/com/kaylerrenslow/armaDialogCreator/icons/eye.png").getImage();
//					Image imgNotVisible = new ImageView("/com/kaylerrenslow/armaDialogCreator/icons/box.png").getImage();
//					@Override
//					public void handle(MouseEvent event) {
//						if(visible){
//							eye.setImage(imgVisible);
//						}else{
//							eye.setImage(imgNotVisible);
//						}
//						visible = !visible;
//					}
//				});
//				hBox.getChildren().addAll(eye, new CheckBox());
//				return hBox;
				return new RadioButton(""/*, new ImageView("/com/kaylerrenslow/armaDialogCreator/icons/eye.png")*/);
			}
		});
		TreeViewMenuItemBuilder.setNewCompositeItemAction(treeView, newComp, "Composite Item", new Object(), null);
	}

	@Override
	public double getGridScale() {
		return 1;
	}

	@Override
	public int alternateSnapPercentage() {
		return cbAltStep.getSelectionModel().getSelectedItem().value;
	}

	@Override
	public int snapPercentage() {
		return cbStep.getSelectionModel().getSelectedItem().value;
	}

	private ImageView createFolderIcon() {
		return new ImageView(ImagePaths.ICON_FOLDER);
	}

	private static class Percentage {
		private final int value;

		public Percentage(int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value + "%";
		}
	}
}

//package com.kaylerrenslow.armaplugin.dialog;
//
//import com.intellij.openapi.project.Project;
//import com.intellij.util.ui.UIUtil;
//import com.kaylerrenslow.armaplugin.*;
//import javafx.animation.AnimationTimer;
//import javafx.application.Platform;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.value.ObservableValue;
//import javafx.embed.swing.JFXPanel;
//import javafx.geometry.HPos;
//import javafx.geometry.Insets;
//import javafx.geometry.NodeOrientation;
//import javafx.geometry.Orientation;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.TextFieldTableCell;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.scene.text.Text;
//import javafx.util.converter.DefaultStringConverter;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.JComponent;
//import javax.swing.JDialog;
//import java.io.File;
//import java.util.ResourceBundle;
//import java.util.function.Function;
//
//public class IndexArmaAddonsStatusDialog extends JDialog {
//
//	private static final ResourceBundle bundle = ResourceBundle.getBundle("com.kaylerrenslow.armaplugin.IndexArmaAddonsBundle");
//	@NotNull
//	private final Project project;
//
//	private StatusBar statusBar;
//	private CenterPanel centerPanel;
//	private Footer footer;
//
//	public IndexArmaAddonsStatusDialog(@NotNull Project project) {
//		this.project = project;
//		setModalityType(ModalityType.APPLICATION_MODAL);
//		setContentPane(createCenterPanel());
//		setSize(800, 570);
//	}
//
//	@NotNull
//	private JComponent createCenterPanel() {
//		JFXPanel root = new JFXPanel();
//		if (UIUtil.isUnderDarcula()) {
//			//todo add dark stylesheet
//		}
//		Platform.runLater(() -> {
//			//don't initialize these outside Platform.runLater because they throw exceptions for not being on JavaFX thread
//			statusBar = new StatusBar();
//			centerPanel = new CenterPanel();
//			footer = new Footer();
//
//			BorderPane rootNode = new BorderPane();
//			root.setScene(new Scene(rootNode));
//
//			final Insets padding = new Insets(5);
//
//			rootNode.setTop(statusBar);
//			statusBar.setPadding(padding);
//
//			rootNode.setCenter(centerPanel);
//			centerPanel.setPadding(padding);
//
//			rootNode.setBottom(footer);
//			footer.setPadding(padding);
//
//			File selectedAddonsCfgFile = new File("D:\\Archive\\Intellij Files\\Arma Tools\\Arma Intellij Plugin\\addonsCfgTest.xml");
//			ArmaAddonsProjectConfig config = ArmaAddonsManager.parseAddonsConfig(selectedAddonsCfgFile, project);
//			ArmaAddonsManager.loadAddonsAsync(config, new File(selectedAddonsCfgFile.getParentFile().getAbsolutePath() + "/addonsIndexLog.txt"), new IndexingCallbackCluster(
//					statusBar, centerPanel, footer
//			));
//		});
//		return root;
//	}
//
//	private static class Footer extends HBox implements IndexingCallback {
//
//		private final RunningIndicator indicator = new RunningIndicator();
//
//		public Footer() {
//			super(5);
//			getChildren().add(indicator);
//			setRunning(false);
//		}
//
//		private void setRunning(boolean running) {
//			indicator.color1 = running ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
//			indicator.color2 = indicator.color1.deriveColor(0, 1, 0.5, 1);
//			if (!running) {
//				indicator.timer.stop();
//				indicator.paintStopped();
//			} else {
//				indicator.timer.start();
//			}
//		}
//
//		@Override
//		public void finishedIndex() {
//			setRunning(false);
//		}
//
//		@Override
//		public void startedIndex(@NotNull ArmaAddonsIndexingData data) {
//			setRunning(true);
//		}
//
//		private class RunningIndicator extends Canvas {
//			private Color color1;
//			private Color color2;
//			private double time = 0;
//			private boolean blinkIn = true;
//			private final GraphicsContext gc = this.getGraphicsContext2D();
//			private long lastPaint = 0;
//
//			private final AnimationTimer timer = new AnimationTimer() {
//				@Override
//				public void handle(long now) {
//					final double TIME_PER_BLINK = 1000; //1 second
//					long curTime = System.currentTimeMillis();
//					long timeElapsed = curTime - lastPaint;
//					lastPaint = curTime;
//
//					if (blinkIn) {
//						time += timeElapsed;
//						if (time >= TIME_PER_BLINK) {
//							time = TIME_PER_BLINK;
//							blinkIn = false;
//						}
//					} else {
//						time -= timeElapsed;
//						if (time <= 0) {
//							time = 0;
//							blinkIn = true;
//						}
//					}
//					gc.setFill(color1.interpolate(color2, time / TIME_PER_BLINK));
//					gc.fillRect(0, 0, getWidth(), getHeight());
//				}
//			};
//
//			public RunningIndicator() {
//				setWidth(32);
//				setHeight(32);
//			}
//
//			public void paintStopped() {
//				gc.setFill(Color.RED);
//				gc.fillRect(0, 0, getWidth(), getHeight());
//			}
//		}
//	}
//
//	private static class CenterPanel extends VBox implements IndexingCallback {
//		private final Label lblStatusMessage = new Label();
//		private final ProgressBar pbTotalWork = new ProgressBar();
//		private final ProgressBar pbCurrentWork = new ProgressBar();
//		private final TextArea taConsole = new TextArea();
//		private final CheckBox checkBoxExtractPbo = new CheckBox();
//		private final CheckBox checkBoxDebinarizeConfigs = new CheckBox();
//		private final CheckBox checkBoxParseConfigs = new CheckBox();
//		private final CheckBox checkBoxSaveReferences = new CheckBox();
//		private final CheckBox checkBoxCleanup = new CheckBox();
//		private final TableView<Message> tableViewMessage = new TableView<>();
//
//		public CenterPanel() {
//			super(5);
//			SplitPane centerSplitPane = new SplitPane(taConsole, tableViewMessage);
//			centerSplitPane.setOrientation(Orientation.HORIZONTAL);
//			VBox.setVgrow(centerSplitPane, Priority.ALWAYS);
//			GridPane gridPaneProgress = new GridPane();
//
//			getChildren().addAll(
//					new HBox(5, new Label(bundle.getString("CenterPanel.status")), lblStatusMessage),
//					gridPaneProgress,
//					new FlowPane(Orientation.HORIZONTAL, 5, 10,
//							checkBoxExtractPbo,
//							new Separator(Orientation.VERTICAL),
//							checkBoxDebinarizeConfigs,
//							new Separator(Orientation.VERTICAL),
//							checkBoxParseConfigs,
//							new Separator(Orientation.VERTICAL),
//							checkBoxSaveReferences,
//							new Separator(Orientation.VERTICAL),
//							checkBoxCleanup
//					),
//					centerSplitPane
//			);
//
//			{//grid pane progress stuff
//				gridPaneProgress.addRow(0,
//						new Label(bundle.getString("CenterPanel.total-work-for-mod")),
//						pbTotalWork
//				);
//				gridPaneProgress.addRow(1,
//						new Label(bundle.getString("CenterPanel.current-work")),
//						pbCurrentWork
//				);
//				gridPaneProgress.getColumnConstraints().add(new ColumnConstraints(0, -1, -1, Priority.NEVER, HPos.CENTER, false));
//				pbCurrentWork.setPrefWidth(Double.MAX_VALUE);
//				pbTotalWork.setPrefWidth(Double.MAX_VALUE);
//			}
//
//			{//table view stuff
//				TableColumn<Message, String> columnModName = new TableColumn<>(bundle.getString("CenterPanel.Table.ColumnName.addon"));
//				tableViewMessage.getColumns().add(columnModName);
//				columnModName.setCellValueFactory(param -> {
//					return param.getValue().addonName;
//				});
//				columnModName.setCellFactory(param -> {
//					return new WrappingTextFieldTableCell<>();
//				});
//
//				TableColumn<Message, String> columnMessageTxt = new TableColumn<>(bundle.getString("CenterPanel.Table.ColumnName.message"));
//				tableViewMessage.getColumns().add(columnMessageTxt);
//				columnMessageTxt.setCellValueFactory(param -> {
//					return param.getValue().message;
//				});
//				columnMessageTxt.setCellFactory(param -> {
//					return new WrappingTextFieldTableCell<>();
//				});
//
//				TableColumn<Message, String> columnType = new TableColumn<>(bundle.getString("CenterPanel.Table.ColumnName.type"));
//				tableViewMessage.getColumns().add(columnType);
//				columnType.setCellValueFactory(param -> {
//					return param.getValue().type;
//				});
//				columnType.setCellFactory(param -> {
//					return new WrappingTextFieldTableCell<>();
//				});
//			}
//
//			{ //checkbox stuff
//				//prevent user input
//				checkBoxExtractPbo.setDisable(true);
//				checkBoxDebinarizeConfigs.setDisable(true);
//				checkBoxParseConfigs.setDisable(true);
//				checkBoxSaveReferences.setDisable(true);
//				checkBoxCleanup.setDisable(true);
//
//				checkBoxExtractPbo.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
//				checkBoxDebinarizeConfigs.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
//				checkBoxParseConfigs.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
//				checkBoxSaveReferences.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
//				checkBoxCleanup.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
//
//				checkBoxExtractPbo.setText(bundle.getString("Status.Preview.extracting-pbos"));
//				checkBoxExtractPbo.setUserData(bundle.getString("Status.Active.extracting-pbos"));
//
//				checkBoxDebinarizeConfigs.setText(bundle.getString("Status.Preview.debinarize-configs"));
//				checkBoxDebinarizeConfigs.setUserData(bundle.getString("Status.Active.debinarize-configs"));
//
//				checkBoxParseConfigs.setText(bundle.getString("Status.Preview.parse-configs"));
//				checkBoxParseConfigs.setUserData(bundle.getString("Status.Active.parse-configs"));
//
//				checkBoxSaveReferences.setText(bundle.getString("Status.Preview.save-references"));
//				checkBoxSaveReferences.setUserData(bundle.getString("Status.Active.save-references"));
//
//				checkBoxCleanup.setText(bundle.getString("Status.Preview.cleanup"));
//				checkBoxCleanup.setUserData(bundle.getString("Status.Active.cleanup"));
//			}
//
//			taConsole.setEditable(false);
//
//		}
//
//		@Override
//		public void stepStart(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step newStep) {
//			CheckBox checkBox = getCheckBoxForStep(newStep);
//			checkBox.setSelected(false);
//			checkBox.setIndeterminate(true);
//
//			lblStatusMessage.setText(checkBox.getUserData().toString());
//		}
//
//		@Override
//		public void stepFinish(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step stepFinished) {
//			CheckBox checkBox = getCheckBoxForStep(stepFinished);
//			checkBox.setSelected(true);
//			checkBox.setIndeterminate(false);
//		}
//
//		@Override
//		public void message(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message) {
//			writeToConsole(handle, message);
//		}
//
//		@Override
//		public void errorMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//			String errorType = bundle.getString("CenterPanel.Table.message-type-error");
//			String cmessage = errorType + " - " + message;
//			writeToConsole(handle, cmessage);
//			addMessageRow(handle.getAddonName(), message, errorType);
//		}
//
//		@Override
//		public void errorMessage(@NotNull String message, @Nullable Exception e) {
//			String errorType = bundle.getString("CenterPanel.Table.message-type-error");
//			String cmessage = errorType + " - " + message;
//			taConsole.appendText(cmessage);
//			taConsole.appendText("\n");
//			addMessageRow("?", message, errorType);
//		}
//
//		@Override
//		public void warningMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//			String warningType = bundle.getString("CenterPanel.Table.message-type-warning");
//			String cmessage = warningType + " - " + message;
//			writeToConsole(handle, cmessage);
//			addMessageRow(handle.getAddonName(), message, warningType);
//		}
//
//		@Override
//		public void indexStartedForAddon(@NotNull ArmaAddonIndexingHandle handle) {
//			resetSteps();
//		}
//
//		private void writeToConsole(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message) {
//			taConsole.appendText(handle.getAddonName());
//			taConsole.appendText(" - ");
//			taConsole.appendText(message);
//			taConsole.appendText("\n");
//		}
//
//		private void clearConsole() {
//			taConsole.setText("");
//		}
//
//		private void addMessageRow(@NotNull String addonName, @NotNull String message, @NotNull String type) {
//			tableViewMessage.getItems().add(new Message(addonName, message, type));
//		}
//
//		private CheckBox getCheckBoxForStep(@NotNull Step step) {
//			switch (step) {
//				case ExtractPBOs: {
//					return checkBoxExtractPbo;
//				}
//				case DeBinarizeConfigs: {
//					return checkBoxDebinarizeConfigs;
//				}
//				case ParseConfigs: {
//					return checkBoxParseConfigs;
//				}
//				case SaveReferences: {
//					return checkBoxSaveReferences;
//				}
//				case Cleanup: {
//					return checkBoxCleanup;
//				}
//				default: {
//					throw new IllegalStateException("unhandled step: " + step);
//				}
//			}
//		}
//
//		private void resetSteps() {
//			checkBoxExtractPbo.setSelected(false);
//			checkBoxExtractPbo.setIndeterminate(false);
//
//			checkBoxDebinarizeConfigs.setSelected(false);
//			checkBoxDebinarizeConfigs.setIndeterminate(false);
//
//			checkBoxParseConfigs.setSelected(false);
//			checkBoxParseConfigs.setIndeterminate(false);
//
//			checkBoxSaveReferences.setSelected(false);
//			checkBoxSaveReferences.setIndeterminate(false);
//
//			checkBoxCleanup.setSelected(false);
//			checkBoxCleanup.setIndeterminate(false);
//		}
//
//		private class Message {
//			@NotNull
//			private final ObservableValue<String> addonName;
//			@NotNull
//			private final ObservableValue<String> message;
//			@NotNull
//			private final ObservableValue<String> type;
//
//			public Message(@NotNull String addonName, @NotNull String message, @NotNull String type) {
//				this.addonName = new SimpleStringProperty(addonName);
//				this.message = new SimpleStringProperty(message);
//				this.type = new SimpleStringProperty(type);
//			}
//		}
//
//		private class WrappingTextFieldTableCell<S> extends TextFieldTableCell<S, String> {
//
//			private final Text cellText;
//
//			public WrappingTextFieldTableCell() {
//				super(new DefaultStringConverter());
//				this.cellText = createText();
//			}
//
//			@Override
//			public void cancelEdit() {
//				super.cancelEdit();
//				setGraphic(cellText);
//			}
//
//			@Override
//			public void updateItem(String item, boolean empty) {
//				super.updateItem(item, empty);
//				if (!isEmpty() && !isEditing()) {
//					setGraphic(cellText);
//				}
//			}
//
//			private Text createText() {
//				Text text = new Text();
//				text.wrappingWidthProperty().bind(widthProperty());
//				text.textProperty().bind(itemProperty());
//				return text;
//			}
//		}
//	}
//
//	private static class StatusBar extends ToolBar implements IndexingCallback {
//		private final Label lblModName = new Label();
//		private final Label lblModsLeft = new Label();
//		private final Label lblModsFinished = new Label();
//		private final Label lblErrorCount = new Label();
//		private final Label lblWarningCount = new Label();
//		private final Label lblTimeElapsed = new Label();
//		private final Font BOLD_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize());
//
//		private int modsFinishedCount = 0;
//		private int totalAddonsToIndex = 0;
//		private int errorCount = 0;
//		private int warningCount = 0;
//		private long timeIndexStarted = 0;
//		private long millisSinceLastLabelUpdate = 0;
//		private final AnimationTimer timerTimeElapsed = new AnimationTimer() {
//			private final String formatString = bundle.getString("StatusBar.time-elapsed-minutes-f");
//
//			@Override
//			public void start() {
//				super.start();
//				lblTimeElapsed.setText(String.format(formatString, 0));
//			}
//
//			@Override
//			public void handle(long now) {
//				long curTime = System.currentTimeMillis();
//				long timeElapsed = curTime - timeIndexStarted;
//				millisSinceLastLabelUpdate += timeElapsed;
//				if (millisSinceLastLabelUpdate >= 60 * 1000) {
//					//update the label every minute
//					lblTimeElapsed.setText(String.format(formatString, millisSinceLastLabelUpdate / (60 * 1000)));
//					millisSinceLastLabelUpdate = 0;
//				}
//			}
//		};
//
//		public StatusBar() {
//			Function<String, Label> getBoldLabel = text -> {
//				Label lbl = new Label(text);
//				lbl.setFont(BOLD_FONT);
//				return lbl;
//			};
//
//			getItems().addAll(
//					getBoldLabel.apply(bundle.getString("StatusBar.mod-name")),
//					lblModName,
//					new Separator(Orientation.VERTICAL),
//					new Separator(Orientation.VERTICAL),
//					getBoldLabel.apply(bundle.getString("StatusBar.mods-left")),
//					lblModsLeft,
//					new Separator(Orientation.VERTICAL),
//					getBoldLabel.apply(bundle.getString("StatusBar.mods-finished")),
//					lblModsFinished,
//					new Separator(Orientation.VERTICAL),
//					getBoldLabel.apply(bundle.getString("StatusBar.errors")),
//					lblErrorCount,
//					new Separator(Orientation.VERTICAL),
//					getBoldLabel.apply(bundle.getString("StatusBar.warnings")),
//					lblWarningCount,
//					new Separator(Orientation.VERTICAL),
//					getBoldLabel.apply(bundle.getString("StatusBar.time-elapsed")),
//					lblTimeElapsed
//			);
//		}
//
//		@Override
//		public void indexStartedForAddon(@NotNull ArmaAddonIndexingHandle handle) {
//			lblModName.setText(handle.getAddonName());
//		}
//
//		@Override
//		public void startedIndex(@NotNull ArmaAddonsIndexingData data) {
//			totalAddonsToIndex = data.getAddonsMarkedToIndex().size();
//			timeIndexStarted = System.currentTimeMillis();
//			timerTimeElapsed.start();
//			updateModsLeftLabel();
//			updateModsFinishedLabel();
//			updateErrorCountLabel();
//			updateWarningCountLabel();
//		}
//
//		@Override
//		public void finishedIndex() {
//			timerTimeElapsed.stop();
//		}
//
//		@Override
//		public void indexFinishedForAddon(@NotNull ArmaAddonIndexingHandle handle) {
//			modsFinishedCount++;
//			updateModsFinishedLabel();
//			updateModsLeftLabel();
//		}
//
//		@Override
//		public void errorMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//			errorCount++;
//			updateErrorCountLabel();
//		}
//
//		@Override
//		public void errorMessage(@NotNull String message, @Nullable Exception e) {
//			errorCount++;
//			updateErrorCountLabel();
//		}
//
//		@Override
//		public void warningMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//			warningCount++;
//			updateWarningCountLabel();
//		}
//
//		private void updateWarningCountLabel() {
//			lblWarningCount.setText(warningCount + "");
//		}
//
//		private void updateErrorCountLabel() {
//			lblErrorCount.setText(errorCount + "");
//		}
//
//		private void updateModsFinishedLabel() {
//			lblModsFinished.setText(modsFinishedCount + "");
//		}
//
//		private void updateModsLeftLabel() {
//			lblModsLeft.setText((totalAddonsToIndex - modsFinishedCount) + "");
//		}
//	}
//
//	/**
//	 * Creates a wrapper around a series of {@link ArmaAddonsIndexingCallback} instances and then forwards method invocations
//	 * in the JavaFX UI Thread
//	 */
//	private static class IndexingCallbackCluster implements ArmaAddonsIndexingCallback {
//
//		@NotNull
//		private final ArmaAddonsIndexingCallback[] callbacks;
//
//		public IndexingCallbackCluster(@NotNull ArmaAddonsIndexingCallback... callbacks) {
//			this.callbacks = callbacks;
//		}
//
//		@Override
//		public void indexStartedForAddon(@NotNull ArmaAddonIndexingHandle handle) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.indexStartedForAddon(handle);
//				}
//			});
//		}
//
//		@Override
//		public void totalWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.totalWorkProgressUpdate(handle, progress);
//				}
//			});
//		}
//
//		@Override
//		public void currentWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.currentWorkProgressUpdate(handle, progress);
//				}
//			});
//		}
//
//		@Override
//		public void message(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.message(handle, message);
//				}
//			});
//		}
//
//		@Override
//		public void errorMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.errorMessage(handle, message, e);
//				}
//			});
//		}
//
//		@Override
//		public void errorMessage(@NotNull String message, @Nullable Exception e) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.errorMessage(message, e);
//				}
//			});
//		}
//
//		@Override
//		public void warningMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.warningMessage(handle, message, e);
//				}
//			});
//		}
//
//		@Override
//		public void stepStart(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step newStep) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.stepStart(handle, newStep);
//				}
//			});
//		}
//
//		@Override
//		public void stepFinish(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step stepFinished) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.stepFinish(handle, stepFinished);
//				}
//			});
//		}
//
//		@Override
//		public void indexFinishedForAddon(@NotNull ArmaAddonIndexingHandle handle) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.indexFinishedForAddon(handle);
//				}
//			});
//		}
//
//		@Override
//		public void finishedIndex() {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.finishedIndex();
//				}
//			});
//		}
//
//		@Override
//		public void startedIndex(@NotNull ArmaAddonsIndexingData data) {
//			Platform.runLater(() -> {
//				for (ArmaAddonsIndexingCallback callback : callbacks) {
//					callback.startedIndex(data);
//				}
//			});
//		}
//	}
//
//	private interface IndexingCallback extends ArmaAddonsIndexingCallback {
//
//		@Override
//		default void indexStartedForAddon(@NotNull ArmaAddonIndexingHandle handle) {
//		}
//
//		@Override
//		default void totalWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress) {
//		}
//
//		@Override
//		default void currentWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress) {
//		}
//
//		@Override
//		default void message(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message) {
//		}
//
//		@Override
//		default void errorMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//		}
//
//		@Override
//		default void warningMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
//		}
//
//		@Override
//		default void stepStart(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step newStep) {
//		}
//
//		@Override
//		default void stepFinish(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step stepFinished) {
//		}
//
//		@Override
//		default void indexFinishedForAddon(@NotNull ArmaAddonIndexingHandle handle) {
//		}
//
//		@Override
//		default void finishedIndex() {
//		}
//
//		@Override
//		default void startedIndex(@NotNull ArmaAddonsIndexingData data) {
//		}
//
//		@Override
//		default void errorMessage(@NotNull String message, @Nullable Exception e) {
//		}
//	}
//}

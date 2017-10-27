package com.kaylerrenslow.armaplugin.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.UIUtil;
import com.kaylerrenslow.armaplugin.ArmaPlugin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.ResourceBundle;

public class IndexArmaAddonsStatusDialog extends DialogWrapper {

	private static final ResourceBundle bundle = ArmaPlugin.getPluginBundle();

	private final StatusBar statusBar = new StatusBar();
	private final CenterPanel centerPanel = new CenterPanel();

	public IndexArmaAddonsStatusDialog(@Nullable Project project) {
		super(project, false);
		init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		JFXPanel root = new JFXPanel();
		if (UIUtil.isUnderDarcula()) {
			//todo add dark stylesheet
		}
		BorderPane rootNode = new BorderPane();
		root.setScene(new Scene(rootNode));

		final Insets padding = new Insets(5);

		rootNode.setTop(statusBar);
		statusBar.setPadding(padding);

		rootNode.setCenter(centerPanel);
		centerPanel.setPadding(padding);

		return root;
	}

	private static class Footer extends HBox {

	}

	private static class CenterPanel extends VBox {
		private final Label lblStatusMessage = new Label();
		private final ProgressBar pbTotalWork = new ProgressBar();
		private final ProgressBar pbCurrentWork = new ProgressBar();
		private final TextArea taConsole = new TextArea();
		private final CheckBox checkBoxExtractPbo = new CheckBox();
		private final CheckBox checkBoxDebinarizeConfigs = new CheckBox();
		private final CheckBox checkBoxParseConfigs = new CheckBox();
		private final CheckBox checkBoxSaveReferences = new CheckBox();
		private final CheckBox checkBoxCleanup = new CheckBox();
		private final TableView<Message> tableViewMessage = new TableView<>();

		public enum Step {
			ExtractPBOs, DeBinarizeConfigs, ParseConfigs, SaveReferences, Cleanup
		}

		public CenterPanel() {
			super(5);
			SplitPane centerSplitPane = new SplitPane(taConsole, tableViewMessage);
			centerSplitPane.setOrientation(Orientation.HORIZONTAL);
			GridPane gridPaneProgress = new GridPane();

			getChildren().addAll(
					new HBox(5, new Label(bundle.getString("Dialog.IndexArmaAddonsStatus.CenterPanel.status")), lblStatusMessage),
					gridPaneProgress,
					new FlowPane(Orientation.HORIZONTAL, 5, 10, checkBoxExtractPbo, checkBoxDebinarizeConfigs, checkBoxParseConfigs, checkBoxSaveReferences, checkBoxCleanup),
					centerSplitPane
			);

			{//grid pane progress stuff
				gridPaneProgress.addRow(0,
						new Label(bundle.getString("Dialog.IndexArmaAddonsStatus.CenterPanel.total-work-for-mod")),
						pbTotalWork
				);
				gridPaneProgress.addRow(1,
						new Label(bundle.getString("Dialog.IndexArmaAddonsStatus.CenterPanel.current-work")),
						pbCurrentWork
				);
				gridPaneProgress.getColumnConstraints().add(new ColumnConstraints(0, -1, -1, Priority.NEVER, HPos.CENTER, false));
				pbCurrentWork.setPrefWidth(Double.MAX_VALUE);
				pbTotalWork.setPrefWidth(Double.MAX_VALUE);
			}

			{//table view stuff
				TableColumn<Message, String> columnModName = new TableColumn<>();
				tableViewMessage.getColumns().add(columnModName);
				columnModName.setCellValueFactory(param -> {
					return param.getValue().modName;
				});

				TableColumn<Message, String> columnMessageTxt = new TableColumn<>();
				tableViewMessage.getColumns().add(columnMessageTxt);
				columnMessageTxt.setCellValueFactory(param -> {
					return param.getValue().message;
				});

				TableColumn<Message, String> columnType = new TableColumn<>();
				tableViewMessage.getColumns().add(columnType);
				columnType.setCellValueFactory(param -> {
					return param.getValue().type;
				});
			}

			{ //checkbox stuff
				//prevent user input
				checkBoxExtractPbo.setDisable(true);
				checkBoxDebinarizeConfigs.setDisable(true);
				checkBoxParseConfigs.setDisable(true);
				checkBoxSaveReferences.setDisable(true);
				checkBoxCleanup.setDisable(true);

				checkBoxExtractPbo.setText(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Preview.extracting-pbos"));
				checkBoxExtractPbo.setUserData(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Active.extracting-pbos"));

				checkBoxDebinarizeConfigs.setText(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Preview.debinarize-configs"));
				checkBoxDebinarizeConfigs.setUserData(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Active.debinarize-configs"));

				checkBoxParseConfigs.setText(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Preview.parse-configs"));
				checkBoxParseConfigs.setUserData(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Active.parse-configs"));

				checkBoxSaveReferences.setText(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Preview.save-references"));
				checkBoxSaveReferences.setUserData(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Active.save-references"));

				checkBoxCleanup.setText(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Preview.cleanup"));
				checkBoxCleanup.setUserData(bundle.getString("Dialog.IndexArmaAddonsStatus.Status.Active.cleanup"));
			}

			taConsole.setDisable(true);
		}

		public void finishStep(@NotNull Step step) {
			CheckBox checkBox = getCheckBoxForStep(step);
			checkBox.setSelected(true);
			checkBox.setIndeterminate(false);
		}

		public void startStep(@NotNull Step step) {
			CheckBox checkBox = getCheckBoxForStep(step);
			checkBox.setIndeterminate(true);

			lblStatusMessage.setText(checkBox.getUserData().toString());
		}

		public void addError(@NotNull String modName, @NotNull String message) {
			addMessageRow(modName, message, bundle.getString("Dialog.IndexArmaAddonsStatus.CenterPanel.Table.message-type-error"));
		}

		public void addWarning(@NotNull String modName, @NotNull String message) {
			addMessageRow(modName, message, bundle.getString("Dialog.IndexArmaAddonsStatus.CenterPanel.Table.message-type-warning"));
		}

		public void writeToConsole(@NotNull String message) {
			taConsole.appendText(message);
		}

		public void clearConsole() {
			taConsole.setText("");
		}

		private void addMessageRow(@NotNull String modeName, @NotNull String message, @NotNull String type) {
			tableViewMessage.getItems().add(new Message(modeName, message, type));
		}

		private CheckBox getCheckBoxForStep(@NotNull Step step) {
			switch (step) {
				case ExtractPBOs: {
					return checkBoxExtractPbo;
				}
				case DeBinarizeConfigs: {
					return checkBoxDebinarizeConfigs;
				}
				case ParseConfigs: {
					return checkBoxParseConfigs;
				}
				case SaveReferences: {
					return checkBoxSaveReferences;
				}
				case Cleanup: {
					return checkBoxCleanup;
				}
				default: {
					throw new IllegalStateException("unhandled step: " + step);
				}
			}
		}

		public void resetSteps() {
			checkBoxExtractPbo.setSelected(false);
			checkBoxExtractPbo.setIndeterminate(false);

			checkBoxDebinarizeConfigs.setSelected(false);
			checkBoxDebinarizeConfigs.setIndeterminate(false);

			checkBoxParseConfigs.setSelected(false);
			checkBoxParseConfigs.setIndeterminate(false);

			checkBoxSaveReferences.setSelected(false);
			checkBoxSaveReferences.setIndeterminate(false);

			checkBoxCleanup.setSelected(false);
			checkBoxCleanup.setIndeterminate(false);
		}

		private class Message {
			@NotNull
			private final ObservableValue<String> modName;
			@NotNull
			private final ObservableValue<String> message;
			@NotNull
			private final ObservableValue<String> type;

			public Message(@NotNull String modName, @NotNull String message, @NotNull String type) {
				this.modName = new SimpleStringProperty(modName);
				this.message = new SimpleStringProperty(message);
				this.type = new SimpleStringProperty(type);
			}
		}
	}

	private static class StatusBar extends ToolBar {
		private final Label lblModName = new Label();
		private final Label lblModsLeft = new Label();
		private final Label lblModsFinished = new Label();
		private final Label lblErrorCount = new Label();
		private final Label lblWarningCount = new Label();
		private final Label lblTimeElapsed = new Label();
		private final Font BOLD_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize());

		public StatusBar() {
			getChildren().addAll(
					getBoldLabel(bundle.getString("Dialog.IndexArmaAddonsStatus.StatusBar.mod-name")),
					lblModName,
					new Separator(Orientation.VERTICAL),
					new Separator(Orientation.VERTICAL),
					getBoldLabel(bundle.getString("Dialog.IndexArmaAddonsStatus.StatusBar.mods-left")),
					lblModsLeft,
					new Separator(Orientation.VERTICAL),
					getBoldLabel(bundle.getString("Dialog.IndexArmaAddonsStatus.StatusBar.mods-finished")),
					lblModsFinished,
					new Separator(Orientation.VERTICAL),
					getBoldLabel(bundle.getString("Dialog.IndexArmaAddonsStatus.StatusBar.errors")),
					lblErrorCount,
					new Separator(Orientation.VERTICAL),
					getBoldLabel(bundle.getString("Dialog.IndexArmaAddonsStatus.StatusBar.warnings")),
					lblWarningCount,
					new Separator(Orientation.VERTICAL),
					getBoldLabel(bundle.getString("Dialog.IndexArmaAddonsStatus.StatusBar.time-elapsed")),
					lblTimeElapsed
			);
		}

		private Label getBoldLabel(@NotNull String text) {
			Label lbl = new Label(text);
			lbl.setFont(BOLD_FONT);
			return lbl;
		}

		public void setModName(@NotNull String name) {
			lblModName.setText(name);
		}

		public void setModsLeft(int modsLeft) {
			lblModsLeft.setText(modsLeft + "");
		}

		public void setModsFinished(int modsFinished) {
			lblModsFinished.setText(modsFinished + "");
		}

		public void setErrorCount(int errorCount) {
			lblErrorCount.setText(errorCount + "");
		}

		public void setWarningCount(int warningCount) {
			lblWarningCount.setText(warningCount + "");
		}

		public void setTimeElapsed(int minutes) {
			lblTimeElapsed.setText(String.format(bundle.getString("Dialog.IndexArmaAddonsStatus.StatusBar.time-elapsed-minutes-f"), minutes));
		}
	}
}

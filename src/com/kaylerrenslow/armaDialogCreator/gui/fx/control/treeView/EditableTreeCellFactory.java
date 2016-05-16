package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;


import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

class EditableTreeCellFactory<E> extends TreeCell<TreeItemData<?>> {
	/** How long it takes for the current hovered tree item that is expandable for it to expand and show its children. Value is in milliseconds. */
	private static final long WAIT_DURATION_TREE_VIEW_FOLDER = 500;
	private static final Color COLOR_TREE_VIEW_DRAG = Color.ORANGE;

	private final ITreeCellSelectionUpdate treeCellSelectionUpdate;

	private TextField textField;

	private long waitStartTime = 0;

	private static TreeItem<TreeItemData<?>> dragging; //must be static since the factory is created for each cell and dragging takes place over more than once cell

	EditableTreeCellFactory(@Nullable ITreeCellSelectionUpdate treeCellSelectionUpdate) {
		this.treeCellSelectionUpdate = treeCellSelectionUpdate;
		this.setEditable(true);

		// first method called when the user clicks and drags a tree item
		this.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//				// a child of root element. since the treeview has a hidden real root element, the 'fake' root element has the real root element as its parent
				//				// So we need to call getParent() twice to check if the user is trying to drag and drop a 'fake' root element
				//				if (getTreeItem().getParent().getParent() == null) {
				//					getTreeItem().setExpanded(true);
				//					event.consume();
				//					return;
				//				}

				if (getTreeItem().getValue().isPlaceholder()) {
					event.consume();
					return;
				}

				// disables right clicking to start drag and drop
				if (event.isSecondaryButtonDown()) {
					event.consume();
					return;
				}

				EditableTreeView view = (EditableTreeView) getTreeView();

				Dragboard dragboard = view.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content;
				dragging = getTreeItem();
				content = new ClipboardContent();
				content.putString("hi");// don't remove. drag and drop doesn't work if this doesn't happen for a strange reason
				dragboard.setContent(content);

				event.consume();

			}
		});

		this.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				// not dragging on the tree
				if (getTreeItem() == null) {
					event.consume();
					return;
				}

				// trying to drag into itself
				if (getTreeItem().getValue().equals(getTreeView().getSelectionModel().getSelectedItem().getValue())) {
					event.consume();
					return;
				}

				// if it's a folder, we need to make sure it doesn't drag itself into its children
				if (dragging.getValue().isFolder()) {
					if (TreeUtil.hasChild(dragging, getTreeItem())) {
						event.consume();
						return;
					}
				}

				// auto expands the hovered tree item if it has children after a period of time.
				// timer is reset when the mouse moves away from the current hovered item
				if (!getTreeItem().isLeaf() || getTreeItem().getValue().isFolder()) {
					if (!getTreeItem().isExpanded()) {
						if (waitStartTime == 0) {
							waitStartTime = System.currentTimeMillis();
						} else {
							long now = System.currentTimeMillis();
							// wait a while before the tree item is expanded
							if (waitStartTime + WAIT_DURATION_TREE_VIEW_FOLDER <= now) {
								getTreeItem().setExpanded(true);
								waitStartTime = 0;
							}
						}
					}
				}

				// when it reaches this point, the tree item is okay to move
				event.acceptTransferModes(TransferMode.MOVE);
				setEffect(new InnerShadow(1.0, 0.0, 2.0, COLOR_TREE_VIEW_DRAG));
			}
		});

		this.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				event.acceptTransferModes(TransferMode.MOVE);

				// add a placeholder treeitem to the dragging's parent if it is a folder that will be empty after the move
				if (dragging.getParent().getValue().isFolder() && dragging.getParent().getChildren().size() == 1) {
					dragging.getParent().getChildren().add(new MoveableTreeItem());
				}

				// remove the dragging item's last position and add it to the new parent
				dragging.getParent().getChildren().remove(dragging);
				int index = getTreeItem().getParent().getChildren().lastIndexOf(getTreeItem());
				getTreeItem().getParent().getChildren().add(index, dragging);
				getTreeView().getSelectionModel().select(index + 1);

				// if the location where dragging was dropped was into a folder, remove the placeholder item in that folder if there is one
				if (getTreeItem().getParent().getValue().isFolder() && getTreeItem().getParent().getChildren().size() == 2 && getTreeItem().getParent().getChildren().get(1).getValue().isPlaceholder()) {
					getTreeItem().getParent().getChildren().remove(1);
				}

				dragging = null;
				getTreeView().getSelectionModel().clearSelection();
				event.consume();
			}

		});

		this.setOnDragExited(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if (getTreeItem() == null) {
					// this happens when something is still being dragged but it isn't over the tree
					event.consume();
					return;
				}

				waitStartTime = 0;
				setEffect(null);
				event.consume();
			}

		});
	}

	EditableTreeCellFactory getNewInstance() {
		return new EditableTreeCellFactory(this.treeCellSelectionUpdate);
	}

	@Override
	public void startEdit() {
		if (getTreeItem() == null || getTreeItem().getValue().isPlaceholder()) {
			return;
		}
		super.startEdit();
		if (textField == null) {
			createTextField();
		}
		setText(null);
		setGraphic(textField);
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getTreeItem().getValue().getText());
		setGraphic(getTreeItem().getGraphic());
		textField = null;
	}

	@Override
	protected void updateItem(TreeItemData node, boolean empty) {
		super.updateItem(node, empty);
		// this adds a textfield to the tree item to get a new name
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setText(null);
				setGraphic(textField);
			} else {
				if (textField != null) {
					setText(textField.getText());
					getTreeItem().getValue().setText(textField.getText());
					textField = null;
				} else {
					setText(getItem().getText());
				}
				setGraphic(getTreeItem().getGraphic());
			}
		}
	}

	private void createTextField() {
		textField = new TextField(getString());
		textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(getItem());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});

	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}

}

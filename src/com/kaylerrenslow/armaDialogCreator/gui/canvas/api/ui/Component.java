package com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui;


import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.action.ActionEvent;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.action.ActionListener;

/**
 * Created by Kayler on 05/12/2016.
 */
public class Component extends PaintedRegion {

	private ActionListener onClickListener;

	public Component(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
//
//	public void setOnRightClick(ActionListener listener) {
//		this.onClickListener = listener;
//	}
//
//	public void removeRightClickListener() {
//		this.onClickListener = null;
//	}
//
//	public void fireRightClickEvent() {
//		if (this.onClickListener != null) {
//			this.onClickListener.actionPerformed(new ActionEvent(this));
//		}
//	}

}

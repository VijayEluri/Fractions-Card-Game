package tugstory;

import java.awt.image.BufferedImage;

import manipulatives.ManModel;
import pebblebag.TugImages;

public class TugPersonView {
	private BufferedImage normal;
	private BufferedImage rotated;
	private BufferedImage lost;
	private BufferedImage fallen;
	private boolean showNormal;
	private boolean showLost;
	private boolean mySide;
	private boolean showFallen;
	private ManModel model;
	
	public TugPersonView(BufferedImage reg, BufferedImage lost, boolean side) {
		normal = reg;
		mySide = side;
		rotated = TugImages.rotatePerson(reg, Math.PI/8, side);
		this.fallen = TugImages.rotatePerson(lost, -1 * Math.PI/2, !side);
		this.lost = TugImages.rotatePerson(lost, Math.PI/2, !side);
		showLost = false;
		model = new ManModel();
	}
	
	public BufferedImage getImage() {
		if(showFallen) {
			if(showLost) return lost;
			return fallen;
		}
		if(showNormal) {
			return normal;
		}
		return rotated;
	}
	
	public void personFalls(boolean didTheyLose) {
		showFallen = true;
		showLost = didTheyLose;
	}
	
	public void personWon() {
		showNormal = true;
	}
	
	public boolean fallen() {
		return showFallen;
	}
	
	public boolean onMySide() {
		return mySide;
	}
	
	public BufferedImage getNormalImage() {
		return normal;
	}
	
	public void flipImage() {
		showNormal = !showNormal;
	}
	
	public boolean isUpright() {
		return showNormal;
	}
	
	public int getYOffset() {
		return model.getY();
	}
	
	public void jumpUpAndDown() {
		if(!fallen()) {
			model.jumpUpAndDown();
		}
	}
}

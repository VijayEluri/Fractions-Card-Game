package tugstory;
/*
 * TugPanel
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import manipulatives.AssetView;
import manipulatives.ManipInterface;
import basic.Constants;
import deck.DeckView;
import extras.Debug;
import extras.GameImages;


public class TugPanel extends JPanel {
	private int myside;
	private int opposide;
	private ArrayList<TugPersonView> mypeeps;
	private ArrayList<TugPersonView> oppopeeps;
	private ArrayList<TugListener> listeners;
	private int flagX;
	private int flagI;
	private int ropeX;
	private boolean canMoveFlag;
	private TugPersonView regImage;
	private int ticksPassedTugging;
	private boolean donePulling;
	private boolean wasTold;
	
	public static final int TUG_WIDTH = 900;
	public static final int TUG_HEIGHT = 640;
	public static final int LOST_DX = 50;
	public static final int LOST_DY = 30;
	public static final int TUG_DX = 15;
	public static final int TUG_LINE_DIVISOR = 20;
	public static final int TUG_TICK = 50;
	public static final double TUG_MS_ACTION = 200;
	public static final double TUG_PROBABILITY = (TUG_TICK/(TUG_MS_ACTION))/2;

	public TugPanel(int myside, int theirside) {
		initialSetup(myside, theirside);
		mypeeps = setupPeople(myside, true);
		oppopeeps = setupPeople(theirside, false);
		
		//		add(new JLabel(""+myside), BorderLayout.WEST);
		//		add(new JLabel(""+opposide), BorderLayout.EAST);
	}
	
	public TugPanel(List<DeckView> decksInPlay, boolean told) {
		ArrayList<AssetView> myAssets = new ArrayList<AssetView>();
		ArrayList<AssetView> theirAssets = new ArrayList<AssetView>();
		for(DeckView dv: decksInPlay) {
			if(dv.getPlayer().isHuman()) {
				potentiallyAddAssets(dv, myAssets);
			}else{
				potentiallyAddAssets(dv, theirAssets);
			}
		}
		initialSetup(myAssets.size(), theirAssets.size());
		if(Constants.USE_CHARACTER_MANIPS_IN_CALC || Constants.USE_CHARACTER_MANIPS_IN_GAME) {
			mypeeps = setupPeople(myAssets, true);
			oppopeeps = setupPeople(theirAssets, false);
		}else{
			mypeeps = setupPeople(myAssets.size(), true);
			oppopeeps = setupPeople(theirAssets.size(), false);
		}
		wasTold = told;
	}
	
	private void potentiallyAddAssets(DeckView dv, ArrayList<AssetView> assets) {
		for(ManipInterface mi: dv.getManipulatives()) {
			if(!mi.isStinky()) {
				assets.add((AssetView) mi);
			}
		}
	}
	
	private void initialSetup(int myside, int theirside) {
		setPreferredSize(new Dimension(TUG_WIDTH, TUG_HEIGHT));
		this.myside = myside;
		opposide = theirside;
		canMoveFlag = false;
		
		regImage = new TugPersonView(GameImages.getMan(), GameImages.getLostMan(), true);
		flagI = 0;
		flagX = TUG_WIDTH/2 - regImage.getNormalImage().getWidth()/8;
		ropeX = TUG_WIDTH/2 - GameImages.getTugRope().getWidth()/2;
		listeners = new ArrayList<TugListener>();
		donePulling = false;
		setLayout(new BorderLayout());		
	}
	
	//This is initialization code for the the Timer
	public void initializeTug() {
		
	}

	public ArrayList<TugPersonView> setupPeople(int num, boolean onMyTeam) {
		ArrayList<TugPersonView> peeps = new ArrayList<TugPersonView>();
		for(int i = 0; i < num; i++) {
			peeps.add(new TugPersonView(GameImages.getMan(), GameImages.getLostMan(), onMyTeam));
		}
		return peeps;
	}

	public ArrayList<TugPersonView> setupPeople(ArrayList<AssetView> assets, boolean onMyTeam) {
		ArrayList<TugPersonView> peeps = new ArrayList<TugPersonView>();
		for(int i = 0; i < assets.size(); i++) {
			peeps.add(new TugPersonView(assets.get(i), onMyTeam));
		}
		return peeps;
	}
	
	public void tugAction() {
		rotatePeople(oppopeeps, doILose());
		rotatePeople(mypeeps, doIWin());
		repaint();
	}

	private void rotatePeople(ArrayList<TugPersonView> men, boolean gonnaWin) {
		if((Math.random() < TUG_PROBABILITY && men.size() > 0 && men.get(0).isUpright()) || (ticksPassedTugging > TUG_MS_ACTION/TUG_TICK && men.size() > 0 && !men.get(0).isUpright())) {
			for(int i = 0; i < men.size(); i++) {
				TugPersonView tpv = men.get(i);
				tpv.flipImage();
			}
			if(gonnaWin && canMoveFlag) {
				repositionRope();
				canMoveFlag = false;
			}
			ticksPassedTugging = 0;
			repaint();
		}else if(men.size() > 0 && !men.get(0).isUpright()) {
			ticksPassedTugging++;
		}
	}
	
	private void makePeopleFall(ArrayList<TugPersonView> men) {
		makePeopleFall(men, men.size(), true);
	}
	
	private void makePeopleFall(ArrayList<TugPersonView> men, int amount, boolean didTheyLose) {
		makePeopleFall(men, 0, amount, didTheyLose);
	}
	
	/*also deprecated and transformed so watch out */
	private void makePeopleFall(ArrayList<TugPersonView> men, int start, int amount, boolean didTheyLose) {
//		amount = Math.min(men.size(), start+amount);
		Debug.println("start: "+start+" amount: "+amount);
		start = Math.min(men.size()-1, start);
		amount = Math.max(0, men.size()-(start-amount));
		for(int i = start; i > start - amount; i--) {
			TugPersonView tpv = men.get(i);
			tpv.personFalls(didTheyLose);
		}
	}
	
	private void makePersonFall(ArrayList<TugPersonView> men, int start, boolean didTheyLose) {
//		amount = Math.min(men.size(), start+amount);
		start--;
		int oldStart = start;
		start = Math.max(0, Math.min(men.size()-1, start));
		Debug.println("oldStart:" + oldStart + ", start: " + start);
		TugPersonView tpv = men.get(start);
		tpv.personFalls(didTheyLose);
	}
	
	private void getPeopleUpright(ArrayList<TugPersonView> men, int amountFallen) {
		getPeopleUpright(men, amountFallen, true);
	}
	
	//deprecated no longer using
	private void getPeopleUpright(ArrayList<TugPersonView> men, int amountFallen, boolean didIWin) {
//		makePeopleFall(men, men.size()-(amountFallen), men.size(), !didIWin);
		for(int i = 0; i < men.size()-amountFallen; i++) {
			TugPersonView tpv = men.get(i);
			tpv.personWon();
		}
	}
	
	private void getPeopleUpright(ArrayList<TugPersonView> men) {
		for(TugPersonView tpv:men) {
			if(!tpv.fallen()) {
				tpv.personWon();
			}
		}
	}
	
	public boolean contestOver() {
		return (flagX > (getWidth()/2.0 + centerToWinDistance())) || (flagX < (getWidth()/2.0 - centerToWinDistance())) || (oneSideDown()); 
	}
	
	public boolean oneSideDown() {
		boolean isDown = numDown(mypeeps) == mypeeps.size() || numDown(oppopeeps) == oppopeeps.size();
		Debug.printlnVerbose("numDown: " + numDown(mypeeps) + "/" + mypeeps.size() + " ------------- " + numDown(oppopeeps) + "/" + oppopeeps.size() + " | " + isDown);
		return isDown;
	}
	
	private int numDown(ArrayList<TugPersonView> peeps) {
		int down = 0;
		for(TugPersonView tpv:peeps) {
			if(tpv.fallen()) {
				down++;
			}
		}
		return down;
	}
	
	public double centerToWinDistance() {
		return ((double)getWidth())/TUG_LINE_DIVISOR;
	}
	
	public void repositionRope() {
		if(isItATie()) return;
		int modifier = 1;
		if(doIWin()) {
			modifier = -1;
		}
		int delta = modifier * calcDifference();
		flagI += delta;
	}
	
	public void moveFlagNextTime() {
		canMoveFlag = true;
	}
	
	public int calcDifference() {
		return Math.abs(myside-opposide);
	}
	
	public int calcNumFall() {
		return Math.min(myside, opposide);
	}
	
	private boolean doIWin() {
		return (myside > opposide);
	}
	
	private boolean doILose() {
		return (opposide > myside);
	}
	
	private boolean isItATie() {
		return myside == opposide;
	}

	public void endTugOfWar(boolean takeOut) {
		while(!oneSideDown()) {
			makeOneSetFall();
		}
		repaint();
		if(isItATie()) {
			getPeopleUpright(mypeeps);
			getPeopleUpright(oppopeeps);
		} else if(doIWin()) {
//			makePeopleFall(oppopeeps);
			getPeopleUpright(mypeeps);
			repaint();
			makePeopleJump(mypeeps);
		}else{
//			makePeopleFall(mypeeps);
			getPeopleUpright(oppopeeps);
			repaint();
			makePeopleJump(oppopeeps);
		}
		donePulling = true;
		repaint();
	}
	
	public void makeOneSetFall() {
		Debug.println("making one set fall win? " + doIWin() + " lose? " + doILose());
		if(oneSideDown()) return;
		if(doIWin()) {
			makeOneFall(mypeeps, true);
			makeOneFall(oppopeeps, false);
		}else if(doILose()) {
			makeOneFall(oppopeeps, true);
			makeOneFall(mypeeps, false);
		}else{
			makeOneFall(mypeeps, true);
			makeOneFall(oppopeeps, true);
		}
		repaint();
	}
	
	private void makeOneFall(ArrayList<TugPersonView> men, boolean didIWin) {
		makePersonFall(men, men.size()-numDown(men), !didIWin);
	}
	
	private void makePeopleJump(ArrayList<TugPersonView> men) {
		for(TugPersonView person:men) {
			person.jumpUpAndDown();
		}
	}
	
	public void makeAllJump() {
		makePeopleJump(mypeeps);
		makePeopleJump(oppopeeps);
		repaint();
	}
	
	public void startPulling() {
		TugPuller tugP = new TugPuller(this);
		Timer tugTimer = new Timer(TUG_TICK, tugP);
		tugP.setTimer(tugTimer);
		tugTimer.setInitialDelay(1000);
		tugTimer.start();
	}
	
	public void addListener(TugListener tl) {
		listeners.add(tl);
	}
	
	public void tugPanelDone() {
		for(TugListener l: listeners) {
			l.tugPanelDone(this);
		}
	}
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(GameImages.getTugBackground(), 0, 0, getWidth(), getHeight(), null);
		int theY = getHeight()/2;
//		BufferedImage buf = regImage.getNormalImage();
		BufferedImage buf = getAnchorImage();
		drawRope(g, getWidth(), theY, buf);
		drawSides(g, theY, buf);
		if(donePulling) {
			drawText(g);
		}
	}
	
	private BufferedImage getAnchorImage() {
		return getAnchorTPV().getNormalImage();
	}
	
	private TugPersonView getAnchorTPV() {
		if(!(Constants.USE_CHARACTER_MANIPS_IN_CALC || Constants.USE_CHARACTER_MANIPS_IN_GAME)) {
			return regImage;
		}
		if(mypeeps == null || mypeeps.size() <= 0) {
			if(oppopeeps == null || oppopeeps.size() <= 0) {
				return regImage;
			}
			return oppopeeps.get(0);
		}
		return mypeeps.get(0);
	}
	
	private int calcManY(TugPersonView tpv, int y) {
		if(tpv.fallen()) {
			return y + LOST_DY;
		}
		return y;
	}
	
	private int calcManX(TugPersonView tpv, int x) {
		if(!tpv.fallen()){
			if(tpv.isUpright()) {
				return x;
			}else{
				if(tpv.onMySide()) {
					return x - TUG_DX;
				}
				return x + TUG_DX;
			}
		}
		if(tpv.onMySide()) {
			return x + LOST_DX;
		}
		return x - LOST_DX;
	}
	
	private void drawPerson(Graphics g, TugPersonView tpv, int x, int y) {
		BufferedImage man = tpv.getImage();
		y = calcManY(tpv, y);
		x = calcManX(tpv, x);
		g.drawImage(man, x, y + tpv.getYOffset(), null);
	}

	private void drawSides(Graphics g, int y, BufferedImage man) {
		double block = getWidth()/2-getWidth()/8;
		int mymanspace = Math.min(man.getWidth(), (int) (block / myside));
		int oppomanspace = Math.min(man.getWidth(), (int) (block / opposide));
		for(int i = 0; i < mypeeps.size(); i++) {
			drawPerson(g, mypeeps.get(i), i * mymanspace, y);
		}
		for(int i = 0; i < oppopeeps.size(); i++) {
			drawPerson(g, oppopeeps.get(i), (getWidth()-man.getWidth())-((i)*oppomanspace), y);
//			BufferedImage otherMan = oppopeeps.get(i).getImage();
//			int manY = calcManY(oppopeeps.get(i), y);
//			if(oppopeeps.get(i).isUpright()) {
//				g.drawImage(otherMan, (getWidth()-man.getWidth())-((i)*oppomanspace), manY, null);
//			}else{
//				g.drawImage(otherMan, ((getWidth()+15)-(man.getWidth()))-((i)*oppomanspace), manY, null);
//			}
		}
	}

	private void drawText(Graphics g) {
		Color oldColor = g.getColor();
		g.setFont(Constants.FONT_LARGE);
		g.setColor(Constants.STATUS_FOREGROUND);
		String finalMsg = determineFinalMessage();
		drawLabelMessage(g, finalMsg, !doILose());
		g.setColor(oldColor);
	}
	
	private String determineFinalMessage() {
		if(isItATie()) {
			return "It's a tie";
		}
		return calcDifference() + ((calcDifference()==1)? Constants.TUG_MSG_POINT:Constants.TUG_MSG_POINTS) + "!";
	}
	
	private void drawLabelMessage(Graphics g, String message, boolean ourSide) {
		int xPos = (int) (getWidth()*.05);
		if(!ourSide) {
			xPos = (int) (getWidth()*.7);
		}
		g.drawString(message, xPos, (int) (getHeight()/2.5));
	}

	private void drawRope(Graphics g, int length, int y, BufferedImage man) {
//old draw method of line and box
		//		g.drawLine(0, y+man.getHeight()/2, length, y+man.getHeight()/2);
//		Color c = g.getColor();
//		g.setColor(Color.RED);
//		g.fillRect(flagX, y+man.getHeight()/2, man.getWidth()/4, man.getHeight()/2);
//		g.setColor(c);
		//new method with images
		BufferedImage rope = GameImages.getTugRope();
		flagX = length/2 - regImage.getNormalImage().getWidth()/8 + flagI * length/TUG_WIDTH;
		ropeX = length/2 - rope.getWidth()/2 + flagI * length/TUG_WIDTH;
		g.drawImage(rope, ropeX, y+man.getHeight()/2, null);
		g.drawImage(GameImages.getRedFlag(), length/2+(flagI), y+man.getHeight()/2, null);
	}
	
	public boolean wasTold() {
		return wasTold;
	}

	/* Need to have a good shake pebble method
	 * that will have the pebbles randomly move in their locations
	 * yet still stay inside the bag.
	 */

	public static void main(String[] args) {
		JFrame frame = new JFrame("Tug Panel");

		//TODO: Modify the images so that the lines are better marked on the floor.
		TugPanel pPanel = new TugPanel(18, 2);
		frame.add(pPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		pPanel.startPulling();
		
	}
}

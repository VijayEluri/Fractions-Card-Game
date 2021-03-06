package basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import cards.CardView;
import deck.DeckView;
import extras.Debug;
import extras.GraphicUtils;

public class CardMover implements ActionListener {
	private GamePanel gPanel;
	private int numTimesMoved;
	private Timer timer;
	private int desiredX;
	private int desiredY;
	private CardView chosenCard;
	private DeckView chosenDeck;
	private Player computer;
	private Player opponent;
	private PossibleMove possibleMove;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 50;
	
	public CardMover(GamePanel p, PossibleMove pm, Player comp, Player oppo, CardView cv, DeckView dv) {
		gPanel = p;
		chosenCard = cv;
		chosenDeck = dv;
		possibleMove = pm;
		computer = comp;
		opponent = oppo;
		numTimesMoved = 0;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			//gPanel.disableUser();
			desiredX = chosenDeck.getCenterX();
			desiredY = chosenDeck.getCenterY();
			Debug.println("Moving to: " + desiredX + ", " + desiredY);
		}
		double curCardX = chosenCard.getX();
		double curCardY = chosenCard.getY();
		chosenCard.moveBy((int) increment(curCardX, desiredX), (int) increment(curCardY, desiredY));
		gPanel.repaint();
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			Debug.println("pebble ended at " + curCardX + ", " + curCardY);
			gPanel.repaint();
			numTimesMoved = 0;
			//stuff to have it finish completely
			timer.stop();
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		gPanel.enableUser();
		gPanel.setTurn(true);
		computer.performMove(possibleMove, opponent);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return GraphicUtils.incrementalMove(cur, desired, MAX_MOVES-numTimesMoved);
	}

}

package manipulatives;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import cards.CardView;
import deck.DeckView;

public class DeckViewPanel extends JPanel {
	private DeckView view;
	private CardView cardPlayed;
	
	public DeckViewPanel() {
		super();
	}
	
	public DeckViewPanel(DeckView dv, CardView cp) {
		super();
		view = dv;
		cardPlayed = cp;
	}
	
	public void setDeckView(DeckView dv) {
		view = dv;
	}
	
	public DeckView getDeckView() {
		return view;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(view != null) {
			view.drawAbsoluteDeck(g, cardPlayed, getHeight());
		}
	}
}

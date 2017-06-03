package Agents;

import java.util.ArrayList;

import Behaviours.InitiatorBehaviour;
import Utilities.Slot;

public class InitiatorAgent extends BaseAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final String type = "Central";

	public static final int AUCTION_TIMEOUT = 500;
	
	public final static ArrayList<Double> basePriceIterations = new ArrayList<Double>();

	private ArrayList<Slot> slotsToSell = new ArrayList<Slot>();

	public InitiatorAgent() {

		this.slotsToSell.add(new Slot("Slot", 0));
		System.out.println(">> Iniciator started working <<");

	}

	@Override
	protected void setup() {

		super.setup();
		System.out.println("[ English Auction started ]\n");
		addBehaviour(new InitiatorBehaviour(this, slotsToSell.get(0)));

	}

	@Override
	public String getType() {
		return this.type;
	}
	
	


}

package Agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Behaviours.ParticipantBehaviour;

public class Sensor extends BaseAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final String type = "Sensor";

	private int cost;
	private int precision;
	private ArrayList<Integer> dataSet = new ArrayList<Integer>();
	private int priority;

	// Money the participant has
	private double money;

	// Max price that every sensor are willing to pay
	private Map<String, Integer> interestedSlot = new HashMap<String, Integer>();

	public Sensor(int cost, int priority) {

		this.interestedSlot.put("Slot", (int) (Math.random() * 400) + 300);
		this.cost = cost;
		this.priority = priority;
		this.dataSet = randomData();
		this.money = (Math.random() * bid()) + 0;
	}

	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new ParticipantBehaviour(this));
	}

	@Override
	public String getType() {

		return this.type;
	}

	public ArrayList<Integer> randomData() {

		ArrayList<Integer> toReturn = new ArrayList<Integer>();

		int size = (int) (Math.random() * 20) + 1;

		for (int i = 0; i < size; i++)
			toReturn.add((int) (Math.random() * 20));

		return toReturn;

	}

	public int checkChangesInReadings() {

		int change = 1;
		int threshold = 10;

		if (this.dataSet.size() > 1) {
			if (this.dataSet.get(this.dataSet.size() - 2) > this.dataSet.get(this.dataSet.size() - 1) + threshold
					|| this.dataSet.get(this.dataSet.size() - 2) > this.dataSet.get(this.dataSet.size() - 1)
							- threshold) {
				change = 0;
			}
		}

		return change;

	}

	public double bid() {

		double normalizedCost = (this.cost - 10) / (10 - 1);

		return (normalizedCost * 1 + this.precision * 2 + checkChangesInReadings() * 5 + this.priority * 3
				+ this.dataSet.size() * 4) * 10;
	}

	public Map<String, Integer> getInterestedSlot() {
		return this.interestedSlot;
	}

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
}

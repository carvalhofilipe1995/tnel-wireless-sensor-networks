package Behaviours;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

import Agents.Sensor;

@SuppressWarnings("serial")
public class ParticipantBehaviour extends BaseBehaviour {

	public final int GETINFORM = 1;
	public final int GETCFP = 2;
	public final int PROPOSE = 3;
	public final int REFUSE = 4;
	public final int END = 5;

	Sensor sensor;
	String slotName;
	double maxPrice;
	boolean isLastWinner = false;
	double basePrice;

	AID initiator;

	private Map<String, Integer> slots = new HashMap<String, Integer>();

	public ParticipantBehaviour(Sensor sensor) {

		this.sensor = sensor;
		state = GETINFORM;
		slots = sensor.getInterestedSlot();

		System.out.println(sensor.getLocalName() + ": [SENSOR] The money I have: " + sensor.getMoney());
	}

	@Override
	public void action() {

		switch (state) {
		case GETINFORM:
			isInterested();
			break;
		case GETCFP:
			getCFP();
			break;
		case PROPOSE:
			propose();
			break;
		case REFUSE:
			refuse();
			break;
		default:
			System.out.println(sensor.getLocalName() + ": Working...");
		}

	}

	public void isInterested() {

		ACLMessage msg = sensor.blockingReceive();

		if (msg.getPerformative() == ACLMessage.INFORM) {
			initiator = msg.getSender();
			String message = msg.getContent();
			slotName = message.substring(6, message.length());
		}
		
		if (slots.get(slotName) == null)
			state = REFUSE;
		else {
			maxPrice = slots.get(slotName);
			state = GETCFP;
		}

	}

	public void getCFP() {

		ACLMessage msg = sensor.blockingReceive();

		if (msg.getPerformative() == ACLMessage.CFP) {

			System.out.println(sensor.getLocalName() + ": [SENSOR] Geting Call For Proposal...");
			String message = msg.getContent();

			int index = message.indexOf("Winner:");

			if (index > 0) {

				basePrice = Double.parseDouble(message.substring(12, message.indexOf(',')));
				String lastWinnerName = message.substring(index + 8, message.length() - 1);

				if (sensor.getLocalName() != lastWinnerName) {
					if (basePrice >= maxPrice || basePrice >= sensor.getMoney())
						state = REFUSE;
					else
						state = PROPOSE;
				} else {
					isLastWinner = true;
					state = PROPOSE;
				}
			} else {
				basePrice = Double.parseDouble(message.substring(12, message.length()));
				state = PROPOSE;
			}

		} else if (msg.getPerformative() == ACLMessage.INFORM) {

		} else if (msg.getPerformative() == ACLMessage.REQUEST) {

			sensor.setMoney(sensor.getMoney() - basePrice);
			System.out.println(
					sensor.getLocalName() + " [WINNER]: Slot purchased successfully... Now, I can send my data.");

		} else
			System.out.println(sensor.getLocalName() + " [WINNER]: Participant Behaviour->getCFP: Not INFORM message!");
	}

	public void propose() {

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println(sensor.getLocalName() + ": Proposing...");

		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.addReceiver(initiator);

		if (isLastWinner) {
			msg.setContent(String.valueOf(basePrice));
		} else {
			basePrice +=  (Math.random() * 11 + 1);
			msg.setContent(String.valueOf(basePrice));
		}

		sensor.send(msg);

		state = GETCFP;
	}

	public void refuse() {
		ACLMessage msg = new ACLMessage(ACLMessage.REFUSE);
		msg.addReceiver(initiator);

		System.out.println(sensor.getLocalName()
				+ ": Not interested or don't have enough money or don't want to pay that much!\n");

		sensor.send(msg);
		state = END;
	}

	@Override
	public boolean done() {

		return (state == END);
	}

}
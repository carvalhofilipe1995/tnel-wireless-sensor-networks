package Behaviours;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import Agents.InitiatorAgent;
import Utilities.Slot;

public class InitiatorBehaviour extends BaseBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final int INFORM = 1;
	public final int CFP = 2;
	public final int GET_PROPOSE = 3;
	public final int REQUEST = 4;
	public final int END = 5;

	private ArrayList<AID> participants = new ArrayList<>();
	private double priceIteration = 0;
	private int round = 0;
	InitiatorAgent ia;

	private AID winner = null;
	Slot slot = null;

	public InitiatorBehaviour(InitiatorAgent ia, Slot slot) {
		this.ia = ia;
		this.state = INFORM;
		this.slot = slot;
		this.priceIteration = slot.getCommonPrice() * 0.3;
	}

	@Override
	public void action() {

		switch (state) {
		case INFORM:
			informAuction();
			break;
		case CFP:
			callForProposal();
			break;
		case GET_PROPOSE:
			getPropose();
			break;
		case REQUEST:
			requestPayment();
			break;
		default:
			System.out.println(ia.getLocalName() + " [Central]: Working...");
		}

	}

	public void informAuction() {

		System.out.println(ia.getLocalName() + " [Central]: Informing about auction...");

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("Sensor");
		template.addServices(sd1);

		try {
			DFAgentDescription[] result = DFService.search(ia, template);

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

			for (int i = 0; i < result.length; ++i) {
				AID receiver = result[i].getName();
				System.out.println(
						ia.getLocalName() + " [SENSOR]: inform Auction receiver name: " + receiver.getLocalName());
				msg.addReceiver(receiver);
				participants.add(receiver);
			}

			msg.setContent("Slot: " + slot.getSlotName());
			ia.send(msg);

		} catch (FIPAException e) {
			e.printStackTrace();
		}

		state = CFP;
	}

	public void callForProposal() {
			
		round++;
		System.out.println(ia.getLocalName() + " [Central] : ROUND " + round + ": Call for proposal..." + " [BASE_PRICE] " + priceIteration + "\n");

		if (round == 1) {
			send(participants, "Base Price: " + priceIteration, ACLMessage.CFP);
			System.out.println(ia.getLocalName() + " [Central]: Base Price: [" + priceIteration + "]");
		} else {
			send(participants, "Base Price: " + priceIteration + ", Winner: " + winner.getLocalName() + "!",
					ACLMessage.CFP);
			System.out.println(ia.getLocalName() + " [Central]: Base Price: [" + priceIteration + "], Winner: ["
					+ winner.getLocalName() + "]");
		}

		state = GET_PROPOSE;
	}

	public void getPropose() {
		System.out.println(ia.getLocalName() + " [Central]: Getting Proposals...");

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < participants.size(); i++) {
			ACLMessage msg = ia.blockingReceive();

			if (msg.getPerformative() == ACLMessage.REFUSE){
				participants.remove(msg.getSender());
				i--;
			}else if (msg.getPerformative() == ACLMessage.PROPOSE) {

				double bidPrice = Double.parseDouble(msg.getContent());
				System.out.println(ia.getLocalName() + " [ROUND] " + round + " [Central]: received propose from "
						+ msg.getSender().getLocalName() + " with price " + bidPrice + "\n");

				if (bidPrice > priceIteration) {
					priceIteration = bidPrice;
					winner = msg.getSender();
				}
			}
		}

		if (participants.size() == 0) {
			state = REQUEST;
		} else if (participants.size() == 1) {
			ia.basePriceIterations.add(priceIteration);
			winner = participants.get(0);
			state = REQUEST;
		} else {
			ia.basePriceIterations.add(priceIteration);
			state = CFP;

		}
	}

	private void requestPayment() {

		send(participants, "Auction ended, the winner is " + winner.getLocalName() + " with price " + priceIteration,
				ACLMessage.INFORM);

		System.out.println(
				ia.getLocalName() + " [Central]: Auction ended, the winner is [" + winner.getLocalName() + "]" + " -> [VALUE] " + priceIteration);

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

		msg.addReceiver(winner);
		msg.setContent("pong");
		ia.send(msg);

		state = END;
	}

	@Override
	public boolean done() {
		return (state == END);
	}

}

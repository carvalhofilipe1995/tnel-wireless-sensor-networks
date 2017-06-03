package Agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class BaseAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseAgent() {

	}

	public abstract String getType();

	@Override
	protected void setup() {
		registerService();
	}

	@Override
	protected void takeDown() {
		System.out.println("AGENT QUIT! Name: " + this.getLocalName() + ", Type: " + this.getType());
		deregisterService();
	}

	private void registerService() {

		ServiceDescription sd = new ServiceDescription();
		sd.setType(getType());
		sd.setName(getName());

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			throw new IllegalStateException("Appeared problem during the service registration.", fe);
		}
	}

	protected void deregisterService() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			throw new IllegalStateException("Appeared problem during the service deregistration.", fe);
		}
	}

}

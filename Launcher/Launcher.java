package Launcher;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import org.jfree.ui.RefineryUtilities;

import Agents.InitiatorAgent;
import Agents.Sensor;
import Charts.BarChart;

public class Launcher {

	public static void main(String[] args) {
		Runtime rt = Runtime.instance();

		Profile p1 = new ProfileImpl();
		//p1.setParameter(...);
		ContainerController mainContainer = rt.createMainContainer(p1);

		AgentController ac1;
		try {
			ac1 = mainContainer.acceptNewAgent("Sensor1", new Sensor(10,2));
			ac1.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac2;
		try {
			ac2 = mainContainer.acceptNewAgent("Sensor2", new Sensor(7,5));
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("Sensor3", new Sensor(3,3));
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		AgentController ac4;
		try {
			ac4 = mainContainer.acceptNewAgent("InitiatorAgent", new InitiatorAgent());
			ac4.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final BarChart demo = new BarChart();
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
	}

}
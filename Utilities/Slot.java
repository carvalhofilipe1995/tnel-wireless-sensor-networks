package Utilities;

public class Slot {

	private String slotName;
	private double commonPrice;

	public Slot(String slotName, double commonPrice) {
		this.slotName = slotName;
		this.commonPrice = commonPrice;
	}

	public String getSlotName() {
		return slotName;
	}

	public void setSlotName(String productName) {
		this.slotName = productName;
	}

	public double getCommonPrice() {
		return commonPrice;
	}

	public void setCommonPrice(double commonPrice) {
		this.commonPrice = commonPrice;
	}

}

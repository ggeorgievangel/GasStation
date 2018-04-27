package gasStation;

public class Car {

	enum FuelType {GAS, BENZ, DIZEL}
	private FuelType fuelType;
	private int liters;
	private int kolonka;
	
	public int getKolonka() {
		return kolonka;
	}
	
	public int getLiters() {
		return liters;
	}
	
	public FuelType getFuelType() {
		return fuelType;
	}
	
	public void setKolonka(int kolonka) {
		this.kolonka = kolonka;
	}
	
	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}
	
	public void setLiters(int liters) {
		this.liters = liters;
	}
	
}

package demo;

import gasStation.Car;
import gasStation.GasStation;

public class Demo {

	public static void main(String[] args) {
		
		GasStation station = new GasStation();
		while(true) {
			for (int i = 0; i < 10; i++) {
				station.enterGasStation(new Car());
			}
			
			try {
				Thread.sleep(30*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

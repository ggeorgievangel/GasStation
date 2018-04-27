package gasStation;

import java.util.Queue;
import java.util.Random;

import gasStation.Car.FuelType;

public class Worker extends Thread {
	
	private static final int MIN_FUEL = 10;
	private static final int MAX_FUEL = 40;
	static GasStation station;
	
	@Override
	public void run() {
		while(true) {
			// obikalq vsichki kolonki
			for (int i = 0; i < station.getKolonki().size(); i++) {
				Queue<Car> kolonka = station.getKolonki().get(i);
				Car car;
				synchronized(kolonka) {
					if(kolonka.isEmpty()) {
						continue;
					}
					// ako ima kola - sleep za 5 sek sled koeto prashta kolat na kasa
					car = kolonka.poll();
				}
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					System.out.println("ops");
				}
				
				FuelType fuelType = Car.FuelType.values()[new Random().nextInt(Car.FuelType.values().length)];
				if(car != null) {
					car.setFuelType(fuelType);
					int liters = new Random().nextInt(MAX_FUEL - MIN_FUEL+1) + MIN_FUEL;
					car.setLiters(liters);
					car.setKolonka(i+1);
					System.out.println("This car get "  + liters + " liters " + fuelType.toString() + " from kolonka " + car.getKolonka() +". Now go pay!");
					station.goTocashierQueue(car); 
				}
				
				
			}
		}
	}
}

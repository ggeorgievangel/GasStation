package gasStation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class Cashier extends Thread {
	static GasStation station;
	private ArrayBlockingQueue<Car> kasa;

	public Cashier(ArrayBlockingQueue<Car> kasa) {
		this.kasa = kasa;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Car car = kasa.take();
				Thread.sleep(2000);
				// add to collection - car, fuel type, liters
				System.out.println("Car paid for " + car.getLiters() + " liters fuel: " + car.getFuelType().toString() + " from kolonka " + car.getKolonka());
				station.addData(car.getKolonka(), car.getFuelType(), car.getLiters(), LocalDateTime.now());
				
			} catch (InterruptedException e) {
				System.out.println("ops: " + e.getMessage());
			}
			
		}
	}
	
	
}

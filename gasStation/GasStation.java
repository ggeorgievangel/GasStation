package gasStation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import gasStation.Car.FuelType;

public class GasStation {

	private static class StatisticsReporter extends Thread {
	
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(50_000);
					DBManager.getInstance().getAllLoadings();
					DBManager.getInstance().getAllLoadedCars();
					DBManager.getInstance().getLoadedFuelByType();
					// statistic 4
				} catch (InterruptedException e) {
					System.out.println("ops");
				}
			}
		}
	}
	
	private Cashier cashier1;
	private Cashier cashier2;
	private Worker woerker1  = new Worker();
	private Worker woerker2 = new Worker();
	private ArrayList<Queue<Car>> kolonki = new ArrayList<>();
	private ArrayList<ArrayBlockingQueue<Car>> kasi = new ArrayList<>();
	
	// kolonka - fuel type - date - liters
	private static HashMap<Integer, HashMap<FuelType, ConcurrentHashMap<LocalDateTime, Integer>>> statistics;
	
	public GasStation() {
		Worker.station = this;
		Cashier.station = this;
		statistics = new HashMap<>();
		for (int i = 0; i < 5; i++) {
			kolonki.add(new LinkedList<>());
			statistics.put(i+1, new HashMap<>());
			statistics.get(i+1).put(Car.FuelType.BENZ, new ConcurrentHashMap<>());
			statistics.get(i+1).put(Car.FuelType.DIZEL, new ConcurrentHashMap<>());
			statistics.get(i+1).put(Car.FuelType.GAS, new ConcurrentHashMap<>());
		}

		for (int i = 0; i < 2; i++) {
			kasi.add(new ArrayBlockingQueue<>(10));
		}
		cashier1 = new Cashier(kasi.get(0));
		cashier2 = new Cashier(kasi.get(1));
		cashier1.start();
		cashier2.start();
		woerker1.start();
		woerker2.start();	
		StatisticsReporter reporter = new StatisticsReporter();
		reporter.setDaemon(true);
		reporter.start();
	}
	
	public void enterGasStation(Car c){
		kolonki.get(new Random().nextInt(kolonki.size())).offer(c);
	}

	public List<Queue<Car>> getKolonki() {
		return Collections.unmodifiableList(kolonki);
	}

	public void goTocashierQueue(Car car) {
		kasi.get(new Random().nextInt(kasi.size())).offer(car);
	}
	
	public void addData(int kolonka, FuelType fuelType, int liters, LocalDateTime date) {
		statistics.get(kolonka).get(fuelType).put(date, liters);
		DBManager.getInstance().insertStatisticIntoDB(kolonka, fuelType, liters, date);
	}
	
	public static void printStatistics() {
		for(Integer kolonka : statistics.keySet()) {
			System.out.println("Kolonka: "  + kolonka + " fuel statistics: ");
			for(FuelType f : statistics.get(kolonka).keySet()) {
				System.out.println("     " + f.toString() + ": ");
				for(Entry<LocalDateTime, Integer> e : statistics.get(kolonka).get(f).entrySet()) {
					System.out.println("          " + e.getValue() + " liters on : "+ e.getKey().toString() + " date");
				} 
			}
		}
	}

	static void writeReportsInFile(String content, int number) {
		File f = new File("Report -" + number + "- " +LocalDate.now()+".txt");
		try(FileWriter fw = new FileWriter(f);) {
			fw.write(content);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}

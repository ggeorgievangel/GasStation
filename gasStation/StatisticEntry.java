package gasStation;

import java.time.LocalDateTime;

public class StatisticEntry implements Comparable<StatisticEntry>{

	private int kolonkaId;
	private String fuelType;
	private int liters;
	private LocalDateTime loadingTime;
	
	public StatisticEntry(int kolonkaId, String fuelType, int liters, LocalDateTime dateTime) {
		this.kolonkaId = kolonkaId;
		this.fuelType = fuelType;
		this.liters = liters;
		this.loadingTime = dateTime;
	}

	@Override
	public int compareTo(StatisticEntry statistic) {
		return this.loadingTime.compareTo(statistic.loadingTime);
	}
	
	
	@Override
	public String toString() {
		return "          Fuel Type: " + this.fuelType + "; Liters: " + this.liters + "; Loading DateTime: " + this.loadingTime.toString();
	}
}

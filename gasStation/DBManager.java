package gasStation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import gasStation.Car.FuelType;

public class DBManager {

	// make connection to DB and use it with reference to all places needed
	private static DBManager instance = new DBManager();
	private static final String PASS = "123asd";
	private static Connection  connection; //reference to connection to DB
	
	private DBManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} 
		catch (ClassNotFoundException e) {
			System.out.println("Driver not loaded or does not exist! Abort the mission!");
		}
		System.out.println("Driver loaded!\n");
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", PASS);
		} 
		catch (SQLException e) {
			System.out.println("Wrong credentials");
			System.out.println(e.getMessage());
		}
	}
	
	public synchronized static DBManager getInstance() {
		return instance;
	}
	
	public Connection getConection() {
		return connection;
	}
	
	// statistics: 
	public void getAllLoadings(){
		StringBuilder reportForFile = new StringBuilder();
		String sql =  "SELECT kolonka_id, fuel_type, liters, date_time FROM gas_station";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)) {
			ResultSet result = statement.executeQuery();
			
			// kolonka -> collection Statistic entry(zarejdaniq za tazi kolonka)
							// sortiran po data
			TreeMap<Integer, TreeSet<StatisticEntry>> loadings = new TreeMap<>();
			
			while(result.next()){
				int kolonka_id = result.getInt("kolonka_id");
				StatisticEntry loadingStatistic = new StatisticEntry(
												kolonka_id, 
												result.getString("fuel_type"),
												result.getInt("liters"),
												result.getTimestamp("date_time").toLocalDateTime());
				if(!loadings.containsKey(kolonka_id)){
					loadings.put(kolonka_id, new TreeSet<>());
				}
				loadings.get(kolonka_id).add(loadingStatistic);
			}
			result.close();
			for(Map.Entry<Integer, TreeSet<StatisticEntry>> entry : loadings.entrySet()) {
				
				String reportText1 ="Kolonka " + entry.getKey();
				reportForFile.append(reportText1 + "\n");
				System.out.println(reportText1);
				
				for(StatisticEntry loadingStatistic : entry.getValue()){
					reportForFile.append(loadingStatistic + "\n");
					System.out.println(loadingStatistic);
				}
			}
			GasStation.writeReportsInFile(reportForFile.toString(), 1);
		} 
		catch (SQLException e) {
			System.out.println("ops " + e.getMessage());
		}
	}

	public void getAllLoadedCars() {
		String sql = "SELECT kolonka_id, COUNT(fuel_type) AS fuels FROM gas_station WHERE DATE(date_time) = CURDATE() GROUP BY kolonka_id";
		StringBuilder reportForFile = new StringBuilder();
		try (PreparedStatement pState = connection.prepareStatement(sql) ) {
			ResultSet result = pState.executeQuery();
			while (result.next()) {
				reportForFile.append("Kolonka " + result.getInt("kolonka_id") + " - Automobiles: " + result.getInt("fuels") + "\n");
			}
			result.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		GasStation.writeReportsInFile(reportForFile.toString(), 2);
	}
	
	public void getLoadedFuelByType() {
		String sql = "SELECT DISTINCT(fuel_type) AS Fuel_types, SUM(liters) AS Liters FROM gas_station GROUP BY fuel_type";
		StringBuilder reportForFile = new StringBuilder();
		try (PreparedStatement pState = connection.prepareStatement(sql) ) {
			ResultSet result = pState.executeQuery();
			while (result.next()) {
				reportForFile.append("Fuel " + result.getString("Fuel_types") + " - Liters: " + result.getInt("Liters") + "\n");
			}
			result.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		GasStation.writeReportsInFile(reportForFile.toString(), 3);
	}
	
	
	public void insertStatisticIntoDB(int kolonka, FuelType fuelType, int liters, LocalDateTime date) {
		String sql =  "INSERT INTO gas_station(kolonka_id, fuel_type, liters, date_time) VALUES (?, ?, ?, ?);";
		Timestamp timeStamp = Timestamp.valueOf(date);	
		try(PreparedStatement pStatement = connection.prepareStatement(sql) ) {
			pStatement.setInt(1, kolonka);
			pStatement.setString(2, fuelType.toString());
			pStatement.setInt(3, liters);
			pStatement.setTimestamp(4, timeStamp);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
package run;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import tcc.Handler;

public class RunHandler {
	
	public static void main(String[] args) throws IOException, SQLException, ParseException, ClassNotFoundException {
		
		int station = 113;
		String file = "113_Piracicaba_MP10";
		
		Handler handler = new Handler();
		handler.clear(file + ".csv");
		ArrayList<ArrayList<String>> data = handler.read("cleaned_" + file + ".csv");
		handler.save(data, station);
		handler.removeMissingData();

	}

}

package test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import model.ModelHandler;

public class TestHandler {
	
	public static void main(String[] args) throws IOException, SQLException, ParseException, ClassNotFoundException {
		
		int station = 113;
		String file = "113_Piracicaba_MP10";
		
		ModelHandler handler = new ModelHandler();
		handler.clear(file + ".csv");
		ArrayList<ArrayList<String>> data = handler.read("cleaned_" + file + ".csv");
		handler.save(data, station);
		handler.removeMissingData();

	}

}

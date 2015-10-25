package go.euro.main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opencsv.CSVWriter;

public class Main {

	public static void main(String[] args) {
		// condition to check null inputs or only spaces
		if (args.length > 0 && args[0].trim().length() > 0) {
			getDataFromURL(args[0]);
		} else {
			System.err.println("No Input Provided");
		}
	}

	private static void getDataFromURL(String input) {
		try {
			String url = "http://api.goeuro.com/api/v2/position/suggest/en/"
					.concat(input);

			URL inputURL = new URL(url);
			URLConnection connection = inputURL.openConnection();
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String readData;
			String jsonString = null;
			while ((readData = bufferReader.readLine()) != null) {
				jsonString = readData;
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			generateCSVFile(jsonArray);
			bufferReader.close();

		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void generateCSVFile(JSONArray jsonArray) {
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(
					"./goEuro.csv"), ',');

			String[] test = { "_id", "name", "type", "latitude", "longitude" };
			writer.writeNext(test, true);
			for (int n = 0; n < jsonArray.length(); n++) {
				List<String> dataForCSVFile = new ArrayList<String>();
				JSONObject jsonObject;
				jsonObject = jsonArray.getJSONObject(n);

				dataForCSVFile.add(Long.toString(jsonObject.getLong("_id")));
				dataForCSVFile.add((String) jsonObject.get("name"));
				dataForCSVFile.add((String) jsonObject.get("type"));

				JSONObject innerObject = jsonObject
						.getJSONObject("geo_position");
				dataForCSVFile.add(Double.toString(innerObject
						.getDouble("latitude")));
				dataForCSVFile.add(Double.toString(innerObject
						.getDouble("longitude")));
				String[] WriteData = new String[dataForCSVFile.size()];
				WriteData = dataForCSVFile.toArray(WriteData);
				writer.writeNext(WriteData, true);
			}
			writer.close();
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

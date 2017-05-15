package moonBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class that connects to the U.S. Naval Observatory's API to get the moon phase.
 * The API is described here: http://aa.usno.navy.mil/data/docs/api.php#phase
 * 
 * TODO: Add error checking and handling (JSON includes an error if it exists)
 * 
 * @author Tasso Sales-Filho
 *
 */
public class USNO
{
	public static void main(String[] args) throws IOException
	{
		// For testing purposes
		USNO.getMoonPhase(1);
	}
	
	/**
	 * Get the moon phase from the website and return it as a string that can be
	 * written in Discord. The API requires requests to be sent in this format:
	 * 		http://api.usno.navy.mil/moon/phase?date=DATE&nump=NUMP
	 * where DATE is the date in "MM/DD/YYYY" format
	 * and NUMP is the number of phases from DATE to be returned.
	 */
	public static String getMoonPhase(int numPhases) throws IOException
	{
		// This maps each moon phase to the one preceding it. This is
		// used to resolve the case where the API, given the current date,
		// returns the NEXT moon phase and not the CURRENT moon phase.
		// This happens because the API returns the moon phases starting
		// from the specified date, and doesn't include the moon phase
		// of the current date (only if today falls on the day of a new phase).
		HashMap<String, String> moonMap = new HashMap<String, String>();
		moonMap.put("New Moon", "Last Quarter");
		moonMap.put("First Quarter", "New Moon");
		moonMap.put("Full Moon", "First Quarter");
		moonMap.put("Last Quarter", "Full Moon");
		
		// Get the current date in month/day/year format, since that's what the site needs
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
		Date currentDate = new Date(); // store this for use later
		String date = sdf.format(currentDate);
		//System.out.println(date);
		
		// Build the complete URL to query using the current date and specified numPhases
		String url = "http://api.usno.navy.mil/moon/phase?date=" + date + "&nump=" + numPhases;
		URL USNOURL = new URL(url);
		HttpURLConnection con = (HttpURLConnection) USNOURL.openConnection();
		
		// Make sure the HTTP request type is GET
		con.setRequestMethod("GET");
		con.setConnectTimeout(10000);
		con.setReadTimeout(10000);
		
		// Created a BufferedReader to read the contents of the request
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine); 
        }

        // Make sure to close the connection
        in.close();
        
        //System.out.println(response.toString());
        
        String jsonString = response.toString();
        
        // Google GSON functions to extract the moon phase string from the JSON
        JsonParser parser = new JsonParser();
        JsonObject webResponse = parser.parse(jsonString).getAsJsonObject();
        JsonArray phaseDataArray = webResponse.getAsJsonArray("phasedata");
        
        JsonObject phaseObject = (JsonObject) phaseDataArray.get(0);
        String phaseResult = phaseObject.get("phase").getAsString();
        String phaseDate = phaseObject.get("date").getAsString();
        
        // Use this to turn the date string into a Date object for comparison, below
        DateFormat format = new SimpleDateFormat("yyyy MMMM d");
        
        // Logic to return the moon phase for the current day, since the API gives
        // the moon phases only after the specified date
        try {
			Date phaseDateObj = format.parse(phaseDate);
			if (currentDate.before(phaseDateObj))
			{
				return moonMap.get(phaseResult);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(phaseResult);
        
        return phaseResult;
	}
}

package moonBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class that connects to the U.S. Naval Observatory's API to get the moon phase.
 * The API is described here: http://aa.usno.navy.mil/data/docs/api.php#phase
 * 
 * @author Tasso Sales-Filho
 *
 */
public class USNO
{
	public static void main(String[] args) throws IOException
	{
		USNO.getMoonPhase(1);
	}
	
	/**
	 * Get the moon phase from the website and return it as a string that can be
	 * written in Discord. The API requires requests to be sent in this format:
	 * 		http://api.usno.navy.mil/moon/phase?date=DATE&nump=NUMP
	 * where DATE is the date in "XX/YY/ZZZZ" format
	 * and NUMP is the number of phases from DATE to be returned.
	 */
	public static String getMoonPhase(int numPhases) throws IOException
	{
		// Get the current date in month/day/year format, since that's what the site needs
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
		String date = sdf.format(new Date());
		System.out.println(date);
		
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
        //JsonArray array = parser.parse(jsonString).getAsJsonArray();
        JsonObject webResponse = parser.parse(jsonString).getAsJsonObject();
        JsonArray phaseDataArray = webResponse.getAsJsonArray("phasedata");
        
        JsonObject phaseObject = (JsonObject) phaseDataArray.get(0);
        String phaseResult = phaseObject.get("phase").getAsString();
        
        return phaseResult;
        
        //System.out.println(phaseResult);
		
		//return null;
	}
}

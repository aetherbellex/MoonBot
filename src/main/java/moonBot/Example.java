package moonBot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

// Example for getting started with the Discord4J API from https://github.com/austinv11/Discord4J
public class Example 
{
	private static String myToken = "MjM4MzYxMzc3MjA5NTgxNTY5.C_YswQ.zPJ-Rn3eJg_IMigTeA068HzWP20";
	
	// Returns a new instance of the Discord client
	public static IDiscordClient createClient(String token, boolean login) 
	{
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(token); // Adds the login info to the builder
        try {
            if (login) {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            } else {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        } catch (DiscordException e) { // This is thrown if there was a problem building the client
            e.printStackTrace();
            return null;
        }
    }
	
	public static void main(String[] args)
	{
		IDiscordClient myClient = Example.createClient(myToken, true);
	}
}

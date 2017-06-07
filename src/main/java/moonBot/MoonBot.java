package moonBot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * A simple bot for the chat program Discord that will tell you the moon phase for today, 
 * to the nearest quarter phase. Uses data from the U.S. Naval Observatory.
 * 
 * @author Tasso Sales-Filho
 *
 */
public class MoonBot implements IListener<MessageReceivedEvent>
{
	public static MoonBot INSTANCE;
	public IDiscordClient client;
	
	// This maps the string moon phases to an image of that moon phase
	private static HashMap<String, String> moonPicMap;

	
	// Main method will log in
	public static void main(String[] args)
	{
		if (args.length < 1) // Needs a bot token provided
			throw new IllegalArgumentException("This bot needs at least 1 argument!");

		INSTANCE = login(args[0]); // Creates the bot instance and logs it in.
	}
	
	/**
	 * Construct the bot, associate a Discord client with it, and register
	 * an EventDispatcher so it can react to things.
	 * 
	 * @param client		Discord client
	 */
	public MoonBot(IDiscordClient client)
	{
		this.client = client;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(this);
		
		moonPicMap = new HashMap<String, String>();
		moonPicMap.put("New Moon", "C:\\eclipse\\workspace\\moonBot\\src\\main\\resources\\newMoon.png");
		moonPicMap.put("First Quarter", "C:\\eclipse\\workspace\\moonBot\\src\\main\\resources\\firstQuarter.png");
		moonPicMap.put("Full Moon", "C:\\eclipse\\workspace\\moonBot\\src\\main\\resources\\fullMoon.png");
		moonPicMap.put("Last Quarter", "C:\\eclipse\\workspace\\moonBot\\src\\main\\resources\\lastQuarter.png");
	}
	
	/**
	 * Log in to Discord so the bot can be used.
	 * 
	 * @param token			String token associated with the bot, from Discord's site
	 * @return				The bot
	 */
	public static MoonBot login(String token)
	{
		MoonBot bot = null;
		
		ClientBuilder builder = new ClientBuilder();
		builder.withToken(token);
		try {
			IDiscordClient client = builder.login();
			bot = new MoonBot(client);
		} catch (DiscordException e) {
			System.err.println("Error occurred while logging in");
			e.printStackTrace();
		}
		
		return bot;
	}
	
	/**
	 * Called when the client receives a message in a channel. If the message matches
	 * something the bot is expecting, then the bot can act on it.
	 */
	@Override
	public void handle(MessageReceivedEvent event)
	{
		// Get message from the event object- not the content
		IMessage message = event.getMessage();
		
		// Channel from which message was sent
		IChannel channel = message.getChannel();
		
		// Text content of the message
		String content = message.getContent();
		
		// If more commands are needed, there should be a better way to parse through
		// the message contents here instead of this equals check
		if (content.equals("!moon"))
		{
			try {
				String moonPhaseReturn = USNO.getMoonPhase(1);
				String output = "The closest quarter moon phase is: " + moonPhaseReturn;
				File moonPic = new File(moonPicMap.get(moonPhaseReturn));
				new MessageBuilder(this.client).withChannel(channel).withContent(output).withFile(moonPic).build();
			} catch (IOException e) {
				// IOException from the USNO class in case connection fails
				e.printStackTrace();
			} catch (RateLimitException e) {
				System.err.print("Sending messages too quickly!");
				e.printStackTrace();
			} catch (DiscordException e) {
				// Many possibilities, so use getErrorMessage
				System.err.print(e.getErrorMessage());
				e.printStackTrace();
			} catch (MissingPermissionsException e) {
				System.err.print("Missing permissions for channel!");
				e.printStackTrace();
			}
		}
	}
}

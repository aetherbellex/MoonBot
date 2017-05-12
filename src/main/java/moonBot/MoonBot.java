package moonBot;

import java.io.IOException;

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

public class MoonBot implements IListener<MessageReceivedEvent>
{
	public static MoonBot INSTANCE;
	public IDiscordClient client;
	
	// Main method will log in
	public static void main(String[] args)
	{
		if (args.length < 1) // Needs a bot token provided
			throw new IllegalArgumentException("This bot needs at least 1 argument!");

		INSTANCE = login(args[0]); // Creates the bot instance and logs it in.
	}
	
	public MoonBot(IDiscordClient client)
	{
		this.client = client;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(this);
	}
	
	public static MoonBot login(String token)
	{
		MoonBot bot = null; // Initialize bot variable
		
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
	 * Called when the client receives a message in a channel
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
				String output = "The moon phase for today is: " + moonPhaseReturn;
				new MessageBuilder(this.client).withChannel(channel).withContent(output).build();
			} catch (IOException e) {
				// IOException from the USNO class in case connection fails
				e.printStackTrace();
			} catch (RateLimitException e) {
				System.err.print("Sending messages too quickly!");
				e.printStackTrace();
			} catch (DiscordException e) {
				// Many possibilities, so use getErrorMessage
				System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
				e.printStackTrace();
			} catch (MissingPermissionsException e) {
				System.err.print("Missing permissions for channel!");
				e.printStackTrace();
			}
		}
		
		/*try {
			new MessageBuilder(this.client).withChannel(channel)
			.withContent(message.getContent()).build();
		} catch (RateLimitException e) {
			System.err.print("Sending messages too quickly!");
			e.printStackTrace();
		} catch (DiscordException e) {
			// Many possibilities, so use getErrorMessage
			System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
			e.printStackTrace();
		} catch (MissingPermissionsException e) {
			System.err.print("Missing permissions for channel!");
			e.printStackTrace();
		}*/
	}
}

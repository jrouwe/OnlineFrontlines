package onlinefrontlines.utils;

import java.util.concurrent.LinkedBlockingQueue; 
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.Logger;

/**
 * Class that sends out e-mails at end of turn
 * 
 * @author jorrit
 * 
 * Copyright (C) 2009-2013 Jorrit Rouwe
 * 
 * This file is part of Online Frontlines.
 *
 * Online Frontlines is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Online Frontlines is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Online Frontlines.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Mailer extends Thread 
{
	private static final Logger log = Logger.getLogger(Mailer.class);

	/**
	 * Singleton
	 */
	private static Mailer instance = new Mailer();
	
	/**
	 * Access to the singleton
	 */
	public static Mailer getInstance()
	{
		return instance;
	}
	
	/**
	 * Mail structure
	 */
	public static class Mail
	{
		/**
		 * Sender of the mail
		 */
		public InternetAddress sender;

		/**
		 * True if the mail should reveal the email address of the sender
		 */
		public boolean revealSenderEmail = false;
		
		/**
		 * Recipient of the mail
		 */
		public InternetAddress recipient;
		
		/**
		 * JSP file to use as a template (relative path to application)
		 */
		public String templateJsp;
		
		/**
		 * Title of the e-mail
		 */
		public String title;
		
		/**
		 * Map containing the name value pairs to pass to the jsp page as parameters
		 */
		public Map<String, String> params = new HashMap<String, String>();
	}
	
	/**
	 * Queue that messages are added to
	 */
	private LinkedBlockingQueue<Mail> queue = new LinkedBlockingQueue<Mail>();

	/**
	 * Constructor
	 */
	public Mailer()
	{
		super("Mailer");
	}
	
	/**
	 * Send a mail
	 */
	public void send(Mail mail)
	{
		queue.offer(mail);
		
		if (queue.size() > 10)
			log.warn(queue.size() + " messages waiting in the mail queue");
	}
	
	/**
	 * Run the thread
	 */
	public void run()
	{
		log.info("Thread started");
		
		for (;;)
		{			
			try
			{
				// Take a mail
				Mail mail = queue.take();
				
				// Send it
				sendInternal(mail);
			}
			catch (InterruptedException e)
			{
				// Normal termination
				break;
			}
			catch (Exception e)
			{			
				// Log error, retry
				Tools.logException(e);
			}		
		}

		log.info("Thread stopped");
	}
	
	/**
	 * Send email using a JSP file as mail template
	 */
	private void sendInternal(Mail mail) throws Exception
	{
		String fullTitle = "[Online Frontlines] " + mail.title;
		
    	// Create url to page that gets the mail content
    	String url = GlobalProperties.getInstance().getString("mailer.url") + "/" + mail.templateJsp
    		+ "?title=" + Tools.encodeGetParameter(fullTitle)
    		+ "&senderName=" + Tools.encodeGetParameter(mail.sender.getPersonal() != null? mail.sender.getPersonal() : mail.sender.getAddress())
    		+ "&recipientName=" + Tools.encodeGetParameter(mail.recipient.getPersonal() != null? mail.recipient.getPersonal() : mail.recipient.getAddress());
    	for (Map.Entry<String, String> e : mail.params.entrySet())
    		url += "&" + e.getKey() + "=" + Tools.encodeGetParameter(e.getValue());
    	
    	// Send message
	    Session session = Session.getInstance(GlobalProperties.getInstance().getProperties(), new SMTPAuthenticator());
	    Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(GlobalProperties.getInstance().getString("mail.smtp.user"), mail.revealSenderEmail? mail.sender.getPersonal() : mail.sender.getPersonal() + " [DO NOT REPLY]", "UTF-8"));
	    msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { mail.recipient });
	    if (mail.revealSenderEmail)
	    	msg.setReplyTo(new InternetAddress[] { mail.sender });
    	msg.setSubject(fullTitle);
	    msg.setSentDate(Calendar.getInstance().getTime());
	    msg.setHeader("X-Mailer", "Online Frontlines Mailer");
		msg.setDataHandler(new DataHandler(new URL(url)));
	    Transport.send(msg);
	}

	/**
	 * Authenticator for mail session
	 */
	private class SMTPAuthenticator extends Authenticator 
	{
		public PasswordAuthentication getPasswordAuthentication() 
		{
			String username = GlobalProperties.getInstance().getString("mail.smtp.auth.username");
			String password = GlobalProperties.getInstance().getString("mail.smtp.auth.password");
			return new PasswordAuthentication(username, password);
		}
	}
}

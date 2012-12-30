package onlinefrontlines.feedback;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import onlinefrontlines.utils.CacheException;
import onlinefrontlines.auth.UserCache;

/**
 * This class contains feedback information from one user to another on a game
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
public class Feedback 
{
	/**
	 * Id of the report
	 */
	public int id;
	
	/**
	 * User id of the reporter
	 */
	public int reporterUserId;
	
	/**
	 * User id of opponent user was playing against
	 */
	public int opponentUserId;
	
	/**
	 * Game id
	 */
	public int gameId;
	
	/**
	 * Score
	 */
	public int score;
	
	/**
	 * User comments
	 */
	public String comments;
	
	/**
	 * Reply from opponent
	 */
	public String reply;
	
	/**
	 * Date the feedback was given
	 */
	public long creationDate = Calendar.getInstance().getTime().getTime();
	
	/**
	 * Id of the report
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * User id of the reporter
	 */
	public int getReporterUserId()
	{
		return reporterUserId;
	}
	
	/**
	 * User id of opponent user was playing against
	 */
	public int getOpponentUserId()
	{
		return opponentUserId;
	}
	
	/**
	 * Game id
	 */
	public int getGameId()
	{
		return gameId;
	}
	
	/**
	 * Score
	 */
	public int getScore()
	{
		return score;
	}
	
	/**
	 * User comments
	 */
	public String getComments()
	{
		return comments;
	}
	
	/**
	 * Reply from opponent
	 */
	public String getReply()
	{
		return reply;
	}
	
	/**
	 * Translate user id into user name
	 */
	public String getReporterUsername()
	{
		try
		{
			return UserCache.getInstance().get(reporterUserId).username;
		}
		catch (CacheException e)
		{
			return "<unknown>";
		}
	}

	/**
	 * Translate user id into user name
	 */
	public String getOpponentUsername()
	{
		try
		{
			return UserCache.getInstance().get(opponentUserId).username;
		}
		catch (CacheException e)
		{
			return "<unknown>";
		}
	}

	/**
	 * Get creation time as a string
	 */
	public String getCreationDateString()
	{
		return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(new Date(creationDate)); 
	}
}

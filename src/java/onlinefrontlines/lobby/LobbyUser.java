package onlinefrontlines.lobby;

import java.util.Calendar;
import java.sql.SQLException;
import onlinefrontlines.Constants;
import onlinefrontlines.Army;
import onlinefrontlines.auth.User;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.feedback.FeedbackDAO;
import onlinefrontlines.userstats.*;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.utils.CacheException;

/**
 * A user that is active / has conquered a country in a lobby
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
public class LobbyUser 
{
	/**
	 * Lobby we belong to
	 */
	public LobbyState lobbyState;
	
	/**
	 * User id
	 */
	public final int userId;
	
	/**
	 * Cached user name
	 */
	public final String cachedUsername;
	
	/**
	 * Army of user (can change for users that have not selected their army yet)
	 */
	public Army army = Army.none;
	
	/**
	 * If user is still connected
	 */
	private boolean isConnected = false;
	
	/**
	 * Last time communication was received (to time out user and kick him from the lobby)
	 */
	private long lastCommunicationTime;
	
	/**
	 * Country that the user is defending
	 */
	private Country defendedCountry;
	
	/**
	 * Country that the user is attacking
	 */
	private Country attackedCountry;
	
	/**
	 * Indicates when the user is linked to an attacked / defended country 
	 * if he still needs to accept the challenge or not 
	 */
	public boolean hasAcceptedChallenge;
	
	/**
	 * If current time passes this time (in millis) the challenge is no longer valid and the user should be removed
	 * from the country 
	 */
	public long challengeValidUntil;
	
	/**
	 * Cached rank
	 */
	public int rank = 0;
	
	/**
	 * Cached level
	 */
	public int level = 0;
	
	/**
	 * Cached leaderboard position
	 */
	public int leaderboardPosition = 0;
	
	/**
	 * Cached feedback score
	 */
	public int feedbackScore = 0;
	
	/**
	 * Number of games won
	 */
	public int gamesWon = 0;
	
	/**
	 * Number of games lost
	 */
	public int gamesLost = 0;
	
	/**
	 * Cached from user
	 */
	public boolean autoDefendOwnedCountry;
	public boolean autoDeclineFriendlyDefender;

	/**
	 * Counter that indicates when this user was last changed
	 */
	public int changeCount = 0;
	
	/**
	 * Constructor
	 */
	public LobbyUser(LobbyState lobbyState, User user)
	{
		this.lobbyState = lobbyState;
		this.userId = user.id;
		this.cachedUsername = user.username;

		try
		{
			updateUserInfoInternal();
		}
		catch (CacheException e)
		{
			Tools.logException(e);
		}
		catch (SQLException e)
		{
			Tools.logException(e);
		}
	}
	
	/**
	 * Get user
	 */
	public User getUser() throws CacheException
	{
		return UserCache.getInstance().get(userId);
	}
	
	/**
	 * Update user info without writing changes to the db
	 * 
	 * @return True if user info changed
	 */
	private boolean updateUserInfoInternal() throws CacheException, SQLException
	{
		boolean modified = false;
		
		// Cache user
		User user = getUser();
		
		// Determine army
		if (user.army != Army.none && army != user.army)
		{
			// Update army
			army = user.army;			
			modified = true;
			
			// Cancel pending actions
			setCountries(null, null);
		}

		// Determine feedback score
		int tempFeedbackScore = FeedbackDAO.getScore(userId);
		if (feedbackScore != tempFeedbackScore)
		{
			feedbackScore = tempFeedbackScore;
			modified = true;
		}
		
		// Determine if user auto defends
		boolean autodef = user.autoDefendOwnedCountry;
		if (autoDefendOwnedCountry != autodef)
		{
			autoDefendOwnedCountry = autodef;
			modified = true;
		}
		
		// Determine if user auto declines friendly matches
		boolean autodecline = user.autoDeclineFriendlyDefender;
		if (autoDeclineFriendlyDefender != autodecline)
		{
			autoDeclineFriendlyDefender = autodecline;
			modified = true;
		}

		// Determine stats
		UserStats stats = UserStatsCache.getInstance().get(userId);
		if (stats != null)
		{
			if (gamesWon != stats.gamesWon)
			{
				gamesWon = stats.gamesWon;
				modified = true;
			}
			
			if (gamesLost != stats.gamesLost)
			{
				gamesLost = stats.gamesLost;
				modified = true;
			}
			
			int tempRank = UserRank.getRank(userId);
			if (rank != tempRank)
			{
				rank = tempRank;
				modified = true;
			}
			
			int tempLevel = UserRank.getLevel(userId);
			if (level != tempLevel)
			{
				level = tempLevel;
				modified = true;
			}
		}
		
		// Determine leaderboard position
		int tempLeaderboardPosition = 0;
		UserLeaderboard board = UserLeaderboardCache.getInstance().get(UserLeaderboardCache.BOARD_TOTAL_POINTS);
		UserLeaderboard.Entry entry = board.getEntry(userId);		
		if (entry != null)
			tempLeaderboardPosition = entry.position;
		if (leaderboardPosition != tempLeaderboardPosition)
		{
			leaderboardPosition = tempLeaderboardPosition;
			modified = true;
		}
		
		return modified;
	}
	
	/**
	 * Update user information from the database
	 */
	public void updateUserInfo() throws SQLException, CacheException
	{
		// Call internal function
		boolean modified = updateUserInfoInternal();

		// Notify any change
		if (modified)
			lobbyState.notifyLobbyUserChanged(this);
	}
	
	/**
	 * Check if a user is still connected
	 */
	public boolean getIsConnected()
	{
		return isConnected;
	}
	
	/**
	 * Called when communication is received from the user
	 */
	public void markConnected()
	{
		lastCommunicationTime = Calendar.getInstance().getTime().getTime();
		if (!isConnected)
		{
			isConnected = true;
			lobbyState.notifyLobbyUserChanged(this);
		}
	}

	/**
	 * Check if user connection timed out
	 */
	public void checkConnectedTimeOut()
	{
		// Check timeout
		if (isConnected
			&& Calendar.getInstance().getTime().getTime() - lastCommunicationTime > Constants.CLIENT_POLL_TIMEOUT)
		{
			// No longer connected
			isConnected = false;
			lobbyState.notifyLobbyUserChanged(this);
		}
	}

	/**
	 * Check if user can be removed from the list of users
	 */
	public boolean canBeRemoved()
	{
		return !isConnected
			&& Calendar.getInstance().getTime().getTime() - lastCommunicationTime > 3 * Constants.CLIENT_POLL_TIMEOUT
			&& defendedCountry == null 
			&& attackedCountry == null
			&& !lobbyState.isUserOnMap(userId);
	}
	
	/**
	 * Set country to defend and attack
	 */
	public void setCountries(Country defendedCountry, Country attackedCountry)
	{
		// Check NOP
		if (defendedCountry == this.defendedCountry
			&& attackedCountry == this.attackedCountry)
			return;
		
		// Unlink from countries
		if (this.defendedCountry != null)
		{
			assert(this.defendedCountry.defender == this);
			this.defendedCountry.defender = null;
		}
		if (this.attackedCountry != null)
		{
			assert(this.attackedCountry.attacker == this);
			this.attackedCountry.attacker = null;
		}
		
		// Link to new countries
		if (defendedCountry != null)
		{
			assert(defendedCountry.army == army);
			assert(defendedCountry.defender == null);
			defendedCountry.defender = this;
		}
		if (attackedCountry != null)
		{
			assert(attackedCountry.army != army);
			assert(attackedCountry.attacker == null);
			attackedCountry.attacker = this;
		}
	
		// Set pointers
		this.defendedCountry = defendedCountry;
		this.attackedCountry = attackedCountry;

		// Reset other flags
		if (defendedCountry == null || attackedCountry == null)
		{
			hasAcceptedChallenge = false;
			challengeValidUntil = 0;
		}
		
		// Flag update
		lobbyState.invalidateDefendableCountries();
	}

	/**
	 * Access to the currently defended country
	 */
	public Country getDefendedCountry()
	{
		return defendedCountry;
	}

	/**
	 * Access to the currently attacked country
	 */
	public Country getAttackedCountry()
	{
		return attackedCountry;
	}

	/**
	 * Get location of challenged country
	 */
	public int getDefendedCountryX()
	{
		return defendedCountry != null? defendedCountry.locationX : -1;
	}

	public int getDefendedCountryY()
	{
		return defendedCountry != null? defendedCountry.locationY : -1;
	}

	/**
	 * Get location of challenged country
	 */
	public int getAttackedCountryX()
	{
		return attackedCountry != null? attackedCountry.locationX : -1;
	}

	public int getAttackedCountryY()
	{
		return attackedCountry != null? attackedCountry.locationY : -1;
	}

	/**
	 * Get state string for communication with Flash application
	 */
	@Override
	public String toString()
	{
		return userId + "," + Tools.flashEscape(cachedUsername) + "," + Army.toInt(army) + "," + (isConnected? 1 : 0) + "," + getDefendedCountryX() + "," + getDefendedCountryY() + "," + getAttackedCountryX() + "," + getAttackedCountryY() + "," + (hasAcceptedChallenge? 1 : 0) + "," + rank + "," + level + "," + leaderboardPosition + "," + feedbackScore + "," + gamesWon + "," + gamesLost + "," + (autoDefendOwnedCountry? 1 : 0) + "," + (autoDeclineFriendlyDefender? 1 : 0);
	}
}

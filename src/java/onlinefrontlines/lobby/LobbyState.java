package onlinefrontlines.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Calendar;
import java.util.Collection;
import java.sql.SQLException;
import java.awt.Point;
import org.apache.log4j.Logger;
import onlinefrontlines.Army;
import onlinefrontlines.Constants;
import onlinefrontlines.auth.User;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.game.CountryConfig;
import onlinefrontlines.game.CountryConfigCache;
import onlinefrontlines.game.Faction;
import onlinefrontlines.game.GameStateDAO;
import onlinefrontlines.lobby.actions.*;
import onlinefrontlines.userstats.UserStatsDAO;
import onlinefrontlines.utils.HexagonGrid;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.utils.CacheException;

/**
 * Runtime state of a lobby
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
public class LobbyState 
{
	private static final Logger log = Logger.getLogger(LobbyState.class);

	/**
	 * Max amount of messages to keep
	 */
	public static final int MAX_MESSAGE_COUNT = 100;
	
	/**
	 * Lobby configuration
	 */
	public final LobbyConfig lobbyConfig;
	
	/**
	 * All countries in this lobby
	 */
	private Country[][] countries;
	
	/**
	 * Last time one of the countries changed
	 */
	private int countriesChangeCount = 0;
	
	/**
	 * All active and timed out users
	 */
	private HashMap<Integer, LobbyUser> users = new HashMap<Integer, LobbyUser>();
	
	/**
	 * Last time one of the users changed
	 */
	private int usersChangeCount = 0;
	
	/**
	 * All text messages
	 */
	private ArrayList<TextMessage> textMessages = new ArrayList<TextMessage>();
	
	/**
	 * Last time one of the messages changed
	 */
	private int textMessageChangeCount = 0;
	
	/**
	 * Counter that is increased everytime something in lobby changes, 
	 * this is used to determine which data to send to each client
	 */
	private int currentChangeCount = 0;
	
	/**
	 * Time at which all countries were conquered by one faction
	 */
	public long allConqueredTime = Long.MAX_VALUE;
	
	/**
	 * Tiles that can be defended by a particular army
	 * @see determineDefendableCountries
	 */
	private static class DefendableCountries
	{
		public int distanceToEnemy[][];
		public int requiredDistance;
	}
	
	/**
	 * Defendable countries per army
	 */
	DefendableCountries defendableCountries[] = { null, null };
	
	/**
	 * Time at which this lobby was created
	 */
	public final long creationTime = Calendar.getInstance().getTime().getTime();
	
	/**
	 * Random generator
	 */
	public final Random random = new Random();
	
	/**
	 * Constructor
	 */
	public LobbyState(LobbyConfig lobbyConfig) throws SQLException, CacheException
	{
		// Store lobby config
		this.lobbyConfig = lobbyConfig;
		
		// Create countries array
		countries = new Country[lobbyConfig.sizeX][lobbyConfig.sizeY];
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				int id = lobbyConfig.getCountryConfigId(x, y);
				if (id > 0)
				{
			    	CountryConfig countryConfig = CountryConfigCache.getInstance().get(id);
					countries[x][y] = new Country(x, y, countryConfig, Army.none);
				}
			}

		// Loop through current state of countries
		for (LobbyCountryStateDAO.State state : LobbyCountryStateDAO.getCountryStates(lobbyConfig.id))			
		{
			Country country = getCountry(state.locationX, state.locationY);
			if (country != null)
			{
				// Copy state
				country.ownerUserId = state.ownerUserId;
				country.ownerExclusiveTime = state.ownerExclusiveTime;
				country.army = state.army;
			}
			else
			{
				// Country no longer exists, remove it from the database
				LobbyCountryStateDAO.removeCountryState(lobbyConfig.id, state.locationX, state.locationY);
				
				// Create warning
				log.warn("Could not restore country state at " + state.locationX + "," + state.locationY + " for lobby " + lobbyConfig.id);
			}
		}

		// Countries that are not in the database yet get a random initial faction and are added to the database
		ArrayList<Country> countriesToCreate = new ArrayList<Country>();
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				Country country = countries[x][y];
				if (country != null && country.army == Army.none)
				{
					country.army = random.nextBoolean()? Army.red : Army.blue;
					countriesToCreate.add(country);
				}
			}
		if (!countriesToCreate.isEmpty())
			LobbyCountryStateDAO.createCountryStates(lobbyConfig.id, countriesToCreate);
		
		// Reload active games
		for (GameStateDAO.GameForLobby game : GameStateDAO.getGamesForLobby(lobbyConfig.id))
		{
			Country attackedCountry = getCountry(game.attackedCountryX, game.attackedCountryY);
			Country defendedCountry = getCountry(game.defendedCountryX, game.defendedCountryY);
			if (attackedCountry != null && defendedCountry != null)
			{
				attackedCountry.currentGameId = game.gameId;
				attackedCountry.currentGameUserId = game.attackerUserId;
				defendedCountry.currentGameId = game.gameId;
				defendedCountry.currentGameUserId = game.defenderUserId;
			}
			else
			{
				// Create warning
				log.warn("Could not restore game in progress " + game.gameId + " for lobby " + lobbyConfig.id);
			}
		}

		// Reload users
		for (LobbyUserDAO.State state : LobbyUserDAO.getUsers(lobbyConfig.id))
		{
			User user = UserCache.getInstance().get(state.userId);

			LobbyUser lobbyUser = new LobbyUser(this, user);
			lobbyUser.army = state.army;
			
			Country defendedCountry = getCountry(state.defendedCountryX, state.defendedCountryY);
			Country attackedCountry = getCountry(state.attackedCountryX, state.attackedCountryY);
			
			if (defendedCountry != null 
				&& attackedCountry != null 
				&& defendedCountry.currentGameId == 0 
				&& attackedCountry.currentGameId == 0)
			{
				lobbyUser.setCountries(defendedCountry, attackedCountry);
				lobbyUser.hasAcceptedChallenge = state.hasAcceptedChallenge;
				lobbyUser.challengeValidUntil = state.challengeValidUntil;
			}
			
			users.put(lobbyUser.userId, lobbyUser);
		}
		
		// Make sure that no users are missed because the lobby_users table was deleted
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				Country country = countries[x][y];
				if (country != null)
				{
					ensureUserCreated(country.currentGameUserId, country.army);
					ensureUserCreated(country.ownerUserId, country.army);
				}
			}
		
		// Update state of capture points
		checkCapturePoints();
		
		// Check if all countries have been conquered
		checkAllConquered();
		
		// Reload messages
		textMessages = LobbyChatDAO.getMessages(lobbyConfig.id, MAX_MESSAGE_COUNT);
		
		// Make sure clients have a change count other than 0 to sync to
		++currentChangeCount;

		// Make sure challenges that should have been deleted are deleted
		checkConnectedUsersTimeOut();
		
		log.info("Created lobby " + lobbyConfig.id);		
	}
	
	/**
	 * Helper function to make sure a user is created
	 * 
	 * @param userId User id of user to create
	 * @param defaultUserArmy If user has no army, this army will be used
	 */
	private void ensureUserCreated(int userId, Army defaultUserArmy) throws SQLException, CacheException
	{
		if (userId != 0 && getLobbyUser(userId) == null)
		{
			User user = UserCache.getInstance().get(userId);
			
			LobbyUser lobbyUser = new LobbyUser(this, user);
			if (lobbyUser.army == Army.none)
				lobbyUser.army = defaultUserArmy;
			users.put(lobbyUser.userId, lobbyUser);
			LobbyUserDAO.createOrUpdateUser(lobbyConfig.id, lobbyUser);
			
			log.warn("Lobby user '" + userId + "' did not exist but is in use");
		}
	}
	
	/**
	 * Get lobby id
	 */
	public int getLobbyId()
	{
		return lobbyConfig.id;
	}
	
	/**
	 * Get current change count
	 */
	public int getCurrentChangeCount()
	{
		return currentChangeCount;
	}
	
	/**
	 * Get a country
	 */
	public Country getCountry(int x, int y)
	{
		if (x >= 0 && y >= 0 && x < lobbyConfig.sizeX && y < lobbyConfig.sizeY)
			return countries[x][y];
		else
			return null;
	}
	
	/**
	 * Mark that a country has changed
	 */
	public void notifyCountryChanged(Country country)
	{
		country.changeCount = currentChangeCount;
		countriesChangeCount = currentChangeCount++;
		
		try
		{
			LobbyCountryStateDAO.updateCountryState(lobbyConfig.id, country);
		}
		catch (SQLException e)
		{
			Tools.logException(e);
		}
	}
	
	/**
	 * Get all users
	 */
	public Collection<LobbyUser> getUsers()
	{
		return users.values();
	}
	
	/**
	 * Get lobby user
	 */
	public LobbyUser getLobbyUser(int userId) throws SQLException
	{
		return users.get(userId);
	}
	
	/**
	 * Get or create lobby user
	 */
	public LobbyUser getOrCreateLobbyUser(User user)
	{
		// Check if already in the lobby
		LobbyUser lobbyUser = users.get(user.id);		
		if (lobbyUser != null && lobbyUser.getIsConnected())
			return lobbyUser;
		
		// Check if there is room for another user
		int userCount = 0;
		for (LobbyUser u : users.values())
			if (u.getIsConnected())
				++userCount;
		if (userCount >= lobbyConfig.maxUsers)
			return null;
		
		// Disconnected user found, return it
		if (lobbyUser != null)
			return lobbyUser;
		
		// Add user to the lobby
		lobbyUser = new LobbyUser(this, user);
		if (user.army != Army.none)
		{
			lobbyUser.army = user.army;
		}
		else
		{
			int count[] = getLobbyUserCount();
			int count_red = count[Army.toInt(Army.red)];
			int count_blue = count[Army.toInt(Army.blue)];
			if (count_red == count_blue)
				lobbyUser.army = random.nextBoolean()? Army.red : Army.blue;
			else if (count_red < count_blue)
				lobbyUser.army = Army.red;
			else
				lobbyUser.army = Army.blue;
		} 
		users.put(lobbyUser.userId, lobbyUser);
		notifyLobbyUserChanged(lobbyUser);
		
		return lobbyUser;
	}
	
	/**
	 * Mark one of the lobby users changed
	 */
	public void notifyLobbyUserChanged(LobbyUser lobbyUser)
	{
		// Update change count
		lobbyUser.changeCount = currentChangeCount;
		usersChangeCount = currentChangeCount++;
		
		// Write new state to database
		try
		{
			LobbyUserDAO.createOrUpdateUser(lobbyConfig.id, lobbyUser);
		}
		catch (SQLException e)
		{
			Tools.logException(e);
		}
	}
	
	/**
	 * Look for users that timed out
	 */
	public void checkConnectedUsersTimeOut() throws SQLException
	{
		long time = Calendar.getInstance().getTime().getTime();
		
		ArrayList<LobbyUser> toBeRemoved = new ArrayList<LobbyUser>();
		
		for (LobbyUser u : users.values())
		{
			// Check if user still connected
			u.checkConnectedTimeOut();
			
			// Check if users challenge timed out
			if (!u.hasAcceptedChallenge
				&& u.challengeValidUntil != 0 
				&& u.challengeValidUntil < time)
			{
				try
				{
					// Cancel the challenge
					Action cancel = new ActionCancel();
					cancel.setLobbyState(this);
					cancel.setLobbyUser(u);
					cancel.doAction();
				}
				catch (Exception e)
				{
					Tools.logException(e);
				}
			}
			
			// Check if user can be deleted
			if (u.canBeRemoved())
				toBeRemoved.add(u);
		}
		
		// Remove users
		for (LobbyUser u : toBeRemoved)
		{
			// Remove from user list
			users.remove(u.userId);
			
			// Remove from database
			LobbyUserDAO.removeUser(lobbyConfig.id, u.userId);
		}
	}
	
	/**
	 * Get amount of users logged in of a particular army
	 */
	public int[] getLobbyUserCount()
	{
		int count[] = { 0, 0 };
		
		for (LobbyUser u : users.values())
			if (u.getIsConnected())
				count[Army.toInt(u.army)]++;
		
		return count;
	}
	
	/**
	 * Check if user is playing a game
	 * 
	 * @param userId Id of user
	 */
	public boolean isUserInGame(int userId)
	{
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				Country country = countries[x][y];
				if (country != null 
					&& country.currentGameUserId == userId)
					return true;
			}
		
		return false;
	}

	/**
	 * Check if user is playing a game or has conquered a country on the map
	 * 
	 * @param userId Id of user
	 */
	public boolean isUserOnMap(int userId)
	{
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				Country country = countries[x][y];
				if (country != null 
					&& (country.currentGameUserId == userId 
						|| country.ownerUserId == userId))
					return true;
			}
		
		return false;
	}
	
	/**
	 * Get a list of changed countries
	 * 
	 * @param changeCount Point after which to get changes
	 */
	public ArrayList<Country> getChangedCountries(int changeCount)
	{
		ArrayList<Country> list = new ArrayList<Country>();
		
		// Find changes
		if (changeCount - countriesChangeCount <= 0)
			for (int y = 0; y < lobbyConfig.sizeY; ++y)
				for (int x = 0; x < lobbyConfig.sizeX; ++x)
				{
					Country c = countries[x][y];
					if (c != null && changeCount - c.changeCount <= 0)
						list.add(c);
				}
		
		return list;
	}
	
	/**
	 * Get a list of changed users
	 * 
	 * @param changeCount Point after which to get changes
	 */
	public ArrayList<LobbyUser> getChangedUsers(int changeCount)
	{
		ArrayList<LobbyUser> list = new ArrayList<LobbyUser>();

		// Find changes
		if (changeCount - usersChangeCount <= 0)
		{
			for (LobbyUser u : users.values())
				if (changeCount - u.changeCount <= 0)
					list.add(u);
		}
		
		return list;
	}
	
	/**
	 * Add new text message
	 * 
	 * @param message Message to add
	 */
	public void addTextMessage(TextMessage message) throws SQLException
	{		
		LobbyChatDAO.addMessage(lobbyConfig.id, message);

		message.changeCount = currentChangeCount;
		textMessages.add(message);
		if (textMessages.size() > MAX_MESSAGE_COUNT)
			textMessages.remove(0);
		textMessageChangeCount = currentChangeCount++;		
	}
	
	/**
	 * Get changed text messages
	 * 
	 * @param changeCount Point after which to get changes
	 */
	public ArrayList<TextMessage> getChangedTextMessages(int changeCount)
	{
		ArrayList<TextMessage> list = new ArrayList<TextMessage>();

		// Find changes
		if (changeCount - textMessageChangeCount <= 0)
		{
			for (TextMessage m : textMessages)
				if (changeCount - m.changeCount <= 0)
					list.add(m);
		}
		
		return list;
	}
	
	/**
	 * Called by the game end thread to notify that a game has ended
	 */
	public void notifyGameEnd(int attackerUserId, int defenderUserId, int attackedCountryX, int attackedCountryY, int defendedCountryX, int defendedCountryY, Faction winner) throws SQLException
	{
		// Get countries
		Country attackedCountry = getCountry(attackedCountryX, attackedCountryY);
		Country defendedCountry = getCountry(defendedCountryX, defendedCountryY);
		if (attackedCountry == null || defendedCountry == null)
		{
			log.error("notifyGameEnd failed because country could not be found!");
			return;
		}
		
		// Get attacker and defender
		LobbyUser attacker = getLobbyUser(attackerUserId);
		LobbyUser defender = getLobbyUser(defenderUserId);
		if (attacker == null || defender == null)
		{
			log.error("notifyGameEnd failed because lobby user could not be found!");
			return;
		}
		
		// End game in progress 
		attackedCountry.currentGameId = 0;
		attackedCountry.currentGameUserId = 0;
		defendedCountry.currentGameId = 0;
		defendedCountry.currentGameUserId = 0;

		// If friendly game, don't change colors
		if (attacker.army != defender.army)
		{
			// Country changes faction, winner stays in the country
			switch (winner)
			{
			case f1:
				attackedCountry.army = attacker.army;
				attackedCountry.ownerUserId = attacker.userId;
				attackedCountry.ownerExclusiveTime = Calendar.getInstance().getTime().getTime() + Constants.EXCLUSIVE_TIME_AFTER_CONQUERED;
				break;
				
			case f2:
				defendedCountry.army = defender.army;
				defendedCountry.ownerUserId = defender.userId;
				defendedCountry.ownerExclusiveTime = Calendar.getInstance().getTime().getTime() + Constants.EXCLUSIVE_TIME_AFTER_CONQUERED;
				break;
			}
		}
		
		// Notify of changes
		notifyCountryChanged(attackedCountry);
		notifyCountryChanged(defendedCountry);
		invalidateDefendableCountries();

		// Update state of capture points
		checkCapturePoints();
		
		// Check if all countries have been conquered
		checkAllConquered();
	}
	
	/**
	 * Check state of all capture points
	 */
	private void checkCapturePoints()
	{
		// Loop through all countries
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				// Check if it is a capture point
				Country c = countries[x][y];
				if (c != null && c.countryConfig.isCapturePoint)
				{
					// Check if all neighbouring countries are of opposite faction
					boolean allCountriesConquered = false;					
					ArrayList<Point> neighbours = lobbyConfig.getNeighbours(x, y);
					for (Point n : neighbours)
					{
						Country nc = countries[n.x][n.y];
						if (nc != null)
						{
							// There is at least one country neighbouring
							allCountriesConquered = true;
							
							if (nc.army == c.army)
							{
								// Country is of same army so not conquered yet
								allCountriesConquered = false;
								break;
							}
						}
					}
					
					// All countries are conquered
					if (allCountriesConquered)
					{
						// Change state
						c.army = Army.opposite(c.army);
						c.ownerUserId = 0;
						notifyCountryChanged(c);

						// Award capture to all neighbours
						for (Point n : neighbours)
						{
							Country nc = countries[n.x][n.y];
							if (nc != null && nc.ownerUserId != 0)
							{
								try
								{
									// Accumulate user stats
									UserStatsDAO.addCapture(nc.ownerUserId, lobbyConfig.id);
								}
								catch (SQLException e)
								{
									// Log error
									Tools.logException(e);
								}
							}
						}						
					}						
				}
			}		
	}
	
	/**
	 * Check if all countries have been conquered
	 */
	private void checkAllConquered()
	{
		int count[] = { 0, 0 };
		
		// Loop through all countries
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				Country c = countries[x][y];
				if (c != null && !c.countryConfig.isCapturePoint)
				{
					// Count ownership
					count[Army.toInt(c.army)]++;
				}
			}
				
		// Check if all countries have been conquered
		if (count[0] == 0 || count[1] == 0)
		{
			allConqueredTime = Calendar.getInstance().getTime().getTime();
		}
	}
		
	/**
	 * Randomize ownership of all countries
	 */
	public void randomizeAllCountries() throws SQLException
	{
		// Set all countries to random army
		Random r = new Random();
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{
				Country country = countries[x][y];
				if (country != null)
				{
					country.army = r.nextBoolean()? Army.red : Army.blue;
					notifyCountryChanged(country);
				}
			}	
		
		// Reset all conquered time
		allConqueredTime = Long.MAX_VALUE;
	}
	
	/**
	 * Helper class to determine defendable countries
	 */
	private static class ListEntry
	{
		int x;
		int y;
		int d;
		
		public ListEntry(int x, int y, int d)
		{
			this.x = x;
			this.y = y;
			this.d = d;
		}
	}
	
	/**
	 * Determine countries that are defendable
	 * 
	 * After this call all countries for which distanceToEnemy[x][y] == requiredDistance
	 * are defendable (if the country is owned by the correct army and there is no other 
	 * defender / attacker yet)
	 */
	private DefendableCountries determineDefendableCountries(Army army)
	{
		// Check if cached
		DefendableCountries dc = defendableCountries[Army.toInt(army)];
		if (dc != null)
			return dc;
		
		// Create new
		dc = new DefendableCountries();
		
		// Init array
		dc.distanceToEnemy = new int[lobbyConfig.sizeX][lobbyConfig.sizeY];
		dc.requiredDistance = 10000;
		
		// Populate list with all countries that are attackable
		ArrayList<ListEntry> list = new ArrayList<ListEntry>();			
		for (int y = 0; y < lobbyConfig.sizeY; ++y)
			for (int x = 0; x < lobbyConfig.sizeX; ++x)
			{ 
				Country c = countries[x][y];
				if (c != null 
					&& c.army != army
					&& !c.countryConfig.isCapturePoint
					&& c.currentGameId == 0
					&& c.attacker == null)
				{
					dc.distanceToEnemy[x][y] = 0;
					list.add(new ListEntry(x, y, 0));
				}
				else
				{
					dc.distanceToEnemy[x][y] = 10000;
				}
			}
		
		// Loop while there are still countries left
		while (list.size() > 0)
		{
			// Get first element from the list
			ListEntry e = list.get(0);
			int x = e.x;
			int y = e.y;
			int d = e.d;
			list.remove(0);
							
			// Check if other shorter path has been found
			if (dc.distanceToEnemy[x][y] < d)
				continue;
				
			// Check if this is the shortest path to an attackable country so far
			if (d < dc.requiredDistance)
			{
				Country c = countries[x][y];
				if (c.army == army
					&& !c.countryConfig.isCapturePoint
					&& c.currentGameId == 0
					&& c.defender == null)
					dc.requiredDistance = d;
			}				
				
			// Add all neighbours to the list to be searched
			d++;
			ArrayList<Point> neighbours = lobbyConfig.getNeighbours(x, y);
			for (Point n : neighbours)
				if (countries[n.x][n.y] != null 
					&& dc.distanceToEnemy[n.x][n.y] > d)
				{
					dc.distanceToEnemy[n.x][n.y] = d;
					list.add(new ListEntry(n.x, n.y, d));
				}
		}
		
		// Store result
		defendableCountries[Army.toInt(army)] = dc;
		
		return dc;
	}

	/**
	 * Invalidate cached defendable countries 
	 */
	public void invalidateDefendableCountries()
	{
		defendableCountries[0] = null;
		defendableCountries[1] = null;
	}

	/**
	 * Check if an attack from dcountry to acountry is possible
	 */
	public boolean isAttackPossible(Country dcountry, Country acountry, Army army)
	{
		DefendableCountries dc = determineDefendableCountries(army);
		
		return dcountry != null
			&& acountry != null
			&& !dcountry.countryConfig.isCapturePoint
			&& !acountry.countryConfig.isCapturePoint
			&& dcountry.army == army 
			&& acountry.army != army
			&& dcountry.currentGameId == 0
			&& acountry.currentGameId == 0
			&& dcountry.defender == null
			&& dcountry.attacker == null
			&& acountry.defender == null
			&& acountry.attacker == null
			&& dc.distanceToEnemy[dcountry.locationX][dcountry.locationY] == dc.requiredDistance
			&& HexagonGrid.getDistance(dcountry.locationX, dcountry.locationY, acountry.locationX, acountry.locationY) == dc.requiredDistance;
	}
}

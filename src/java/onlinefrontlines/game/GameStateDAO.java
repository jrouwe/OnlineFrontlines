package onlinefrontlines.game;

import java.text.DateFormat;
import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.game.actions.*;
import onlinefrontlines.utils.CacheException;
import onlinefrontlines.utils.DbQueryHelper;
import onlinefrontlines.utils.DbStoredProcHelper;
import onlinefrontlines.utils.Tools;

/**
 * This class communicates with the database and manages reading/writing GameState objects
 * 
 * @see onlinefrontlines.game.GameState

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
public class GameStateDAO 
{
	/**
	 * Summary of a game for listing purposes
	 */
	public static class Summary
	{
		/**
		 * Game id
		 */
		public int id;
		
		/**
		 * Country config name
		 */
		public String countryConfigName;
		
		/**
		 * Map id
		 */
		public int mapId;
		
		/**
		 * Current turn
		 */
		public int turnNumber;
		
		/**
		 * Turn end time
		 */
		public long turnEndTime;
		
		/**
		 * Current player mask (bit 0 = Faction 1, bit 1 = Faction 2)
		 */
		public int currentPlayerMask;
		
		/**
		 * Winning faction
		 */
		public Faction winningFaction = Faction.invalid;
		
		/**
		 * Players
		 */
		public int player1Id;
		public int player2Id;		
		public String player1Name;
		public String player2Name;
		
		/**
		 * Game id
		 */
		public int getId()
		{
			return id;
		}
		
		/**
		 * Country config name
		 */
		public String getCountryConfigName()
		{
			return countryConfigName;
		}
		
		/**
		 * Map id
		 */
		public int getMapId()
		{
			return mapId;
		}
		
		/**
		 * Current turn
		 */
		public int getTurnNumber()
		{
			return turnNumber;
		}
		
		/**
		 * Check if player 1 can perform an action
		 */
		public boolean getPlayer1CanPerformAction()
		{
			return (currentPlayerMask & 1) != 0;
		}
		
		/**
		 * Check if player 2 can perform an action
		 */
		public boolean getPlayer2CanPerformAction()
		{
			return (currentPlayerMask & 2) != 0;
		}
		
		/**
		 * User id of player 1
		 */
		public int getPlayer1Id()
		{
			return player1Id;
		}

		/**
		 * Username of player 1
		 */
		public String getPlayer1Name()
		{
			return player1Name;
		}
		
		/**
		 * User id of player 2
		 */
		public int getPlayer2Id()
		{
			return player2Id;
		}

		/**
		 * Username of player 2
		 */
		public String getPlayer2Name()
		{
			return player2Name;
		}
		
		/**
		 * Check if game has ended
		 */
		public boolean getHasGameEnded()
		{
			return winningFaction != Faction.invalid;
		}
		
		/**
		 * Check if it is a users turn to move
		 * 
		 * @param userId User to check for
		 */
		public boolean getIsMyTurn(int userId)
		{
			return (player1Id == userId && getPlayer1CanPerformAction()) || (player2Id == userId && getPlayer2CanPerformAction());
		}
	}

	/**
	 * Comparator for sorting summaries
	 */
	private static class SummarySorter implements Comparator<Summary>
	{
		public SummarySorter(int currentUserId)
		{
			this.currentUserId = currentUserId;
		}
		
		public int compare(Summary s1, Summary s2)
		{
			// First sort on finished or not (finished games go last)
			boolean s1Finished = s1.winningFaction != Faction.invalid;
			boolean s2Finished = s2.winningFaction != Faction.invalid;
			if (s1Finished != s2Finished)
				return s2Finished? -1 : 1;
			
			// Then sort on if the current user can do something (games where it is my turn go first)
			boolean s1MyTurn = s1.getIsMyTurn(currentUserId);
			boolean s2MyTurn = s2.getIsMyTurn(currentUserId);
			if (s1MyTurn != s2MyTurn)
				return s1MyTurn? -1 : 1;
			
			// Finally sort on turn end time (closer to now goes first)
			return s1.turnEndTime - s2.turnEndTime < 0? -1 : 1;
		}
		
		private int currentUserId;
	}
	
	/**
	 * Get list number games that the user can continue playing
	 * 
	 * @param userId User id of the user that wants to continue playing
	 * @return Number of games
	 */
	public static int getNumberOfGamesToContinue(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	helper.prepareQuery("SELECT COUNT(1) FROM games WHERE (userId1=? OR userId2=?) AND (winningFaction=? OR winningTime>?) AND (corrupt=0)");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, userId);
	    	helper.setInt(3, Faction.toInt(Faction.invalid));
	    	helper.setLong(4, Calendar.getInstance().getTime().getTime() - 2L * 24L * 60L * 60L * 1000L);
	    	helper.executeQuery();
	    	
	    	if (helper.nextRecord())
	    		return helper.getInt(1);
	    	else
	    		return 0;
	    }        
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Get list of games that the user needs to make a move
	 */
	public static List<Integer> getGamesIdsToContinue(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	helper.prepareQuery("SELECT id FROM games WHERE ((userId1=? AND (currentPlayer & 1)<>0) OR (userId2=? AND (currentPlayer & 2)<>0)) AND (winningFaction=?) AND (corrupt=0)");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, userId);
	    	helper.setInt(3, Faction.toInt(Faction.invalid));
	    	helper.executeQuery();

	    	ArrayList<Integer> rv = new ArrayList<Integer>();
	    	while (helper.nextRecord())
	    		rv.add(helper.getInt(1));
	    	return rv;
	    }        
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Get list of games that the user can continue playing
	 * 
	 * @param userId User id of the user that wants to continue playing
	 * @return List of games that the user can continue
	 */
	public static Summary[] getGamesToContinue(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		ArrayList<Summary> rv = new ArrayList<Summary>();
    		
        	helper.prepareQuery("SELECT games.id, country_configs.name, user1.id, user2.id, user1.username, user2.username, country_configs.mapId, turnNumber, currentPlayer, winningFaction, turnEndTime FROM games"
        						+ " INNER JOIN country_configs ON country_configs.id=countryConfigId"
        						+ " LEFT JOIN users AS user1 ON user1.id=userId1"
        						+ " LEFT JOIN users AS user2 ON user2.id=userId2"
        						+ " WHERE (userId1=? OR userId2=?) AND (winningFaction=? OR winningTime>?) AND (corrupt=0)");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, userId);
	    	helper.setInt(3, Faction.toInt(Faction.invalid));
	    	helper.setLong(4, Calendar.getInstance().getTime().getTime() - 2L * 24L * 60L * 60L * 1000L);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		Summary s = new Summary();
	    		s.id = helper.getInt(1);
	    		s.countryConfigName = helper.getString(2);
	    		s.player1Id = helper.getInt(3);
	    		s.player2Id = helper.getInt(4);
	    		s.player1Name = helper.getString(5);
	    		s.player2Name = helper.getString(6);
	    		s.mapId = helper.getInt(7);
	    		s.turnNumber = helper.getInt(8);
	    		s.currentPlayerMask = helper.getInt(9);
	    		s.winningFaction = Faction.fromInt(helper.getInt(10));
	    		s.turnEndTime = helper.getLong(11);
	    		rv.add(s);
	    	}
	    
	    	// Sort the results
			Summary[] sortedList = rv.toArray(new Summary[0]);
	    	Arrays.sort(sortedList, new SummarySorter(userId));
	    	return sortedList;
	    }        
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Get list of games that have timed out
	 * 
	 * @return List of games that have timed out
	 */
	public static ArrayList<Integer> getTimedOutGames() throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		ArrayList<Integer> rv = new ArrayList<Integer>();
    		
        	helper.prepareQuery("SELECT id FROM games"
        						+ " WHERE (turnEndTime IS NOT NULL) AND (?>turnEndTime) AND (winningFaction=?) AND (corrupt=0)");
	    	helper.setLong(1, Calendar.getInstance().getTime().getTime());
	    	helper.setInt(2, Faction.toInt(Faction.invalid));
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    		rv.add(helper.getInt(1));
	    	
	    	return rv;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Get list of games that the user can join playing
	 * 
	 * @param userId User id of the user that wants to join a game
	 * @return List of games that are joinable
	 */
	public static ArrayList<Summary> getJoinableGames(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		ArrayList<Summary> rv = new ArrayList<Summary>();
    		
        	helper.prepareQuery("SELECT games.id, country_configs.name, user1.id, user1.username FROM games"
        						+ " INNER JOIN country_configs ON country_configs.id=countryConfigId"
        						+ " LEFT JOIN users AS user1 ON user1.id=userId1"
        						+ " WHERE (userId1<>?)" 
        						+ " AND (userId2 IS NULL)"
        						+ " AND (winningFaction=?)"
        						+ " AND (corrupt=0)");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, Faction.toInt(Faction.invalid));
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		Summary s = new Summary();
	    		s.id = helper.getInt(1);
	    		s.countryConfigName = helper.getString(2);
	    		s.player1Id = helper.getInt(3);
	    		s.player1Name = helper.getString(4);
	    		rv.add(s);
	    	}
	    	
	    	return rv;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Get summary of game
	 * 
	 * @param gameId Game id of game
	 * @return Summary
	 * @throws SQLException
	 */
	public static Summary getGameSummary(int gameId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	helper.prepareQuery("SELECT country_configs.name, user1.id, user2.id, user1.username, user2.username, winningFaction FROM games"
        						+ " INNER JOIN country_configs ON country_configs.id=countryConfigId"
        						+ " LEFT JOIN users AS user1 ON user1.id=userId1"
        						+ " LEFT JOIN users AS user2 ON user2.id=userId2"
        						+ " WHERE games.id=?");
	    	helper.setInt(1, gameId);
	    	helper.executeQuery();
	    	
	    	if (!helper.nextRecord())
	    		return null;
	    	
    		Summary s = new Summary();
    		s.id = gameId;
    		s.countryConfigName = helper.getString(1);
    		s.player1Id = helper.getInt(2);
    		s.player2Id = helper.getInt(3);
    		s.player1Name = helper.getString(4);
    		s.player2Name = helper.getString(5);
    		s.winningFaction = Faction.fromInt(helper.getInt(6));
    		return s;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Game in progress for a lobby
	 */
	public static class GameForLobby
	{
		public int gameId;
		public int attackerUserId;
		public int defenderUserId;
		public int attackedCountryX;
		public int attackedCountryY;
		public int defendedCountryX;
		public int defendedCountryY;
		public Faction winningFaction;
	}
	
	/**
	 * Query to get games for a lobby
	 */
	private static final String gamesForLobbiesQuery = "SELECT id, userId1, userId2, attackedCountryX, attackedCountryY, defendedCountryX, defendedCountryY, winningFaction FROM games";
	
	/**
	 * Helper function to fill in an array of games for a lobby
	 */
	private static ArrayList<GameForLobby> fillGamesForLobbies(DbQueryHelper helper) throws SQLException
	{
    	ArrayList<GameForLobby> games = new ArrayList<GameForLobby>();
    	
    	while (helper.nextRecord())
    	{
    		GameForLobby g = new GameForLobby();
    		
    		g.gameId = helper.getInt(1);
    		g.attackerUserId = helper.getInt(2);
    		g.defenderUserId = helper.getInt(3);
    		g.attackedCountryX = helper.getInt(4);
    		g.attackedCountryY = helper.getInt(5);
    		g.defendedCountryX = helper.getInt(6);
    		g.defendedCountryY = helper.getInt(7);
    		g.winningFaction = Faction.fromInt(helper.getInt(8));
    		
    		games.add(g);
    	}
    	
    	return games;

	}
	
	/**
	 * Get games in progress for a specific lobby
	 * 
	 * @param lobbyId Lobby id of lobby to query for
	 */
	public static ArrayList<GameForLobby> getGamesForLobby(int lobbyId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	helper.prepareQuery(gamesForLobbiesQuery + " WHERE lobbyId=? AND lobbyProcessedGame=0");
	    	helper.setInt(1, lobbyId);
	    	helper.executeQuery();
	    	
	    	return fillGamesForLobbies(helper);
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Get list of games that need to be processed by a lobby
	 * 
	 * @param lobbyId Lobby to get games for
	 * @return List of games that should be processed 
	 * @throws SQLException
	 */
	public static ArrayList<GameForLobby> getGamesForLobbyToProcess(int lobbyId) throws SQLException
	{
		DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery(gamesForLobbiesQuery + " WHERE (lobbyId=? AND lobbyProcessedGame=0 AND (winningFaction<>? OR corrupt=1))");
	    	helper.setInt(1, lobbyId);
	    	helper.setInt(2, Faction.toInt(Faction.invalid));
	    	helper.executeQuery();
	    	
	    	return fillGamesForLobbies(helper);
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }        
	}

	/**
	 * Mark game processed by lobby
	 * 
	 * @param gameId Id of game
	 */
	public static void markGameProcessedByLobby(int gameId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("UPDATE games SET lobbyProcessedGame=1 WHERE id=?");
	    	helper.setInt(1, gameId);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Get number of games in progress for a specific lobby
	 * 
	 * @param lobbyId Lobby id of lobby to query for
	 */
	public static int getGameCountForLobby(int lobbyId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	helper.prepareQuery("SELECT COUNT(1) FROM games WHERE lobbyId=? AND winningFaction=? AND corrupt=0");
	    	helper.setInt(1, lobbyId);
	    	helper.setInt(2, Faction.toInt(Faction.invalid));
	    	helper.executeQuery();	    	
	    	helper.nextRecord();
	    	return helper.getInt(1);
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Class that contains results of multiple games
	 */
	public static class GameResults
	{
		/**
		 * Number of games still in progress
		 */
		public int inProgress;
		
		/**
		 * Number of games that faction 1 won
		 */
		public int f1Wins;
		
		/**
		 * Number of games that faction 2 won
		 */
		public int f2Wins;
		
		/**
		 * Number of games that resulted in a draw
		 */
		public int draws;

		/**
		 * Number of games still in progress
		 */
		public int getInProgress()
		{
			return inProgress;
		}
		
		/**
		 * Number of games that faction 1 won
		 */
		public int getF1Wins()
		{
			return f1Wins;
		}
		
		/**
		 * Number of games that faction 2 won
		 */
		public int getF2Wins()
		{
			return f2Wins;
		}
		
		/**
		 * Number of games that resulted in a draw
		 */
		public int getDraws()
		{
			return draws;
		}

		/**
		 * Get total amount of games played
		 */
		public int getTotal()
		{
			return inProgress + f1Wins + f2Wins + draws;
		}
	}
	
	/**
	 * Class that contains results of multiple games ordered by map
	 */
	public static class GameResultsByMap extends GameResults
	{
		/**
		 * Id of the map that this results belong to
		 */
		public int mapId;
		
		/**
		 * Id of the map that this results belong to
		 */
		public int getMapId()
		{
			return mapId;
		}
		
		/**
		 * Get map name
		 */
		public String getMapName() throws CacheException
		{
			return MapConfigCache.getInstance().get(mapId).name;
		}
	}
	
	/**
	 * Class that contains results of multiple games ordered by country
	 */
	public static class GameResultsByCountry extends GameResults
	{
		/**
		 * Id of the country that this results belong to
		 */
		public int countryConfigId;
		
		/**
		 * Id of the country that this results belong to
		 */
		public int getCountryConfigId()
		{
			return countryConfigId;
		}
		
		/**
		 * Get country config name
		 */
		public String getCountryConfigName() throws CacheException
		{
			return CountryConfigCache.getInstance().get(countryConfigId).name;
		}
	}
	
	/**
	 * Get game results by country
	 */
	public static ArrayList<GameResultsByCountry> getGameResultsByCountryConfig() throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		ArrayList<GameResultsByCountry> rv = new ArrayList<GameResultsByCountry>();
    		
        	helper.prepareQuery("SELECT countryConfigId, SUM(IF(winningFaction = 0, 1, 0)) AS inProgress, SUM(IF(winningFaction = 1, 1, 0)) AS f1, SUM(IF(winningFaction = 2, 1, 0)) AS f2, SUM(IF(winningFaction = 3, 1, 0)) AS draw FROM games WHERE userId2 IS NOT NULL GROUP BY countryConfigId");
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		GameResultsByCountry s = new GameResultsByCountry();
	    		s.countryConfigId = helper.getInt(1);
	    		s.inProgress = helper.getInt(2);
	    		s.f1Wins = helper.getInt(3);
	    		s.f2Wins = helper.getInt(4);
	    		s.draws = helper.getInt(5);
	    		rv.add(s);
	    	}
	    	
	    	return rv;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Get game results by country
	 */
	public static ArrayList<GameResultsByMap> getGameResultsByMap() throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		ArrayList<GameResultsByMap> rv = new ArrayList<GameResultsByMap>();
    		
        	helper.prepareQuery("SELECT country_configs.mapId, SUM(IF(winningFaction = 0, 1, 0)) AS inProgress, SUM(IF(winningFaction = 1, 1, 0)) AS f1, SUM(IF(winningFaction = 2, 1, 0)) AS f2, SUM(IF(winningFaction = 3, 1, 0)) AS draw FROM games LEFT JOIN country_configs ON country_configs.id = games.countryConfigId WHERE userId2 IS NOT NULL GROUP BY mapId");
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		GameResultsByMap s = new GameResultsByMap();
	    		s.mapId = helper.getInt(1);
	    		s.inProgress = helper.getInt(2);
	    		s.f1Wins = helper.getInt(3);
	    		s.f2Wins = helper.getInt(4);
	    		s.draws = helper.getInt(5);
	    		rv.add(s);
	    	}
	    	
	    	return rv;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Summary of a game for listing purposes
	 */
	public static class FinishedGame
	{
		/**
		 * Game id
		 */
		public int id;
		
		/**
		 * Opponent
		 */
		public int opponentId;
		public String opponentName;
		
		/**
		 * Game time
		 */
		public long winningTime;
		
		/**
		 * Game result
		 */
		public String result;
		
		/**
		 * Get game id
		 */
		public int getId()
		{
			return id;
		}
		
		/**
		 * Opponent user id
		 */
		public int getOpponentId()
		{
			return opponentId;
		}
		
		/**
		 * Get opponent name
		 */
		public String getOpponentName()
		{
			return opponentName;
		}
		
		/**
		 * Get winning time as a string
		 */
		public String getWinningDateString()
		{
			return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(new Date(winningTime)); 
		}
		
		/**
		 * Get result
		 */
		public String getResult()
		{
			return result;
		}
	}

	/**
	 * Get list of games that the user finished recently
	 * 
	 * @param userId User id of the user
	 * @return List of finished games
	 */
	public static ArrayList<FinishedGame> getFinishedGames(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		ArrayList<FinishedGame> rv = new ArrayList<FinishedGame>();

    		helper.prepareQuery("SELECT games.id, user1.id, user2.id, user1.username, user2.username, games.winningTime, games.winningFaction FROM games"
        						+ " LEFT JOIN users AS user1 ON user1.id=userId1"
        						+ " LEFT JOIN users AS user2 ON user2.id=userId2"
        						+ " WHERE (userId1=? OR userId2=?) AND (userId2 IS NOT NULL) AND (winningFaction<>?) AND (corrupt=0)"
        						+ " ORDER BY games.winningTime DESC LIMIT 10");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, userId);
	    	helper.setInt(3, Faction.toInt(Faction.invalid));
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		FinishedGame s = new FinishedGame();
	    		s.id = helper.getInt(1);
	    		boolean playerIsFaction1 = helper.getInt(2) == userId; 
	    		if (playerIsFaction1)
	    		{
	        		s.opponentId = helper.getInt(3);
		    		s.opponentName = helper.getString(5);
	    		}
	    		else
	    		{
	        		s.opponentId = helper.getInt(2);
		    		s.opponentName = helper.getString(4);
	    		}	    		
	    		s.winningTime = helper.getLong(6);
	    		switch (Faction.fromInt(helper.getInt(7)))
				{
	    		case f1:
	    			s.result = playerIsFaction1? "won" : "lost";
	    			break;
	    		case f2:
	    			s.result = playerIsFaction1? "lost" : "won";
	    			break;
	    		default:
	    			s.result = "draw";
	    			break;
				}	    		
	    		rv.add(s);
	    	}
	    	
	    	return rv;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Create an entry for a game in the database
	 * 
	 * @param gameState Game to create an entry for
	 * @throws SQLException
	 */
	public static void create(GameState gameState) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("INSERT INTO games (countryConfigId, faction1IsRed, faction1Starts, lobbyId, attackedCountryX, attackedCountryY, defendedCountryX, defendedCountryY, creationTime, winningFaction, playByMail, actions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	    	helper.setInt(1, gameState.countryConfig.id);
	    	helper.setInt(2, gameState.faction1IsRed? 1 : 0);
	    	helper.setInt(3, gameState.faction1Starts? 1 : 0);
	    	helper.setInt(4, gameState.lobbyId);
	    	helper.setInt(5, gameState.attackedCountryX);
	    	helper.setInt(6, gameState.attackedCountryY);
	    	helper.setInt(7, gameState.defendedCountryX);
	    	helper.setInt(8, gameState.defendedCountryY);
	    	helper.setLong(9, Calendar.getInstance().getTime().getTime());
	    	helper.setInt(10, Faction.toInt(Faction.invalid));
	    	helper.setInt(11, gameState.playByMail? 1 : 0);
	    	helper.setString(12, gameState.actions);
	    	helper.executeUpdate();
	    	
	    	// Set id
	    	ArrayList<Integer> generatedKeys = helper.getGeneratedKeys();
	    	gameState.id = generatedKeys.get(0);
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Remove game from database
	 * 
	 * @param id ID of the game to remove
	 * @throws SQLException
	 */
	public static void delete(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Remove maps record
	    	helper.prepareQuery("DELETE FROM games WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Insert user that joined game in the database
	 * 
	 * @param gameId Id of game
	 * @param faction Faction of the user
	 * @param userId Id of user that joined
	 */
	public static void joinGame(int gameId, int userId, Faction faction) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("UPDATE games SET " + (faction == Faction.f1? "userId1" : "userId2") + "=? WHERE id=?");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, gameId);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Mark a game as corrupt
	 * 
	 * @param gameId Game Id of the game
	 */
	public static void markCorrupt(int gameId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("UPDATE games SET corrupt=1 WHERE id=?");
	    	helper.setInt(1, gameId);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Load a game
	 * 
	 * @param gameId Id of the game to load
	 * @return The game
	 * @throws SQLException
	 */
	public static GameState load(int gameId) throws SQLException, CacheException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Get record
	    	helper.prepareQuery("SELECT countryConfigId, userId1, userId2, faction1IsRed, faction1Starts, lobbyId, attackedCountryX, attackedCountryY, defendedCountryX, defendedCountryY, playByMail, turnEndTime, corrupt, actions FROM games WHERE id=?");
	    	helper.setInt(1, gameId);
	    	helper.executeQuery();	    	
	    	if (!helper.nextRecord())
	    		return null;
	    	
	    	// Check corrupt
	    	if (helper.getInt(13) != 0)
	    		throw new RuntimeException("Trying to load corrupt game");
	    	
	    	// Get country config 
	    	CountryConfig gc = CountryConfigCache.getInstance().get(helper.getInt(1));
    	
	    	// Create game state
			GameState gameState = gc.createGameState(helper.getInt(4) != 0, helper.getInt(5) != 0, helper.getInt(6), helper.getInt(7), helper.getInt(8), helper.getInt(9), helper.getInt(10), helper.getInt(11) != 0, -1);
			gameState.id = gameId;
			gameState.turnEndTime = helper.getLong(12);
			
			// Set users
			int userId1 = helper.getInt(2);
			if (userId1 > 0)
				gameState.reJoinGame(Faction.f1, UserCache.getInstance().get(userId1));
			int userId2 = helper.getInt(3);
			if (userId2 > 0)
				gameState.reJoinGame(Faction.f2, UserCache.getInstance().get(userId2));
			
			// Execute actions
			gameState.actions = helper.getString(14);
			String actions[] = gameState.actions.split("\n");
			for (String a : actions)
	    	{
	    		try
	    		{
		    		String[] params = a.split(",");
		    		Action action = Action.createAction(params[0]);
		    		action.setGameState(gameState);
		    		action.fromString(params, null);
		    		gameState.execute(action, false);
	    		}
	    		catch (IgnoreActionException e)
	    		{
	    			// Just ignore
	    		}
	    		catch (Exception e)
	    		{
	    			// Should normally not happen as the action was executed before
					Tools.logException("Exception caught while loading game, "
				        				+ "game: '" + gameId + "', " 
				        				+ "action: '" + a + "', exception: ", e);
	        		
	        		// Mark this game as corrupt
	        		try
	        		{
	        			markCorrupt(gameId);
	        		}
	        		catch (SQLException e2)
	        		{
	    				Tools.logException(e2);
	        		}
	        		
	        		// Wrap as SQL exception and throw it
	        		throw new SQLException(e);
	    		}
	    	}
	    	
	    	// Last action received is longer than the time out value, so set it to that
	    	gameState.lastActionTime -= GameStateCache.TIME_OUT; 
	    	
	    	return gameState;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Updates the state of the game in the database
	 *
	 * @param gameState The game state to update
	 * @throws SQLException
	 */
	public static void update(GameState gameState) throws SQLException
	{
    	DbStoredProcHelper helper = new DbStoredProcHelper();
        try
        {
        	Score f1 = gameState.getScore(Faction.f1);
        	Score f2 = gameState.getScore(Faction.f2);

			helper.prepareCall("{CALL updateGame(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
	    	helper.setString(1, gameState.actions);
	    	helper.setLong(2, gameState.turnEndTime);
	    	helper.setInt(3, gameState.turnNumber);
	    	helper.setInt(4, (gameState.canPerformAction(Faction.f1)? 1 : 0) + (gameState.canPerformAction(Faction.f2)? 2 : 0));
	    	helper.setInt(5, Faction.toInt(gameState.winningFaction));
	    	helper.setInt(6, f1.getTotalScore());
	    	helper.setInt(7, f2.getTotalScore());
	    	helper.setInt(8, f1.numberOfUnitsDestroyed + f1.numberOfBasesDestroyed);
	    	helper.setInt(9, f2.numberOfUnitsDestroyed + f2.numberOfBasesDestroyed);
	    	helper.setLong(10, Calendar.getInstance().getTime().getTime());
	    	helper.setInt(11, gameState.id);
	    	helper.execute();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }
	}
	
	/**
	 * Get game results by country
	 */
	public static int getGamesRequiringAttentionCount(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	helper.prepareQuery("SELECT COUNT(1) FROM games WHERE ((userId1=? AND (currentPlayer & 1) <> 0) OR (userId2=? AND (currentPlayer & 2) <> 0)) AND winningFaction=? AND corrupt=0");
        	helper.setInt(1, userId);
        	helper.setInt(2, userId);
        	helper.setInt(3, Faction.toInt(Faction.invalid));
	    	helper.executeQuery();
	    	
	    	if (helper.nextRecord())
	    		return helper.getInt(1);
	    	else
	    		return 0;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
}

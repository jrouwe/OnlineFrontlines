package onlinefrontlines.game;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.Calendar;
import java.util.ArrayList;
import java.awt.Point;
import java.sql.SQLException;
import javax.mail.internet.InternetAddress;
import onlinefrontlines.Constants;
import onlinefrontlines.auth.*;
import onlinefrontlines.game.actions.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.userstats.*;
import onlinefrontlines.utils.GlobalProperties;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.Mailer;
import onlinefrontlines.utils.Tools;

/**
 * Current state for an active game
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
public final class GameState
{
	/********* BEGIN SYNCHRONIZED BLOCK BETWEEN FLASH AND JAVA *********/
	
	/**
	 * Id of the game
	 */
	public int id = -1;
	
	/**
	 * The players that are playing this game
	 */
	private final Player[] players = new Player[2];
	
	/**
	 * Indicate which players are ready
	 */
	public boolean playerReady[] = { false, false };
	
	/**
	 * If faction 1 starts or 2
	 */
	public final boolean faction1Starts;
	
	/**
	 * Faction of current player
	 */
	public Faction currentPlayer;
	
	/**
	 * Number of turns played
	 */
	public int turnNumber = 0;
	
	/**
	 * Time when the current turn ends
	 */
	public long turnEndTime;

	/**
	 * Initial terrain owners, to calculate score
	 */
	public Faction[] initialTerrainOwners;

	/**
	 * Score objects
	 */
	public final Score[] scores = { new Score(), new Score() };
	
	/**
	 * Players that requested a draw
	 */
	public boolean drawRequested[] = { false, false };
	
	/**
	 * If the game has ended this will indicate the winning faction
	 */
	public Faction winningFaction = Faction.invalid;
	
	/**
	 * All units
	 */
	private final ArrayList<UnitState> units = new ArrayList<UnitState>();
	
	/**
	 * Quick access to a unit by its location
	 */
	private final UnitState[][] unitGrid;

	/**
	 * Quick access to a unit
	 */
	private final HashMap<Integer, UnitState> unitMap = new HashMap<Integer, UnitState>();
	
	/**
	 * Get player
	 */
	public Player getPlayer(Faction faction)
	{
		switch (faction)
		{
		case f1:
			return players[0];
		case f2:
			return players[1];
		default:
			return null;
		}
	}
	
	/**
	 * Get player user id
	 */
	public int getPlayerId(Faction faction)
	{
		Player player = getPlayer(faction);
		return player != null? player.id : -1;			
	}
	
	/**
	 * Get score object
	 */
	public Score getScore(Faction faction)
	{
		switch (faction)
		{
		case f1:
			return scores[0];
		case f2:
			return scores[1];
		default:
			return null;
		}
	}
	
	/**
	 * Join game for second time
	 */
	public void reJoinGame(Faction faction, User user)
	{
		players[faction == Faction.f1? 0 : 1] = new Player(user);
	}

	/**
	 * Register unit with this game 
	 */
	public void registerUnit(UnitState unit)
	{
		assert(unitMap.get(unit.id) == null);
		unitMap.put(unit.id, unit);
	}
	
	/**
	 * Unregister unit
	 */
	public void unregisterUnit(UnitState unit)
	{
		assert(unitMap.get(unit.id) == unit);
		unitMap.remove(unit.id);
	}
	
	/**
	 * Find a unit by its id
	 */
	public UnitState getUnitById(int id)
	{
		return unitMap.get(id);
	}

	/**
	 * Add unit at particular location
	 */
	public void addUnit(UnitState unit)
	{
		// Add to grid and array
		assert(unitGrid[unit.locationX][unit.locationY] == null);
		unitGrid[unit.locationX][unit.locationY] = unit;

		assert(!units.contains(unit));
		units.add(unit);
		
		updateIdentification();
	}
	
	/**
	 * Remove a unit
	 */ 
	public void removeUnit(UnitState unit)
	{
		// Remove from grid and array
		assert(unitGrid[unit.locationX][unit.locationY] == unit);
		unitGrid[unit.locationX][unit.locationY] = null;
		
		assert(units.contains(unit));
		units.remove(unit);
	}
	
	/**
	 * Get unit at particular location
	 */
	public UnitState getUnit(int x, int y)
	{ 
		return unitGrid[x][y];
	}
	
	/**
	 * Get all units
	 */ 
	public ArrayList<UnitState> getUnits()
	{
		return units;
	}
	
	/**
	 * Get terrain at (x, y)
	 */
	public TerrainConfig getTerrainAt(int x, int y)
	{
		return mapConfig.getTerrainAt(x, y);
	}
	
	/**
	 * Get terrain unit is on
	 */ 
	public TerrainConfig getTerrain(UnitState unit)
	{
		return unit.container == null? getTerrainAt(unit.locationX, unit.locationY) : getTerrain(unit.container);
	}	

	/**
	 * Returns terrain owner at x, y
	 */
	public Faction getTerrainOwnerAt(int x, int y)
	{
		return tileOwners[x + y * mapConfig.sizeX];
	}

	/**
	 * Sets terrain owner at x, y
	 */
	public void setTerrainOwnerAt(int x, int y, Faction newOwner)
	{
		Faction initialOwner = getInitialTerrainOwnerAt(x, y);
		Faction oldOwner = getTerrainOwnerAt(x, y);
		
		tileOwners[x + y * mapConfig.sizeX] = newOwner; 
	
		if (newOwner != oldOwner)
		{
			int victoryPoints = getTerrainAt(x, y).victoryPoints;
			if (victoryPoints > 0)
			{			
				// Award points if tile captured				
				if (newOwner != initialOwner)
				{
					Score score = getScore(newOwner);
					if (score != null)
					{
						score.numberOfTilesOwned++;
						score.victoryPointsForTiles += victoryPoints;
					}
				}
				
				// Deduct points if tile is lost again
				if (oldOwner != initialOwner)
				{
					Score score = getScore(oldOwner);
					if (score != null)
					{
						score.numberOfTilesOwned--;
						score.victoryPointsForTiles -= victoryPoints;
					}
				}
			}
		}
	}
	
	/**
	 * Update ownership of tile unit is on
	 */		
	public void updateTerrainOwner(UnitState unit)
	{
		if (unit.armour > 0
			&& unit.unitConfig.unitClass == UnitClass.land
			&& getTerrain(unit).victoryPoints > 0)
			setTerrainOwnerAt(unit.locationX, unit.locationY, unit.faction);
	}

	/**
	 * Calculate initial terrain state
	 */
	public void calculateInitialTerrainOwners()
	{
		initialTerrainOwners = new Faction[mapConfig.sizeX * mapConfig.sizeY];
		
		// Loop through tiles
		for (int x = 0; x < mapConfig.sizeX; ++x)
			for (int y = 0; y < mapConfig.sizeY; ++y)
			{
				// Reset owner if this type of terrain doesn't give any points
				TerrainConfig terrain = getTerrainAt(x, y);
				if (terrain.victoryPoints <= 0)
					setTerrainOwnerAt(x, y, Faction.none);
				
				// Store owner
				initialTerrainOwners[x + y * mapConfig.sizeX] = getTerrainOwnerAt(x, y);
			}
	}
	
	/**
	 * Returns terrain owner at x, y when the game started
	 */
	public Faction getInitialTerrainOwnerAt(int x, int y)
	{
		return initialTerrainOwners[x + y * mapConfig.sizeX];
	}

	/**
	 * Get terrain unit is on (used for attacking, with special case for air)
	 */
	public TerrainConfig getAttackTerrain(UnitState unit)
	{
		// Flying units are always in the air
		if (unit.unitConfig.unitClass == UnitClass.air)
			return TerrainConfig.airTerrain;
			
		// Otherwise look for the tile we're on
		return getTerrain(unit);
	}	
	
	/**
	 * Get terrain unit is on (used for attacking, with special case for air)
	 */
	public TerrainConfig getAttackTerrain(UnitState unit, int x, int y)
	{
		// Flying units are always in the air
		if (unit.unitConfig.unitClass == UnitClass.air)
			return TerrainConfig.airTerrain;

		return getTerrainAt(x, y);
	}

	/**
	 * Check if a unit can be set up on on tile x, y
	 */
	public boolean canUnitBeTeleportedTo(UnitState unitState, int x, int y)
	{
		// Check if base is too close to another base
		if (unitState.unitConfig.isBase)
			for (UnitState u : units)
				if (u != unitState
					&& u.faction == unitState.faction
					&& u.unitConfig.isBase
					&& u.getDistanceTo(x, y) <= 2)
					return false;
		
		return canUnitBeSetupOnHelper(mapConfig, unitState.initialUnitConfig, unitState.faction, x, y);
	}

	/**
	 * Check if a unit can be set up on tile x, y
	 */
	public static boolean canUnitBeSetupOnHelper(MapConfig mapConfig, UnitConfig unitConfig, Faction faction, int x, int y)
	{
		// Unit must be in playable area and the terrain must be owned by the correct faction
		if (!mapConfig.isTileInPlayableArea(x, y)
			|| mapConfig.getTerrainOwnerAt(x, y) != faction)
			return false;

		// If unit needs to be set up next to a specific terrain, validate it
		if (unitConfig.unitSetupNextTo.size() > 0)
		{
			boolean found = false;
			for (Point n : mapConfig.getNeighbours(x, y))
				if (unitConfig.unitSetupNextTo.contains(mapConfig.getTerrainAt(n.x, n.y).id))
				{
					found = true;
					break;
				}
			if (!found)
				return false;
		}

		// If unit can move here
		TerrainConfig terrain = mapConfig.getTerrainAt(x, y);
		if (unitConfig.canMoveOn(terrain))
			return true;
		
		// If terrain is in the list where the unit can be set up on
		if (unitConfig.unitSetupOn.contains(terrain.id))
			return true;
		
		return false;			
	}
		
	/**
	 * Move unit to new location
	 */
	public void moveUnit(UnitState unit, int x, int y)
	{
		unitGrid[unit.locationX][unit.locationY] = null;
		
		unit.locationX = x;
		unit.locationY = y;
		
		unitGrid[x][y] = unit;

		updateIdentification();
	}
	
	/**
	 * Move unit into container unit
	 */
	public void moveUnitInContainer(UnitState unit, UnitState container)
	{
		// Remove from grid
		removeUnit(unit);

		// Add to container
		container.addUnit(unit);
	}
	
	/**
	 * Remove unit from container
	 */
	public void moveUnitOutOfContainer(UnitState unit, UnitState container, int x, int y)
	{
		// Remove from container
		container.removeUnit(unit);
		
		// Place on new position
		unit.locationX = x;
		unit.locationY = y;
		
		// Add to grid
		addUnit(unit);

		// Check if unit can be seen again
		updateIdentification();
	}
	
	/**
	 * Get movement cost when unit moves (1 tile) to new location
	 */
	public int getMovementCost(UnitState unit, int x, int y)
	{
		// Get terrain
		TerrainConfig terrain = getTerrainAt(x, y);

		// Modify terrain under a base to type that unit supports
		UnitState base = getUnit(x, y);
		if (base != null && base.canHold(unit))
		{
			if (unit.unitConfig.unitClass == UnitClass.water)
				terrain = TerrainConfig.waterTerrain;
			else
				terrain = TerrainConfig.plainsTerrain;
		}

		// Get cost for this move
		return unit.unitConfig.getMovementCost(terrain);		
	}
	
	/**
	 * Set the winning faction and end the game
	 */
	public void setWinningFaction(Faction faction)
	{
		// Mark game as ended
		winningFaction = faction;

		// Identify all units
		for (UnitState u : units)
			u.identifyUnitRecursive();		

		try
		{
			// Send new scores to client
			if (players[0] != null)
				execute(new ActionPlayerProperties(Faction.f1), false);
			if (players[1] != null)
				execute(new ActionPlayerProperties(Faction.f2), false);
		}
		catch (IllegalRequestException e)
		{
			Tools.logException(e);
		}
	}

	/**
	 * If user can request draw or surrender (to prevent cheating)
	 */
	public boolean userCanTerminateGame()
	{
		return scores[0].getTotalScore() > 100 || scores[1].getTotalScore() > 100;
	}

	/**
	 * Dump state of the game for comparison
	 * 
	 * @param localFaction The faction that the state should be simulated for
	 */
	public String dumpState(Faction localFaction)
	{
		// Dump general properties
		StringBuilder rv = new StringBuilder(10240);
		rv.append("turnNumber = ");
		rv.append(turnNumber);
		rv.append("\ncurrentPlayer = ");
		rv.append(Faction.toInt(currentPlayer));
		
		// Dump initial terrain ownership
		rv.append("\ninitialTerrainOwner =\n");
		for (int y = 0; y < mapConfig.sizeY; ++y)
		{
			rv.append(Faction.toInt(getInitialTerrainOwnerAt(0, y)));
			for (int x = 1; x < mapConfig.sizeX; ++x)
			{
				rv.append(", ");
				rv.append(Faction.toInt(getInitialTerrainOwnerAt(x, y)));
			}
			rv.append("\n");
		}
		
		// Dump terrain ownership
		rv.append("terrainOwner =\n");
		for (int y = 0; y < mapConfig.sizeY; ++y)
		{
			rv.append(Faction.toInt(getTerrainOwnerAt(0, y)));
			for (int x = 1; x < mapConfig.sizeX; ++x)
			{
				rv.append(", ");
				rv.append(Faction.toInt(getTerrainOwnerAt(x, y)));
			}
			rv.append("\n");
		}
		
		// Dump scores
		for (int i = 0; i < 2; ++i)
		{
			rv.append("scores[");
			rv.append(i);
			rv.append("] = ");
			scores[i].dumpState(rv);
			rv.append("\n");
		}

		// Dump units
		rv.append("units =\n");
		for (int y = 0; y < mapConfig.sizeY; ++y)
			for (int x = 0; x < mapConfig.sizeX; ++x)
			{
				UnitState unit = getUnit(x, y);
				if (unit != null)
					unit.dumpState(rv, localFaction, 0);
			}
		
		return rv.toString();
	}

	/********* END SYNCHRONIZED BLOCK BETWEEN FLASH AND JAVA *********/

	/**
	 * Settings for the game
	 */
	public final CountryConfig countryConfig;
	
	/**
	 * Cached map config
	 */
	public final MapConfig mapConfig;
	
	/**
	 * Which color faction 1 is
	 */
	public final boolean faction1IsRed;
	
	/**
	 * Lobby that hosted the game
	 */
	public final int lobbyId;
	
	/**
	 * Country location
	 */
	public final int attackedCountryX;
	public final int attackedCountryY;
	public final int defendedCountryX;
	public final int defendedCountryY;
	
	/**
	 * If we want to send an email at the end of each turn
	 */
	public final boolean playByMail;
	
	/**
	 * Last communication time with client
	 */
	private long lastCommunicationTime[] = { 0, 0 };
	
	/**
	 * Flag to indicate if the current player did something this turn
	 */
	public boolean currentPlayerIdle = true;

	/**
	 * Owning faction of the tiles
	 */
	private final Faction[] tileOwners;
	
	/**
	 * True if state mismatch was already logged for this game
	 */
	public boolean loggedStateMismatch = false;
		
	/**
	 * Helper class that determines which faction should receive which actions
	 */
	public static class FilteredList
	{
		public ArrayList<String> sendList = new ArrayList<String>();
		public ArrayList<Action> pendingList = new ArrayList<Action>();
	}
	
	/**
	 * Actions filtered per receiving faction
	 */
	private final FilteredList[] filteredList = { new FilteredList(), new FilteredList(), new FilteredList() };
	
	/**
	 * All actions performed, linefeed separated
	 */
	public String actions = "";
	
	/**
	 * If the state has changed and the game state needs to be written to the database
	 */
	private boolean needsToBeWrittenToDb = false;
	
	/**
	 * Last time an action was received
	 */
	public long lastActionTime;
	
	/**
	 * Orred mask of all victory categories that are still active in the game 
	 */
	private int initialVictoryCategories[] = { 0, 0 };
	
	/**
	 * Scores from previous round
	 */
	public final Score[] previousTurnScores = { new Score(), new Score() };

	/**
	 * Stats for all units from a faction
	 */
	public static class AllUnitStats
	{
		public HashMap<Integer, UnitStats> unitToStatsMap = new HashMap<Integer, UnitStats>();
		
		/**
		 * Get unit stats for particular unit, if it doesn't exist yet a new entry will be created
		 */
		public UnitStats getOrCreate(int unitId)
		{
			// Find existing
			UnitStats u = unitToStatsMap.get(unitId);
			if (u != null)
				return u;
			
			// Create new
			u = new UnitStats();
			u.unitId = unitId;
			unitToStatsMap.put(unitId, u);
			return u;
		}
	}
	
	/**
	 * Keeps track of unit stats per player
	 */
	private AllUnitStats[] unitStats = { new AllUnitStats(), new AllUnitStats() };
	
	/**
	 * Random number generator
	 */ 
	public Random random;
	
	/**
	 * Constructor
	 * 
	 * @param countryConfig The configuration to start this game with
	 * @param faction1IsRed If faction 1 is red
	 * @param faction1Starts If faction 1 moves first
	 * @param lobbyId Lobby where this game belongs to (or -1)
	 * @param attackedCountryX X location of country that is being attacked from (or -1) 
	 * @param attackedCountryY Y location of country that is being attacked from (or -1) 
	 * @param defendedCountryX X location of country that is being defended from (or -1) 
	 * @param defendedCountryY Y location of country that is being defended from (or -1) 
	 * @param playByMail If this is a play by mail game
	 * @param randomSeed Seed to use for the random number generator (or -1 if a random seed should be chosen)
	 */
	public GameState(CountryConfig countryConfig, boolean faction1IsRed, boolean faction1Starts, int lobbyId, int attackedCountryX, int attackedCountryY, int defendedCountryX, int defendedCountryY, boolean playByMail, long randomSeed)
	{
		// Store config
		this.countryConfig = countryConfig;
		this.mapConfig = countryConfig.getMapConfig();
		this.faction1IsRed = faction1IsRed;
		this.faction1Starts = faction1Starts;
		this.lobbyId = lobbyId;
		this.attackedCountryX = attackedCountryX;
		this.attackedCountryY = attackedCountryY;
		this.defendedCountryX = defendedCountryX;
		this.defendedCountryY = defendedCountryY;
		this.playByMail = playByMail;
		
		// Create random number generator
		random = randomSeed != -1? new Random(randomSeed) : new Random();
		
		// Starting the game will first toggle the faction so currentPlayer starts at the other faction
		currentPlayer = faction1Starts? Faction.f2 : Faction.f1;
		
		// Create unit grid
		this.unitGrid = new UnitState[mapConfig.sizeX][mapConfig.sizeY];		

		// Clone owners (will be modified when units start moving around)
		tileOwners = mapConfig.cloneTileOwners();
		
		// Mark now as last action received time
		lastActionTime = Calendar.getInstance().getTime().getTime();
	}
	
	/**
	 * Join game first time
	 */
	public void joinGame(Faction faction, User user) throws SQLException, IllegalRequestException
	{
		// Join the game
		reJoinGame(faction, user);

		// Execute action
		execute(new ActionPlayerJoin(faction, user.id), true);

    	// Add to database
		GameStateDAO.joinGame(id, user.id, faction);

		// Make sure the text appears in the top bar
		clearPageTopCache();

		// Check if both players are in now
		if (players[0] != null && players[1] != null)
		{
			// Start timer
			resetTimeLeft();
			
			// Both players can start deploying
			sendMail(Faction.f1, "mail/PBMDeployUnits.jsp", "Deploy your units");
			sendMail(Faction.f2, "mail/PBMDeployUnits.jsp", "Deploy your units");
		}
	}
	
	/**
	 * Get faction of a particular user
	 * @param user User to get faction for
	 */
	public Faction getUserFaction(User user)
	{
		if (user != null && players[0] != null && players[0].id == user.id)
			return Faction.f1;
		else if (user != null && players[1] != null && players[1].id == user.id)
			return Faction.f2;
		else
			return Faction.none;		
	}
	
	/**
	 * Check a faction can make a move 
	 * @faction Faction to check
	 */
	public boolean canPerformAction(Faction faction)
	{
		if (faction == Faction.none)
		{
			return false;
		}			
		else if (turnNumber == 0)
		{
			return !playerReady[faction == Faction.f1? 0 : 1]
				&& winningFaction == Faction.invalid;
		}
		else
		{
			return currentPlayer == faction
				&& winningFaction == Faction.invalid;
		}
	}

	/**
	 * Class to hold previous state
	 */
	private static class OldState
	{
		public int turnNumber;
		public Faction winningFaction;
		public Faction currentPlayer;
		public boolean drawRequested[];
		
		public OldState(GameState state)
		{
			turnNumber = state.turnNumber;
			winningFaction = state.winningFaction;
			currentPlayer = state.currentPlayer;
			drawRequested = state.drawRequested.clone();
		}		
	}

	/**
	 * Execute an action
	 */
	public void execute(Action action, boolean addToDb) throws IllegalRequestException
	{
		// Remember old state
		OldState oldState = new OldState(this);
		
		// Update pending actions
		for (FilteredList l : filteredList)
			for (Action a : l.pendingList)
				a.pendingActionUpdate();
				
		// Set game state
		action.setGameState(this);
		
		// Perform the action
		action.doAction(addToDb);

		// Add to database
		if (addToDb)
		{
			actions += action.toString() + "\n";
			needsToBeWrittenToDb = true;
		}
		
		// Make sure action gets its first update
		action.pendingActionUpdate();
		
		// Add to filtered list
		addActionToFilteredList(action, Faction.f1);
		addActionToFilteredList(action, Faction.f2);
		addActionToFilteredList(action, Faction.none);
		
		// Get units that are no longer detected
		for (UnitState u : unitMap.values())
			if (u.checkDetectionLost())
			{
				u.confirmDetectionLost();
				
				execute(new ActionRemoveUnit(u.id), false);
				execute(new ActionCreateUnit(u.id, u.locationX, u.locationY, u.container != null? u.container.id : -1, u.faction, u.unitConfig.id, true), false);
			}			
		
		// Send properties for players if they just joined
		if (action instanceof ActionPlayerJoin)
			execute(new ActionPlayerProperties(((ActionPlayerJoin)action).getFaction()), false);

		// Add identification if a create unit was processed
		if (action instanceof ActionCreateUnit)
			execute(new ActionIdentifyUnit(((ActionCreateUnit)action).getUnitId()), false);
		
		// Optionally execute some other actions
		if (addToDb)
			postExecuteAction(oldState);
	}

	/**
	 * Get victory categories still available in the game (victory category 0 sets bit 0 etc.)
	 * 
	 * @param f Faction to test for
	 * @return Mask with bits set for which categories still exist
	 */
	public int getVictoryCategories(Faction f)
	{
		int categories = 0;
		
		for (UnitState u : units)
			if (u.faction == f)
				categories |= getVictoryCategoriesRecursive(u);
		
		return categories;
	}
	
	/**
	 * Recursive helper function
	 */
	public int getVictoryCategoriesRecursive(UnitState unit)
	{
		int categories = 1 << unit.unitConfig.victoryCategory;

		for (UnitState c : unit.containedUnits)
			categories |= getVictoryCategoriesRecursive(c);
		
		return categories;		
	}

	/**
	 * Determine if the game should start or end
	 */
	private void postExecuteAction(OldState oldState) throws IllegalRequestException
	{	
		// Your turn messages
		if (oldState.currentPlayer != currentPlayer)
			sendMail(currentPlayer, "mail/PBMYourTurn.jsp", "Your turn");
		
		// Draw request messages
		if (drawRequested[0] && !drawRequested[1] && drawRequested[0] != oldState.drawRequested[0])
			sendMail(Faction.f2, "mail/PBMDrawRequested.jsp", "Draw requested");
		if (drawRequested[1] && !drawRequested[0] && drawRequested[1] != oldState.drawRequested[1])
			sendMail(Faction.f1, "mail/PBMDrawRequested.jsp", "Draw requested");

		// Check if turn number / current player changed
		if (oldState.turnNumber != turnNumber 
			|| oldState.currentPlayer != currentPlayer)
		{
			// Mark in database
			needsToBeWrittenToDb = true;
		}
		
		// Check if this action triggered end game
		if (oldState.winningFaction != winningFaction)
		{
			// Mark in database
			needsToBeWrittenToDb = true;

			// Only valid games that are on a published map get their stats added
			if (players[0] != null 
				&& players[1] != null
				&& players[0].id != Constants.USER_ID_AI
				&& players[1].id != Constants.USER_ID_AI
				&& countryConfig.publishState == PublishState.published)
			{
				// Update stats
				for (int p = 0; p < 2; ++p)
				{
					int userId = players[p].id;
					
					// Determine if this player won / lost / drew
					Faction pf = p == 0? Faction.f1 : Faction.f2;
					boolean won = winningFaction == pf;
					boolean lost = winningFaction == Faction.opposite(pf);

					try
					{
						// Set user stats for this game
						UserStats s = new UserStats(userId);
						s.gamesPlayed = 1;
						s.gamesWon = won? 1 : 0;
						s.gamesLost = lost? 1 : 0;
						s.totalPoints = (int)((won? 2.0 : (lost? 0.5 : 1.0)) * scores[p].getTotalScore());
						
						// Accumulate totals
						UserStatsDAO.accumulateStats(s);
					}
					catch (SQLException e)
					{
						Tools.logException(e);
					}
						
					try
					{
						// Accumulate unit stats
						for (UnitStats u : unitStats[p].unitToStatsMap.values())
							UnitStatsDAO.accumulateUnitStats(userId, u);
					}
					catch (SQLException e)
					{
						Tools.logException(e);
					}
				}				
			}

			// End game messages
			sendGameResultMail(Faction.f1);
			sendGameResultMail(Faction.f2);
		}

		// Determine game start
		if (turnNumber == 0 && playerReady[0] && playerReady[1])
			execute(new ActionEndTurn(), true);
		
		// Determine winner
		if (turnNumber > 0 && winningFaction == Faction.invalid)
		{
			// Check score
			if (scores[0].getTotalScore() >= countryConfig.scoreLimit)
				execute(new ActionEndGame(Faction.f1), true);
			else if (scores[1].getTotalScore() >= countryConfig.scoreLimit)
				execute(new ActionEndGame(Faction.f2), true);
			
			// Check if all units in a victory category were destroyed 
			else if (initialVictoryCategories[0] != (getVictoryCategories(Faction.f1) & initialVictoryCategories[0]))
				execute(new ActionEndGame(Faction.f2), true);
			else if (initialVictoryCategories[1] != (getVictoryCategories(Faction.f2) & initialVictoryCategories[1]))
				execute(new ActionEndGame(Faction.f1), true);
			
			// Check pending draw requests
			else if (drawRequested[0] && drawRequested[1])
				execute(new ActionEndGame(Faction.none), true);
		}

		// Mark now as last action received time
		lastActionTime = Calendar.getInstance().getTime().getTime();
	}
	
	/**
	 * Get an Action list filtered for a specific faction
	 * 
	 * @param faction Receiving faction
	 */
	public FilteredList getFilteredList(Faction faction)
	{
		switch (faction)
		{
		case f1:
			return filteredList[0];
		case f2:
			return filteredList[1];
		default:
			return filteredList[2];
		}
	}
	
	/**
	 * Comparator for sorting units
	 */
	private static class SortOnPendingActionSortKey implements Comparator<Action>
	{
		 public int compare(Action a1, Action a2)
		 {
			 return a1.pendingActionGetSortKey() - a2.pendingActionGetSortKey();
		 }
	}
	
	/**
	 * Add action to filtered list
	 */
	private void addActionToFilteredList(Action action, Faction faction)
	{
		FilteredList list = getFilteredList(faction);
		
		switch (action.pendingActionGetReceiveTime(faction))
		{
		case now:
			// Check if previous actions need to be sent now
			processPendingList(faction);

			// Action accepted
			list.sendList.add(action.toString(faction));
			break;
			
		case later:
			// Action cannot yet be sent to remote
			list.pendingList.add(action);
			break;
			
		case never:
			// Do not add to list
			break;
		}
	}
	
	/**
	 * Search through the pending list for actions that need to be sent now
	 */
	private void processPendingList(Faction faction)
	{
		FilteredList list = getFilteredList(faction);

		// Search through pending list
		ArrayList<Action> executeNowList = new ArrayList<Action>();
		ArrayList<Action> executeNeverList = new ArrayList<Action>();
		for (Action a : list.pendingList)
			switch (a.pendingActionGetReceiveTime(faction))
			{
			case now:
				executeNowList.add(a);
				break;
				
			case never:
				executeNeverList.add(a);
				break;
			}
		
		// Remove actions in the remove list
		for (Action a : executeNeverList)
			list.pendingList.remove(a);
		
		// Sort pending actions so that actions that depend on other actions
		// are received last (units that are contained in a container)
		Action[] sortedList = executeNowList.toArray(new Action[0]);
    	Arrays.sort(sortedList, new SortOnPendingActionSortKey());
		for (Action a : sortedList)
		{
			list.pendingList.remove(a);
			list.sendList.add(a.toString(faction));
		}
	}
	
	/**
	 * Initialize the game
	 */
	public void initGame() throws IllegalRequestException, DeploymentFailedException
	{		
		// Create initial random deployment
		DeploymentHelper helper = new DeploymentHelper(random);
		ArrayList<UnitState> deployment = helper.getDeployment(countryConfig);
		for (UnitState u : deployment)
			deployUnit(u);
		
		// Initial time out
		resetTimeLeft();
	}

	/**
	 * Deploy a unit and its children
	 */
	private void deployUnit(UnitState u) throws IllegalRequestException
	{
		// Add unit
		execute(new ActionCreateUnit(u.id, u.locationX, u.locationY, u.container != null? u.container.id : -1, u.faction, u.unitConfig.id, false), true);
		
		// Add children
		for (UnitState c : u.containedUnits)
			deployUnit(c);
	}
	
	/**
	 * Determine initial victory categories that are present in the game
	 */
	public void determineInitialVictoryCategories()
	{
		initialVictoryCategories[0] = getVictoryCategories(Faction.f1);
		initialVictoryCategories[1] = getVictoryCategories(Faction.f2);
	}
	
	/**
	 * Unit has changed, check if identification state needs to be updated
	 */
	public void updateIdentification()
	{
		if (turnNumber > 0)
			for (UnitState unit : units)
			{
				boolean detected = false;
				boolean identified = unit.identifiedByEnemy;
	
				for (UnitState otherUnit : units)
					if (otherUnit.faction != unit.faction)
					{
						int distance = MapConfig.getDistance(unit.locationX, unit.locationY, otherUnit.locationX, otherUnit.locationY);
						if (distance <= unit.unitConfig.beDetectedRange)
						{
							detected = true;
							if (!countryConfig.fogOfWarEnabled)
							{
								identified = true;
								break;
							}
						}
						
						if (distance <= otherUnit.unitConfig.visionRange)
						{
							detected = true;
							identified = true;
							break;
						}
					}
				
				// Update detected state of unit
				if (!detected)
					unit.setDetectedRecursive(false, Integer.MAX_VALUE);
				else
					unit.setDetected(true);
				
				// Update identified state of unit
				if (identified)
				{
					unit.setDetectedRecursive(true, 2);
					unit.identifiedByEnemy = true;
				}
				else
					unit.identifiedByEnemy = false;
			}
	}
	
	/**
	 * Reset time left
	 */
	public void resetTimeLeft()
	{
		// Get time of this turn
		long timeThisTurn = 0;
		if (players[0] == null || players[1] == null)
		{
			// Game cannot last longer than an hour without two players
			timeThisTurn = 60L * 60L * 1000L;
		}
		else if (lobbyId != 0)
		{
			// Playing from a lobby
			Player player = getPlayer(currentPlayer);
			if (player != null && UserRank.getLevel(player.id) > Constants.HIGH_RANKED_USER_LEVEL)
				timeThisTurn = Constants.PLAY_LOBBY_TURN_TIME_HIGH_RANKED_USER;
			else
				timeThisTurn = Constants.PLAY_LOBBY_TURN_TIME;
		}
		else if (playByMail)
		{
			// Play by mail
			timeThisTurn = Constants.PLAY_BY_MAIL_TURN_TIME;
		}
		else
		{
			// Play live
			int numUnits = 0;
			for (UnitState u : getUnits())
				if (u.faction == currentPlayer)
					++numUnits;

			if (turnNumber == 0)
				timeThisTurn = Constants.GAME_SETUP_TIME;
			else
				timeThisTurn = Constants.GAME_MIN_TIME_PER_TURN + numUnits * Constants.GAME_TIME_EXTRA_PER_UNIT;
		}

		// Set time
		turnEndTime = Calendar.getInstance().getTime().getTime() + timeThisTurn;
		needsToBeWrittenToDb = true;
	}
	
	/**
	 * Get time left in turn
	 * 
	 * @return Time left in seconds or -1 for infinite
	 */
	public long getTimeLeft()
	{
		if (winningFaction != Faction.invalid)
			return -1;
		
		return Math.max(turnEndTime - Calendar.getInstance().getTime().getTime(), 0);
	}
	
	/**
	 * Check if turn has timed out and do according action
	 *  
	 * @throws IllegalRequestException
	 */
	public void checkTimeout() throws IllegalRequestException
	{
		if (getTimeLeft() == 0)
		{
			// Flag timeout
			execute(new ActionTimeOut(), true);

			if (turnNumber == 0)
			{
				if (getPlayer(Faction.f2) != null)
				{
					// Start the game
					execute(new ActionEndTurn(), true);
				}
				else
				{
					// Draw
					execute(new ActionEndGame(Faction.none), true);
				}
			}
			else if (currentPlayerIdle)
			{
				// Opposite faction is winner
				Faction winner = Faction.opposite(currentPlayer);
				execute(new ActionEndGame(winner), true);
			}
			else
			{
				// End turn for current player
				execute(new ActionEndTurn(), true);								
			}
		}
	}

	/**
	 * Mark that a player connected
	 */
	public void markPlayerConnected(Faction faction)
	{
		// Remember last communication time for player
		switch (faction)
		{
		case f1:
			lastCommunicationTime[0] = Calendar.getInstance().getTime().getTime();
			break;
		case f2:
			lastCommunicationTime[1] = Calendar.getInstance().getTime().getTime();
			break;
		}
	}
	
	/**
	 * Check if a specific player recently communicated with the cerver
	 */
	public boolean isPlayerConnected(Faction faction)
	{
		// AI is always connected
		Player player = getPlayer(faction);
		if (player != null && player.id == Constants.USER_ID_AI)
			return true;
		
		// Otherwise check last communication
		switch (faction)
		{
		case f1:
			return Calendar.getInstance().getTime().getTime() - lastCommunicationTime[0] < Constants.CLIENT_POLL_TIMEOUT;
		case f2:
			return Calendar.getInstance().getTime().getTime() - lastCommunicationTime[1] < Constants.CLIENT_POLL_TIMEOUT;
		default:
			return false;
		}
	}

	/**
	 * Send a mail indicating game results
	 */
	private void sendGameResultMail(Faction recipientFaction)
	{
		if (!playByMail)
			return;

		if (recipientFaction == winningFaction)
			sendMail(recipientFaction, "mail/PBMGameWon.jsp", "You won");
		else if (recipientFaction == Faction.opposite(winningFaction))
			sendMail(recipientFaction, "mail/PBMGameLost.jsp", "You lost");
		else
			sendMail(recipientFaction, "mail/PBMGameDraw.jsp", "Game is a draw");
	}
	
	/**
	 * Send e-mail to player player
	 */
	public void sendMail(Faction recipientFaction, String templateJsp, String title)
	{
		// Check if this is a play by mail game
		if (!playByMail)
			return;
		
		try
		{
			// Determine sender and recicpient
			Faction senderFaction = Faction.opposite(recipientFaction);
			User sender = getPlayer(senderFaction).getUser();
			User recipient = getPlayer(recipientFaction).getUser();
			
			// Check if recipient wants email
			if (!recipient.getHasEmail() || !recipient.receiveGameEventsByMail)
				return;
			
	    	// Determine parameters
	    	Mailer.Mail mail = new Mailer.Mail();
	    	mail.params.put("p1Name", getPlayer(Faction.f1).getUser().username);
	    	mail.params.put("p2Name", getPlayer(Faction.f2).getUser().username);
	    	mail.params.put("p1Score", Integer.toString(scores[0].getTotalScore()));
	    	mail.params.put("p2Score", Integer.toString(scores[1].getTotalScore()));
	    	mail.params.put("countryName", countryConfig.name);
	    	mail.params.put("targetScore", Integer.toString(countryConfig.scoreLimit));
	    	int s = Faction.toInt(senderFaction) - 1;
	    	mail.params.put("unitsDestroyed", Integer.toString(scores[s].numberOfUnitsDestroyed - previousTurnScores[s].numberOfUnitsDestroyed));
	    	mail.params.put("basesDestroyed", Integer.toString(Math.max(scores[s].numberOfBasesDestroyed - previousTurnScores[s].numberOfBasesDestroyed, 0)));
	    	mail.params.put("tilesConquered", Integer.toString(scores[s].numberOfTilesOwned - previousTurnScores[s].numberOfTilesOwned));
	    	mail.params.put("link", GlobalProperties.getInstance().getString("app.url") + "/GamePlay.do" + "?gameId=" + id);
	    	
	    	// Get sender address, the sender might not have filled in an e-mail address at all in which case sender.getEmailAsInternetAddress() will return null
	    	InternetAddress senderAddress = new InternetAddress();
	    	senderAddress.setPersonal(sender.getFriendlyName(), "UTF-8");
	    	
			// Send mail
	    	mail.sender = senderAddress;
	    	mail.recipient = recipient.getEmailAsInternetAddress();
	    	mail.templateJsp = templateJsp;
	    	mail.title = title;
			Mailer.getInstance().send(mail);
		}
		catch (Exception e)
		{			
			Tools.logException(e);
		}
	}
	
	/**
	 * Calculate time between two polls from the client
	 * 
	 * @return Delta time in milliseconds
	 */
	public long getTimeBetweenPolls()
	{
		long time = Calendar.getInstance().getTime().getTime();

		return Constants.MIN_DELAY_BETWEEN_CLIENT_POLLS + ((time - lastActionTime) * (Constants.MAX_DELAY_BETWEEN_CLIENT_POLLS - Constants.MIN_DELAY_BETWEEN_CLIENT_POLLS)) / GameStateCache.TIME_OUT; 
	}
	
	/**
	 * Accumulate stats for an attack to put in the db at the end of the game
	 * 
	 * @param attackerFaction Attacking faction
	 * @param attackerUnitConfigId Attacker unit config id
	 * @param defenderUnitConfigId Defender unit config id
	 * @param attackerDeaths Amount of units killed on attacking side (can be > 1 if unit contains other units)
	 * @param defenderDeaths Amount of units killed on defending side (can be > 1 if unit contains other units)
	 * @param attackerDamageDealt Amount of damage dealt by attacker
	 * @param defenderDamageDealt Amount of damage dealt by defender
	 */
	public void accumulateAttackStats(Faction attackerFaction, int attackerUnitConfigId, int defenderUnitConfigId, int attackerDeaths, int defenderDeaths, int attackerDamageDealt, int defenderDamageDealt)
	{
		int attackerInt = Faction.toInt(attackerFaction) - 1;

		UnitStats au = unitStats[attackerInt].getOrCreate(attackerUnitConfigId);
		UnitStats du = unitStats[attackerInt ^ 1].getOrCreate(defenderUnitConfigId);
		
		au.numAttacks++;
		du.numDefends++;
		
		if (attackerDeaths > 0)
		{
			du.kills += attackerDeaths;
			au.deaths += attackerDeaths;
		}

		if (defenderDeaths > 0)
		{
			au.kills += defenderDeaths;
			du.deaths += defenderDeaths;
		}
		
		au.damageDealt += attackerDamageDealt;
		du.damageDealt += defenderDamageDealt;
		
		au.damageReceived += defenderDamageDealt;
		du.damageReceived += attackerDamageDealt;
	}
	
	/**
	 * Update game state in database
	 * 
	 * @throws SQLException
	 */
	public void updateDb() throws SQLException
	{
		if (needsToBeWrittenToDb)
		{
			GameStateDAO.update(this);
			needsToBeWrittenToDb = false;
		}
	}
	
	/**
	 * Clear cache for notification bar
	 */
	public void clearPageTopCache()
	{
		for (Player p : players)
			if (p != null)
				CacheTag.purgeElement("top", null, p.id);
	}
}

package onlinefrontlines.game
{
	import mx.core.Application;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.net.*;
	import flash.utils.*;
	import com.adobe.crypto.MD5;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Current state for active game
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
 	public class GameState
	{
		/********* BEGIN SYNCHRONIZED BLOCK BETWEEN FLASH AND JAVA *********/
		
		/**
		 * Id of the game
		 */
		public var id : int = -1;

		/**
		 * The players that are playing this game
		 */
		private var players : Array = [ null, null ]; // of type Player
		
		/**
		 * Indicate which players are ready
		 */
		public var playerReady : Array = [ false, false ];
		
		/**
		 * If faction 1 starts or 2
		 */
		public var faction1Starts : Boolean;
		
		/**
		 * Faction of current player
		 */
		public var currentPlayer : int;

		/**
		 * Number of turns played
		 */
		public var turnNumber : int = 0;	
		
		/**
		 * Time when the current turn ends
		 */
		public var turnEndTime : int = -1;
		
		/**
		 * Initial terrain owners, to calculate score
		 */
		public var initialTerrainOwners : Array;
	
		/**
		 * Score objects
		 */
		public var scores : Array = [ new Score(), new Score() ];
		
		/**
		 * Players that requested a draw
		 */
		public var drawRequested : Array = [ false, false ];
		
		/**
		 * If the game has ended this will indicate the winning faction
		 */
		public var winningFaction : int = Faction.invalid;
	
		/**
		 * All units
		 */
		private var units : Array = new Array();
		
		/**
		 * Quick access to a unit by its location
		 */
		private var unitGrid : Array; // of Array of UnitState (equal to map grid size)
		
		/**
		 * Quick access to a unit
		 */
		private var unitMap : Object = new Object(); // Map<Integer, UnitState> (unit id to unit reference)
		
		/**
		 * Get a player
		 */
		public function getPlayer(faction : int) : Player
		{
			switch (faction)
			{
			case Faction.f1:
				return players[0];
			case Faction.f2:
				return players[1];
			default:
				return null;
			}
		}

		/**
		 * Get player user id
		 */
		public function getPlayerId(faction : int) : int
		{
			var player : Player = getPlayer(faction);
			return player != null? player.id : -1;
		}

		/**
		 * Get score object
  	  	 */
		public function getScore(faction : int) : Score
		{
			switch (faction)
			{
			case Faction.f1:
				return scores[0];
			case Faction.f2:
				return scores[1];
			default:
				return null;
			}
		}
		
		/**
		 * Join game
		 */
		public function reJoinGame(faction : int, player : Player) : void
		{
			players[faction == Faction.f1? 0 : 1] = player;
		}

		/**
		 * Register unit with this game 
		 */
		public function registerUnit(unit : UnitState) : void
		{
			unitMap[unit.id] = unit;
		}
		
		/**
		 * Unregister unit with this game
		 */
		public function unregisterUnit(unit : UnitState) : void
		{
			unitMap[unit.id] = null;
		}

		/**
		 * Find a unit by its id
		 */
		public function getUnitById(id : int) : UnitState 
		{
			return unitMap[id];
		}

		/**
		 * Add unit at particular location
		 */
		public function addUnit(unit : UnitState) : void
		{
			// Add to grid and array
			unitGrid[unit.locationX][unit.locationY] = unit;
			units.push(unit);
		}
		
		/**
		 * Remove a unit
		 */
		public function removeUnit(unit : UnitState) : void
		{
			// Remove from grid and array
			unitGrid[unit.locationX][unit.locationY] = null;
			var i : int = units.indexOf(unit);
			units.splice(i, 1);
		}
				
		/**
		 * Get unit at particular location
		 */
		public function getUnit(x : int, y : int) : UnitState
		{ 
			return unitGrid[x][y];
		}
		
		/**
		 * Get all units
		 */
		public function getUnits() : Array
		{
			return units;
		}
		
		/**
		 * Get terrain at (x, y)
		 */
		public function getTerrainAt(x : int, y : int) : TerrainConfig
		{
			var tile : int = mapConfig.getTile(x, y).getTerrainImage();
			return TerrainConfig.allTerrainMap[tile];
		}
		
		/**
		 * Get terrain unit is on
		 */
		public function getTerrain(unit : UnitState) : TerrainConfig
		{
			return unit.container == null? getTerrainAt(unit.locationX, unit.locationY) : getTerrain(unit.container);
		}	

		/**
		 * Returns terrain owner at x, y
		 */
		public function getTerrainOwnerAt(x : int, y : int) : int
		{
			return mapConfig.getTile(x, y).getOwnerFaction();
		}
	
		/**
		 * Sets terrain owner at x, y
	 	 */
		public function setTerrainOwnerAt(x : int, y : int, newOwner : int) : void
		{
			var initialOwner : int = getInitialTerrainOwnerAt(x, y);
			var oldOwner : int = getTerrainOwnerAt(x, y);
			
			mapConfig.getTile(x, y).setOwnerFaction(newOwner);

			if (newOwner != oldOwner)
			{
				var victoryPoints : int = getTerrainAt(x, y).victoryPoints;
				if (victoryPoints > 0)
				{
					var score : Score; 
	
					// Award points if tile captured				
					if (newOwner != initialOwner)
					{
						score = getScore(newOwner);
						if (score != null)
						{
							score.numberOfTilesOwned++;
							score.victoryPointsForTiles += victoryPoints;
						}
					}
					
					// Deduct points if tile is lost again
					if (oldOwner != initialOwner)
					{
						score = getScore(oldOwner);
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
		public function updateTerrainOwner(unit : UnitState) : void
		{
			if (unit.armour > 0
				&& unit.unitConfig.unitClass == UnitClass.land
				&& getTerrain(unit).victoryPoints > 0)
				setTerrainOwnerAt(unit.locationX, unit.locationY, unit.faction);
		}
		
		/**
		 * Calculate initial terrain state
		 */
		public function calculateInitialTerrainOwners() : void
		{
			initialTerrainOwners = new Array(mapConfig.sizeX * mapConfig.sizeY);
			
			// Loop through tiles
			for (var x : int = 0; x < mapConfig.sizeX; ++x)
				for (var y : int = 0; y < mapConfig.sizeY; ++y)
				{
					// Reset owner if this type of terrain doesn't give any points
					var terrain : TerrainConfig = getTerrainAt(x, y);
					if (terrain.victoryPoints <= 0)
						setTerrainOwnerAt(x, y, Faction.none);
					
					// Store owner
					initialTerrainOwners[x + y * mapConfig.sizeX] = getTerrainOwnerAt(x, y);
				}
		}
		
		/**
		 * Returns terrain owner at x, y when the game started
		 */
		public function getInitialTerrainOwnerAt(x : int, y : int) : int
		{
			return initialTerrainOwners[x + y * mapConfig.sizeX];
		}
	
		/**
		 * Get terrain unit is on (used for attacking, with special case for air)
		 */
		public function getAttackTerrain(unit : UnitState) : TerrainConfig
		{
			// Flying units are always in the air
			if (unit.unitConfig.unitClass == UnitClass.air)
				return TerrainConfig.airTerrain;
				
			// Otherwise look for the tile we're on
			return getTerrain(unit);
		}	

		/**
		 * Check if a unit can be set up on tile x, y
		 */
		public function canUnitBeTeleportedTo(unitState : UnitState, x : int, y : int) : Boolean
		{
			// Check if base is too close to another base
			if (unitState.unitConfig.isBase)
				for each (var u : UnitState in units)
					if (u != unitState
						&& u.faction == unitState.faction
						&& u.unitConfig.isBase
						&& u.getDistanceTo(x, y) <= 2)
						return false;
			
			return canUnitBeSetupOnHelper(unitState.initialUnitConfig, unitState.faction, x, y);
		}

		/**
		 * Check if a unit can be deployed from a base on tile x, y
		 */
		public function canUnitBeDeployedOn(unitConfig : UnitConfig, x : int, y : int) : Boolean
		{
			if (mapConfig.isTileInPlayableArea(x, y)
				&& getUnit(x, y) == null)
			{
				var otherConfig : UnitConfig = UnitConfig.allUnitsMap[unitConfig.transformableToUnitId];
				var terrain : TerrainConfig = getTerrainAt(x, y);
				if (unitConfig.canMoveOn(terrain) 
					|| (unitConfig.transformableType != TransformableType.inBase && otherConfig != null && otherConfig.canMoveOn(terrain)))
					return true;
			}
			
			return false;
		}
	
		/**
		 * Check if a unit can be set up on on tile x, y
		 */
		private function canUnitBeSetupOnHelper(unitConfig : UnitConfig, faction : int, x : int, y : int) : Boolean
		{
			// Unit must be in playable area and the terrain must be owned by the correct faction
			if (!mapConfig.isTileInPlayableArea(x, y)
				|| getTerrainOwnerAt(x, y) != faction)
				return false;
				
			// If unit needs to be set up next to a specific terrain, validate it
			if (unitConfig.unitSetupNextTo.length > 0)
			{
				var found : Boolean = false;
				for each (var n : Object in mapConfig.getNeighbours(x, y))
					if (unitConfig.unitSetupNextTo.indexOf(getTerrainAt(n.x, n.y).id) >= 0)
					{
						found = true;
						break;
					}
				if (!found)
					return false;
			}
	
			// If unit can move here
			var terrain : TerrainConfig = getTerrainAt(x, y);
			if (unitConfig.canMoveOn(terrain))
				return true;
			
			// If terrain is in the list where the unit can be set up on
			if (unitConfig.unitSetupOn.indexOf(terrain.id) >= 0)
				return true;
			
			return false;			
		}
		
		/**
		 * Check if a unit can be transformed in it's current location
		 */
		public function canUnitBeTransformed(unit : UnitState) : Boolean
		{
			if (unit.containedUnits.length != 0)
				return false;
				
			var otherConfig : UnitConfig = UnitConfig.allUnitsMap[unit.unitConfig.transformableToUnitId];					
			
			switch (unit.unitConfig.transformableType)
			{
			case TransformableType.inBase:
				return unit.container != null
					&& unit.container.unitConfig.containerUnitIds.indexOf(otherConfig.id) >= 0;
				
			case TransformableType.moveOneTile:
				if (!otherConfig.canMoveOn(getTerrain(unit)))
				{
					if (unit.container == null 
						&& unit.actionsLeft > 0)
						for each (var n : Object in mapConfig.getNeighbours(unit.locationX, unit.locationY))
							if (getUnit(n.x, n.y) == null
								&& mapConfig.isTileInPlayableArea(n.x, n.y)
								&& otherConfig.canMoveOn(getTerrainAt(n.x, n.y)))
								return true;
					return false;
				}
				
				// Note: Same code as for case TransformableType.onSpot
				// A fallthrough construction could have been used here, but this seems
				// to screw up Flash Player 9,0,115,0 Release which stops the program from running
				// on entering this function. The Debug Flash Player works fine b.t.w.
				return unit.container == null 
					&& unit.actionsLeft > 0 
					&& otherConfig.canMoveOn(getTerrain(unit));
				
			case TransformableType.onSpot:
				return unit.container == null 
					&& unit.actionsLeft > 0 
					&& otherConfig.canMoveOn(getTerrain(unit));
				
			default:
				return false;
			}
		}		
		
		/**
		 * Move unit to new location
		 */
		public function moveUnit(unit : UnitState, x : int, y : int) : void
		{
			unitGrid[unit.locationX][unit.locationY] = null;
			
			unit.locationX = x;
			unit.locationY = y;
			
			unitGrid[x][y] = unit;
		}
		
		/**
		 * Move unit into container unit
		 */
		public function moveUnitInContainer(unit : UnitState, container : UnitState) : void
		{
			// Remove from grid
			removeUnit(unit);
			
			// Add to container
			container.addUnit(unit);
		}
		
		/**
		 * Remove unit from container
		 */
		public function moveUnitOutOfContainer(unit : UnitState, container : UnitState, x : int, y : int) : void
		{
			// Remove from container
			container.removeUnit(unit);
			
			// Place on new position
			unit.locationX = x;
			unit.locationY = y;
			
			// Add to grid
			addUnit(unit);
		}
		
		/**
		 * Get movement cost when unit moves (1 tile) to new location
		 */
		public function getMovementCost(unit : UnitState, x : int, y : int) : int
		{
			// Get terrain
			var terrain : TerrainConfig = getTerrainAt(x, y);

			// Modify terrain under a base to type that unit supports
			var base : UnitState = getUnit(x, y);
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
		public function setWinningFaction(faction : int) : void
		{
			// Mark game as ended
			winningFaction = faction;
	
			// Need to refresh the ui to get rid of the fog of war
			uiRefreshNeeded = true;		
		}
		
		/**
		 * If user can request draw or surrender (to prevent cheating)
		 */
		public function userCanTerminateGame() : Boolean
		{
			return scores[0].getTotalScore() > 100 || scores[1].getTotalScore() > 100;
		}

		/**
		 * Dump state of the game for comparison
		 * 
		 * @param localFaction The faction that the state should be simulated for
		 */
		public function dumpState(localFaction : int) : String
		{
			// Dump general properties
			var rv : String = "turnNumber = " + turnNumber + "\n"
						+ "currentPlayer = " + currentPlayer + "\n";
			
			var x : int, y : int;
			
			// Dump initial terrain ownership
			rv += "initialTerrainOwner =\n";
			for (y = 0; y < mapConfig.sizeY; ++y)
			{
				rv += getInitialTerrainOwnerAt(0, y);
				for (x = 1; x < mapConfig.sizeX; ++x)
					rv += ", " + getInitialTerrainOwnerAt(x, y);
				rv += "\n";
			}
			
			// Dump terrain ownership
			rv += "terrainOwner =\n";
			for (y = 0; y < mapConfig.sizeY; ++y)
			{
				rv += getTerrainOwnerAt(0, y);
				for (x = 1; x < mapConfig.sizeX; ++x)
					rv += ", " + getTerrainOwnerAt(x, y);
				rv += "\n";
			}
			
			// Dump scores
			for (var i : int = 0; i < 2; ++i)
				rv += "scores[" + i + "] = " + scores[i].dumpState() + "\n";
	
			// Dump units
			rv += "units =\n";
			for (y = 0; y < mapConfig.sizeY; ++y)
				for (x = 0; x < mapConfig.sizeX; ++x)
				{
					var unit : UnitState = getUnit(x, y);
					if (unit != null)
						rv += unit.dumpState(localFaction, 0);
				}
			
			return rv;
		}
	
		/********* END SYNCHRONIZED BLOCK BETWEEN FLASH AND JAVA *********/

		// Sounds
		[Embed(source="../../assets/sounds/request_draw.mp3")]
		private static var requestDrawClass : Class;

		// Images
		[Embed(source="../../assets/info_panel/player_red.png")]
		private static var playerRedClass : Class;
		[Embed(source="../../assets/info_panel/player_red_hi.png")]
		private static var playerRedHiClass : Class;
		[Embed(source="../../assets/info_panel/player_blue.png")]
		private static var playerBlueClass : Class;
		[Embed(source="../../assets/info_panel/player_blue_hi.png")]
		private static var playerBlueHiClass : Class;

		/**
		 * Constant texts
		 */		
		private const TEXT_NOTIFICATIONS_ON : String = "You have turned on e-mail notifications so you will receive an e-mail when it is your turn again.";
		private const TEXT_NOTIFICATIONS_OFF : String = "You do not have e-mail notifications turned on, so you will have to check back regularly under Play Now and Continue Game to see if it is your turn.";

		// Attack states
		public static const noAttackPossible : int = 0;
		public static const attackPossibleAfterMove : int = 1;
		public static const attackPossible : int = 2;
		
		// Move states
		public static const noMovePossible : int = -1000;
		
		/**
		 * Settings for the game
		 */
		public var mapConfig : MapConfig;
		public var scoreLimit : int;
		public var fogOfWarEnabled : Boolean;
		public var lobbyId : int;
		public var playByMail : Boolean;
		public var receiveGameEventsByMail : Boolean;

		/**
		 * Information on who is the local player
		 */
		public var isLocalGame : Boolean;
		public var localPlayer : int;
		
		/**
		 * Action bookkeeping
		 */
		private var executedActions : Array = new Array(); // of Action
		private var undoneActions : Array = new Array(); // of Action
		private var numActionsReceived : int = 0;
		private var pendingActions : Array = new Array(); // of Action
		private var loading : Boolean = false;
		private var sendingAction : Boolean = false;
		private var nextLoadTime : int;
		private static var minTimeBetweenPolls : int = 3000;
		private var timeBetweenPolls : int = minTimeBetweenPolls;
		private var dumpStateRequested : Boolean = false;
		
		/**
		 * User interface state
		 */
		private var playbackEnabled : Boolean = true;
		private var currentUIState : UIState;
		private var uiRefreshNeeded : Boolean = true;
		private var hoverOverUnits : Array = new Array();
		private var strengthText : Array = [ null, null, null ];
		private var rangeText : Array = [ null, null, null ];
		private var maxMovementText : BitmapText;
		private var actionsLeftText : BitmapText;
		private var visionText : BitmapText;
		private var ammoText : BitmapText;
		private var playerConnected : Array = [ true, true ];
		public var hasTeleportedUnit : Boolean = false;
		public var hasTeleportedBase : Boolean = false;
		public var anyUnitCanPerformAction : Boolean = true;

		/**
		 * Constructor
		 */
		public function GameState(mapConfig : MapConfig, faction1Starts : Boolean, receiveGameEventsByMail : Boolean) : void
		{
			this.mapConfig = mapConfig;
			this.faction1Starts = faction1Starts;
			this.receiveGameEventsByMail = receiveGameEventsByMail;
			
			// Determine if this is a local game or not
			if (Application.application.parameters.localPlayer != null)
			{
				isLocalGame = false;
				localPlayer = int(Application.application.parameters.localPlayer);
			}
			else
			{
				isLocalGame = true;
				localPlayer = Faction.f1;
			}
			
			// Starting the game will first toggle the faction so currentPlayer starts at the other faction
			currentPlayer = faction1Starts? Faction.f2 : Faction.f1;
			
			// Create grid
			unitGrid = Tools.createArray(mapConfig.sizeX, mapConfig.sizeY, null);

			// Create status test
			strengthText[0] = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			strengthText[0].setPosition(46, 107);
			strengthText[1] = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			strengthText[1].setPosition(67, 107);
			strengthText[2] = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			strengthText[2].setPosition(88, 107);
			rangeText[0] = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			rangeText[0].setPosition(46, 123);
			rangeText[1] = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			rangeText[1].setPosition(67, 123);
			rangeText[2] = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			rangeText[2].setPosition(88, 123);
			maxMovementText = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			maxMovementText.setPosition(67, 139);
			actionsLeftText = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			actionsLeftText.setPosition(67, 155);
			visionText = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			visionText.setPosition(67, 171);
			ammoText = new BitmapText(Application.application.infoPanel, 3, 0, BitmapText.typeMenu);
			ammoText.setPosition(67, 187);
			
			// Init timer
			nextLoadTime = getTimer();
		}
				
		/**
		 * Enable / disable user interface
		 */
		public function enable(enable : Boolean) : void
		{
			mapConfig.enabled = enable;
			
			if (enable)
				updateUI();
		}
		
		/**
		 * Check if it is a players turn
		 */
		public function isPlayerTurn(p : int) : Boolean
		{
			if (turnNumber == 0)
				return !playerReady[p];
			else
				return currentPlayer == p + 1;
		}
		
		/**
		 * Get color for name
		 */
		public function getColorForName(p : int) : int
		{
			if (playerConnected[p])
				return isPlayerTurn(p)? 0xffffff : 0x7f7f7f;
			else
				return 0xff0000;
		}
		
		/**
		 * Check if player is connected
		 */
		public function isPlayerConnected(p : int) : int
		{
			return playerConnected[p];
		}
		
		/**
		 * Get image class belonging to player
		 */
		public function getColorClass(p : int) : Class
		{
			var red : Boolean = (p == 0) == Faction.faction1IsRed;
			if (isPlayerTurn(p))
				return red? playerRedHiClass : playerBlueHiClass;
			else
				return red? playerRedClass : playerBlueClass;
		}		
		
		/**
		 * Update UI state
		 */
		public function updateUI() : void
		{
			Application.application.navBeginButton.visible = executedActions.length != 0;
			Application.application.navTurnBackButton.visible = executedActions.length != 0;
			Application.application.navBackButton.visible = executedActions.length != 0;
			Application.application.navForwardButton.visible = undoneActions.length != 0;
			Application.application.navTurnForwardButton.visible = undoneActions.length != 0;
			Application.application.navEndButton.visible = undoneActions.length != 0;
			Application.application.endTurnButton.visible = canPerformAction() && !sendingAction;
			Application.application.toLobbyButton.visible = winningFaction != Faction.invalid && lobbyId != 0;
			if (loading)
				Application.application.throbber.show();
			else
				Application.application.throbber.hide();
			Application.application.scoreP1.text = getScore(Faction.f1).getTotalScore();
			Application.application.scoreP2.text = getScore(Faction.f2).getTotalScore();
			Application.application.playerColor1.source = getColorClass(0);
			Application.application.playerColor2.source = getColorClass(1);
			Application.application.playerName1.setStyle("color", getColorForName(0));
			Application.application.playerName2.setStyle("color", getColorForName(1));
			Application.application.scoreP1.setStyle("color", getColorForName(0));
			Application.application.scoreP2.setStyle("color", getColorForName(1));
			
			var canTerm : Boolean = userCanTerminateGame(); 
			
			if (localPlayer == Faction.none)
			{
				Application.application.drawButton.enabled = false;
				Application.application.surrenderButton.enabled = false;
			}
			else if (winningFaction != Faction.invalid)
			{
				Application.application.drawButton.enabled = false;
				Application.application.surrenderButton.enabled = false;
			}			
			else if (turnNumber == 0)
			{
				Application.application.endTurnButton.label = "Start Game";
				Application.application.drawButton.enabled = canTerm;
				Application.application.surrenderButton.enabled = canTerm;
			}
			else
			{
				Application.application.endTurnButton.label = "End Turn";
				Application.application.drawButton.enabled = canTerm;
				Application.application.surrenderButton.enabled = canTerm;
			}
			
			if (turnNumber == 0)
				Application.application.currentRoundText.text = "-";
			else
				Application.application.currentRoundText.text = turnNumber.toString();
			
			Application.application.feedbackButton.enabled = localPlayer != Faction.none && players[0] != null && players[1] != null;

			if (Application.application.toLobbyButton.visible)
				HelpBalloon.queueCenterOnDisplayObject(Application.application.toLobbyButton, "The game has ended, return to the continent map by pressing on this button.", true);
		}		

		/**
		 * Get local player
		 */
		public function getLocalPlayer() : Player
		{
			return getPlayer(localPlayer);
		}
		
		/**
		 * Get local player id
		 */
		public function getLocalPlayerId() : int
		{
			var player : Player = getPlayer(localPlayer);
			return player != null? player.id : -1;
		}
		
		/**
		 * Count number of units still alive for one faction
		 * 
		 * @param f Faction to test units for 
		 * @return Amount of units (not bases)
		 */
		public function getUnitCount(f : int) : int
		{
			return getUnitCountRecursive(f, units);
		}
		
		/**
		 * Recursive helper function for the function above
		 */
		private function getUnitCountRecursive(f : int, units : Array) : int
		{
			var count : int = 0;
		
			for each (var u : UnitState in units)
				if (u.faction == f)
				{
					 if (!u.unitConfig.isBase)
						++count;
					
					count += getUnitCountRecursive(f, u.containedUnits);
				}
			
			return count;
		}
		
		/**
		 * Count number of bases that belong to a particular faction
		 * 
		 * @param f Faction to test for
		 * @return Returns number of bases
		 */
		public function getBaseCount(f : int) : int
		{
			var count : int = 0;
			
			for each (var u : UnitState in units)
				if (u.faction == f && u.unitConfig.isBase)
					++count;
			
			return count;
		}

		/**
		 * Get unit on a particular tile
		 */
		public function getUnitOnTile(tile : MapTile) : UnitState
		{
			return unitGrid[tile.locationX][tile.locationY];
		}
		
		/**
		 * Check if unit can still perform an action
		 */
		public function canUnitPerformAction(unit : UnitState) : Boolean
		{
			// Test if unit is owned by us, it is alive and not in a container
			if (unit.faction != currentPlayer
				|| unit.container != null
				|| unit.armour <= 0)
				return false;
				
			// Test if unit can deploy other unit	
			for each (var c : UnitState in unit.containedUnits)
				if (c.actionsLeft > 0)
					for each (var l : Object in mapConfig.getNeighbours(unit.locationX, unit.locationY))
						if (canUnitBeDeployedOn(c.unitConfig, l.x, l.y))
							return true;
							
			// Test if unit can be transformed
			if (canUnitBeTransformed(unit))
				return true;
		
			// Check if unit can attack from current location
			var other : UnitState;	
			if (unit.actionsLeft > 0)	
				for each (other in getUnits())
					if (unit.canAttack(other))
						return true;
								
			// Check if unit can move
			if (unit.movementPointsLeft > 0 
				&& (unit.actionsLeft > 0 || unit.lastActionWasMove)
				&& determineMovementPossibleRecursive(new Object(), unit, unit.locationX, unit.locationY, unit.movementPointsLeft))
					return true;
					
			return false;
		}
						
		/**
		 * Determine if any movement is possible
		 */
		private function determineMovementPossibleRecursive(movementPoints : Object, unit : UnitState, x : int, y : int, points : int) : Boolean
		{
			// Check if any movement points left
			if (points < 0)
				return false;
				
			// Check if we got here via a shorter or similar length path
			var offset : int = y * mapConfig.sizeX + x;
			if (movementPoints[offset] != null && movementPoints[offset] >= points)
				return false;

			// Check if in playable area
			if (!mapConfig.isTileInPlayableArea(x, y))
				return false;
				
			var other : UnitState = getUnit(x, y);
			if (other != null)
			{
				// Cannot pass through units of opposite faction
				if (other.faction != unit.faction)
					return false;
					
				// Can enter this container?
				if (other.canHold(unit))
					return true;
			}
			else
			{
				// We can move here, we're done
				return true;
			}

			// Store this as best path so far
			movementPoints[offset] = points;

			// Recurse to neighbours
			for each (var n : Object in mapConfig.getNeighbours(x, y))
			{
				// Get cost for this move
				var cost : int = getMovementCost(unit, n.x, n.y);
					
				// Recurse
				if (determineMovementPossibleRecursive(movementPoints, unit, n.x, n.y, points - cost))
					return true;
			}
			
			return false;
		}

		/**
		 * Calculates area where unit can move. Area is indicated with 2 dimensional array
		 * where area you can move to is >= 0
		 */
		public function calculateMovementArea(unit : UnitState) : Array
		{
			// Calculate movement range for unit
			var movementPoints : Array = Tools.createArray(mapConfig.sizeX, mapConfig.sizeY, noMovePossible);
			calculateMovementAreaRecursive(movementPoints, unit, unit.locationX, unit.locationY, unit.movementPointsLeft);
			return movementPoints;
		}
		
		/**
		 * Helper function for the function above
		 */
		private function calculateMovementAreaRecursive(movementPoints : Array, unit : UnitState, x : int, y : int, points : int) : void
		{
			// Check if any movement points left
			if (points < 0)
				return;
				
			// Check if we got here via a shorter or similar length path
			if (movementPoints[x][y] >= points)
				return;

			// Check if in playable area
			if (!mapConfig.isTileInPlayableArea(x, y))
				return;
				
			// Cannot pass through units of opposite faction
			var other : UnitState = getUnit(x, y);
			if (other != null && other.faction != unit.faction)
				return;

			// Store this as best path so far
			movementPoints[x][y] = points;

			// Recurse to neighbours
			for each (var n : Object in mapConfig.getNeighbours(x, y))
			{
				// Get cost for this move
				var cost : int = getMovementCost(unit, n.x, n.y);
					
				// Recurse
				calculateMovementAreaRecursive(movementPoints, unit, n.x, n.y, points - cost)
			}
		}

		/**
		 * Calculate attack possibilities, given unit and his movement area
		 */
		public function calculateAttackPossibilities(unit : UnitState, movementPoints : Array) : Array
		{
			var other : UnitState;

			// Start with clean array
			var attackPossibilities : Array = Tools.createArray(mapConfig.sizeX, mapConfig.sizeY, noAttackPossible);

			// Determine attack possibilities after move
			if (unit.actionsLeft > 1 || (unit.actionsLeft > 0 && unit.movementPointsLeft > 0 && unit.lastActionWasMove))
				for (var x : int = 0; x < mapConfig.sizeX; ++x)
					for (var y : int = 0; y < mapConfig.sizeY; ++y)
					{
						// If we can move here
						if (movementPoints[x][y] >= 0)
						{
							// If there is no other unit already here
							other = getUnit(x, y);
							if (other == null || unit == other)
							{
								// Check for any attackable units from this location
								for each (var target : UnitState in getUnits())
									if (unit.canAttackFrom(x, y, target))
										attackPossibilities[target.locationX][target.locationY] = attackPossibleAfterMove;
							}
						}
					}
				
			// Determine direct attack possibilities
			if (unit.actionsLeft > 0)
				for each (other in getUnits())
					if (unit.canAttack(other))
						attackPossibilities[other.locationX][other.locationY] = attackPossible;
						
			return attackPossibilities;
		}
		
		/**
		 * Check if it is our turn
		 */
		public function canPerformAction() : Boolean
		{
			if (localPlayer == Faction.none)
			{
				return false;
			}			
			else if (turnNumber == 0)
			{
				return !playerReady[localPlayer == Faction.f1? 0 : 1]
					&& winningFaction == Faction.invalid;
			}
			else
			{
				return currentPlayer == localPlayer
					&& undoneActions.length == 0
					&& winningFaction == Faction.invalid;
			}
		}
		
		/**
		 * Request an action to be executed
		 */
		public function request(action : Action) : void
		{			
			if (isLocalGame)
			{
				execute(action, true);
			}
			else
			{
				pendingActions.push(action);
				
				if (action.resetStateAfterRequest())
					toState(null);
			}
		}

		/**
		 * Start loading
		 */
		private function startLoad() : void
		{
			// Get next action
			var requestString : String = "";
			if (pendingActions.length > 0)
			{
				requestString = pendingActions[0].toString();
				pendingActions.splice(0, 1);
			}
			
			// Log what we're about to do
			Logger.log("startLoad() - requestString: '" + requestString + "', gameId: " + Application.application.parameters.gameId + ", localPlayer: " + localPlayer + ", numActionsReceived: " + numActionsReceived);
			
			// We're loading
			loading = true;
			sendingAction = requestString.length > 0;
			
			// Update user interface state
			updateUI();

			// Create request				
			var data : String = "gameId=" + Application.application.parameters.gameId
						+ "&localPlayer=" + localPlayer
						+ "&numActionsReceived=" + numActionsReceived
						+ "&requestedAction=" + encodeURIComponent(requestString);
			if (turnNumber > 0
				&& undoneActions.length == 0)
			{
				var state : String = dumpState(Faction.invalid);
				if (dumpStateRequested)
					data += "&remoteState=" + escape(state);
				else
					data += "&remoteStateHash=" + MD5.hash(state);
			}
			
			Tools.processRequest("GameUpdate.servlet", data, loadComplete, loadFailed);
		}
		
		/**
		 * Called after finished loading the data
		 */
		private function loadComplete(xmlRes : XML) : void 
		{
			// Check success
			if (xmlRes.code != 0)
			{
				showCommunicationFailure("Server could not process request!");				
				return;
			}
			
			// Update time left
			if (xmlRes.time < 0)
				turnEndTime = -1;
			else
				turnEndTime = getTimer() + int(xmlRes.time);
				
			// Update connected state
			playerConnected[0] = xmlRes.p1c != 0;
			playerConnected[1] = xmlRes.p2c != 0;
			
			// Update time between polls
			timeBetweenPolls = Math.max(minTimeBetweenPolls, int(xmlRes.tbp));
			
			// Check if server requests us to upload full state
			if (xmlRes.dsr != 0)
				dumpStateRequested = true;
				
			// Execute loaded actions
			executeAll(xmlRes, true);
			
			// No longer loading
			loading = false;
			sendingAction = false;
			nextLoadTime = getTimer() + timeBetweenPolls;

			// Update user interface state
			updateUI();
		}
		
		/**
		 * Called after finished loading the data on failure
		 */
		private function loadFailed() : void 
		{
			showCommunicationFailure("Could not reach server!");
		}
		
		/**
		 * Show communication error dialog and reset state
		 */
		private function showCommunicationFailure(msg : String) : void
		{
			// Reset units
			for each (var u : UnitState in units)
			{
				u.graphics.updateGraphics();
				u.graphics.moveToCurrentTile();
			}
			
			// Go back to an interactive UI state
			toDefaultState();

			// Show error
			Application.application.msgBox.showOk(msg, 
				function() : void
				{
					// No longer loading
					loading = false;
					sendingAction = false;
					nextLoadTime = getTimer() + timeBetweenPolls;
					updateUI();
				});
		}
		
		/**
		 * Execute an action
		 */
		public function execute(action : Action, waitForPlaybackComplete : Boolean) : void
		{
			Logger.log("execute() - action: " + action.toString());
			
			// Set game state
			action.setGameState(this);
			
			// Add as last action to redo?
			if (!action.canBeExecutedAnyTime() 
				&& (undoneActions.length != 0 || (waitForPlaybackComplete && currentUIState != null && currentUIState.isPlaying())))
			{
				undoneActions.splice(0, 0, action);
				return;
			}
			
			// Add to executed actions
			if (action.isUndoable())
				executedActions.push(action);

			// Perform the action
			action.doAction();
			
			// Update user interface
			updateUI();
		}
		
		/**
		 * Execute action from string
		 */
		private function executeFromString(actionString : String, waitForPlaybackComplete : Boolean) : void
		{
			// Split parameters
			var param : Array = actionString.split(",");

			// Determine action
			var action : Action = Action.createAction(param[0]);

			// Set game state
			action.setGameState(this);
			
			// Convert action from string
			action.fromString(param);

			// Perform the action
			execute(action, waitForPlaybackComplete);
		}
		
		/**
		 * Execute multiple actions from xml
		 */
		public function executeAll(xml : XML, waitForPlaybackComplete : Boolean) : void
		{
			for each (var actionString : String in xml.act)
			{
				// Execute the action
				executeFromString(actionString, waitForPlaybackComplete);
				
				// Increment number of actions received
				++numActionsReceived;
			}
		}	
		
		/**
		 * Go to initial state
		 */
		public function goBegin() : void
		{
			// Disable sounds
			SoundSystem.enabled = false;

			while (executedActions.length > 0)
			{
				// Get last performed action
				var action : Action = executedActions.pop();
				
				Logger.log("goBegin() - undo action: " + action.toString());

				// Add to undone actions
				undoneActions.push(action);
	
				// Undo the action
				action.undoAction();
			}

			// Force animations to stop 
			toDefaultState();

			// Stop playback
			setPlaybackEnabled(false);
			
			// Update user interface
			updateUI();			

			// Reenable sound
			SoundSystem.enabled = true;
		}
		
		/**
		 * Go one turn back
		 */
		public function goTurnBack() : void
		{
			// Disable sounds
			SoundSystem.enabled = false;

			// Go back to previous turn
			var hasUndone : Boolean = false;
			while (executedActions.length > 0)
			{
				// Get last performed action
				var action : Action = executedActions.pop();
				
				// Break if we encounter end turn action
				if (hasUndone && (action is ActionEndTurn))
				{
					executedActions.push(action);
					break;
				}
				
				Logger.log("goTurnBack() - undo action: " + action.toString());

				// Add to undone actions
				undoneActions.push(action);
	
				// Undo the action
				action.undoAction();
				
				// Flag that we undid one action
				hasUndone = true;
			}
			
			// Force animations to stop 
			toDefaultState();

			// Stop playback
			setPlaybackEnabled(false);

			// Update user interface
			updateUI();			

			// Reenable sound
			SoundSystem.enabled = true;
		}
		
		/**
		 * Go back an action
		 */
		public function goBack() : void
		{
			// Double check
			if (executedActions.length == 0)
				return;
				
			do
			{
				// Get last performed action
				var action : Action = executedActions.pop();
				
				Logger.log("goBack() - undo action: " + action.toString());
				
				// Add to undone actions
				undoneActions.push(action);
	
				// Undo the action
				action.undoAction();
				
			} while (executedActions.length > 0 && !action.undoRedoStop());

			// Stop playback
			setPlaybackEnabled(false);

			// Update user interface
			updateUI();			
		}
		
		/**
		 * Go forward action
		 */
		public function goForward() : void
		{
			// Double check
			if (undoneActions.length == 0)
				return;

			do
			{
				// Get last undone action
				var action : Action = undoneActions.pop();
				
				Logger.log("goForward() - action: " + action.toString());
	
				// Add to executed actions
				executedActions.push(action);
	
				// Redo the action
				action.doAction();
				
			} while (undoneActions.length > 0 && !action.undoRedoStop());			

			// Resume playback if this was the last action
			if (undoneActions.length == 0)
				setPlaybackEnabled(true);	

			// Update user interface
			updateUI();					
		}
		
		/**
		 * Go one turn forward
		 */
		public function goTurnForward() : void
		{
			// Disable sounds
			SoundSystem.enabled = false;

			// Redo actions			
			while (undoneActions.length > 0)
			{
				// Get last undone action
				var action : Action = undoneActions.pop();
				
				Logger.log("goTurnForward() - action: " + action.toString());
	
				// Add to executed actions
				executedActions.push(action);
	
				// Redo the action
				action.doAction();			
				
				// Detect start of next round
				if (action is ActionEndTurn)
					break;
			}

			// Force animations to stop 
			toDefaultState();

			// Resume playback if this was the last action
			if (undoneActions.length == 0)
				setPlaybackEnabled(true);	

			// Update user interface
			updateUI();			

			// Reenable sound
			SoundSystem.enabled = true;
		}

		/**
		 * Go to current state
		 */
		public function goEnd() : void
		{
			// Disable sounds
			SoundSystem.enabled = false;

			while (undoneActions.length > 0)
			{
				// Get last undone action
				var action : Action = undoneActions.pop();
				
				Logger.log("goEnd() - action: " + action.toString());
				
				// Add to executed actions
				executedActions.push(action);
	
				// Redo the action
				action.doAction();			
			}

			// Force animations to stop 
			toDefaultState();

			// Resume playback 
			setPlaybackEnabled(true);	

			// Update user interface
			updateUI();			

			// Reenable sound
			SoundSystem.enabled = true;
		}
		
		/**
		 * Start / stop playback
		 */
		public function setPlaybackEnabled(enabled : Boolean) : void
		{
			playbackEnabled = enabled || undoneActions.length == 0;
			
			Application.application.navPlayButton.visible = !playbackEnabled;
			Application.application.navStopButton.visible = playbackEnabled && undoneActions.length > 0;						
		}

		/**
		 * Switch to new UI state
		 */
		public function toState(state : UIState) : void
		{
			Logger.log("toState() - " + state);
			
			// Leave previous state
			if (currentUIState != null)
				currentUIState.onLeaveState();
				
			// Update local player if we're playing an offline game
			if (isLocalGame)
			{
				if (turnNumber == 0)
				{
					if (!playerReady[0])
					{
						localPlayer = Faction.f1;
					}
					else if (!playerReady[1])
					{
						localPlayer = Faction.f2;
					}
					else
					{
						// Start the game
						request(new ActionEndTurn());
						return;
					}
				}
				else
				{
					localPlayer = currentPlayer;
				}
			}
				
			// Enter new state
			currentUIState = state;
			if (currentUIState != null)
			{
				uiRefreshNeeded = true;

				currentUIState.setGameState(this);
				currentUIState.onEnterState();
			}
		}
		
		// Go back to the default ui state
		public function toDefaultState() : void
		{
			if (turnNumber == 0)
				toState(new UIStateSettingUp());
			else 
				toState(new UIStateSelectingUnit());
		}
		
		// Switch to the default state if there is no state
		public function toDefaultStateIfNoCurrentState() : void
		{
			if (currentUIState == null)
				toDefaultState();
		}
		
		/**
		 * General callback when tile receives rollover event
		 */
		public function tileRollOver(tile : MapTile) : void
		{
			var unitGraphics : UnitGraphics;
		
			var unit : UnitState = getUnitOnTile(tile);
			if (unit != null)
			{
				if (unit.graphics.getStateUnknown())
				{
					// Unknown unit
					unitGraphics = new UnitGraphics(unit, Application.application.unitImage, Application.application.unitTextImage, true, null);
					unitGraphics.moveTo(0, 0);
					unitGraphics.setStateUnknown(true);
					hoverOverUnits.push(unitGraphics);
					
					Application.application.unitText.visible = true;
					Application.application.unitText.text = "Unknown";
				}
				else
				{
					if ((unit.faction == localPlayer || !fogOfWarEnabled) && unit.containedUnits.length > 0)
					{
						// Local player can see all contained units
						var x : int = -17 * (unit.containedUnits.length - 1);
						for each (var c : UnitState in unit.containedUnits)
						{
							unitGraphics = new UnitGraphics(c, Application.application.unitImage, Application.application.unitTextImage, true, null);
							unitGraphics.moveTo(x, 0);
							hoverOverUnits.push(unitGraphics);
							
							x += 34;
						}  
					}
					else
					{
						// Remote player cannot see all contained units
						unitGraphics = new UnitGraphics(unit, Application.application.unitImage, Application.application.unitTextImage, true, null);
						unitGraphics.moveTo(0, 0);
						hoverOverUnits.push(unitGraphics);
					}					
				
					Application.application.unitText.visible = true;
					Application.application.unitText.text = unit.unitConfig.name;
					strengthText[0].setValue(unit.unitConfig.getStrengthProperties(UnitClass.air).getStrength(unit.ammo > 0));
					strengthText[1].setValue(unit.unitConfig.getStrengthProperties(UnitClass.land).getStrength(unit.ammo > 0));
					strengthText[2].setValue(unit.unitConfig.getStrengthProperties(UnitClass.water).getStrength(unit.ammo > 0));
					rangeText[0].setValue(unit.unitConfig.getStrengthProperties(UnitClass.air).attackRange);
					rangeText[1].setValue(unit.unitConfig.getStrengthProperties(UnitClass.land).attackRange);
					rangeText[2].setValue(unit.unitConfig.getStrengthProperties(UnitClass.water).attackRange);
					maxMovementText.setValue(unit.unitConfig.getMaxMovement());
					actionsLeftText.setValue(unit.actionsLeft);
					visionText.setValue(unit.unitConfig.visionRange);
					ammoText.setValue(unit.ammo);
				}
			}
			
			Application.application.terrainImage.visible = true;
			Application.application.terrainImage.setImage(tile.getTerrainImage());
			Application.application.terrainText.visible = true;
			var terrain : TerrainConfig = TerrainConfig.allTerrainMap[tile.getTerrainImage()];
			Application.application.terrainText.text = terrain.name + " (" + Tools.modifierToString(terrain.strengthModifier) + "%)";

			if (currentUIState != null)
				currentUIState.onMouseRollOver(tile);
		}
		
		/**
		 * General callback when tile receives rollout event
		 */
		public function tileRollOut(tile : MapTile) : void
		{
			for each (var u : UnitGraphics in hoverOverUnits)
				u.destroy();
			hoverOverUnits = new Array();
			
			Application.application.unitText.visible = false;
			strengthText[0].setValue(0);
			strengthText[1].setValue(0);
			strengthText[2].setValue(0);
			rangeText[0].setValue(0);
			rangeText[1].setValue(0);
			rangeText[2].setValue(0);
			maxMovementText.setValue(0);
			actionsLeftText.setValue(0);
			visionText.setValue(0);
			ammoText.setValue(0);
			Application.application.terrainImage.visible = false;
			Application.application.terrainText.visible = false;
			
			if (currentUIState != null)
				currentUIState.onMouseRollOut(tile);
		}
		
		/**
		 * General callback when tile was pressed
		 */
		public function tilePressed(tile : MapTile) : void
		{
			Logger.log("tilePressed - pos = (" + tile.locationX + "," + tile.locationY + "), state = " + currentUIState);
			var unit : UnitState = getUnit(tile.locationX, tile.locationY);
			if (unit != null)
				Logger.log("  unitId = " + unit.id + ", unitConfigId = " + unit.unitConfig.id + " (" + unit.unitConfig.name + ")"); 
		
			if (currentUIState != null)
				currentUIState.onMousePressed(tile);
		}
		
		/**
		 * General callback when tile was released
		 */
		public function tileReleased(tile : MapTile) : void
		{
			if (currentUIState != null)
				currentUIState.onMouseReleased(tile);
		}
	
		/**
		 * Called when new frame is rendered	
		 */
		public function enterFrame() : void
		{
			// Kick off loading
			if (!loading 
				&& !isLocalGame
				&& (getTimer() - nextLoadTime > 0 || pendingActions.length > 0))
				startLoad();
			
			// Go to next action if playback enabled and there are actions waiting
			if (playbackEnabled 
				&& undoneActions.length > 0
				&& (currentUIState == null || !currentUIState.isPlaying()))
				goForward();
			
			// Update current state
			if (currentUIState != null)
				currentUIState.onUpdate();
				
			// Update user interface state
			if (uiRefreshNeeded)
			{			
				updateUI();			
				updateFogOfWar();
				updateCanPerformAction();
				clearUnitTexts(false);
				
				uiRefreshNeeded = false;
			}
			
			// Update time left
			if (turnEndTime == -1)
			{
				Application.application.timeLeft.visible = false;
			}
			else
			{	
				Application.application.timeLeft.text = Tools.timerToString(turnEndTime - getTimer());
				Application.application.timeLeft.visible = true;
			}

			// Show extra help
			if (canPerformAction())
			{
				if (turnNumber == 0)
				{
					HelpBalloon.queueCenterOnDisplayObject(Application.application.endTurnButton, 
						"This is the deployment phase. You can drag and drop your units and bases to their starting positions. "
						+ "You can also drag units into and out of bases.\n\n"
						+ "When you are ready, press the Start Game button. "
						+ "After your opponent also presses the Start Game button the game will begin."
						+ (playByMail? "\n\n" + (receiveGameEventsByMail? TEXT_NOTIFICATIONS_ON : TEXT_NOTIFICATIONS_OFF) : ""), true);
				}
				else if (turnNumber == 1)
				{
					HelpBalloon.queueCenterOnDisplayObject(Application.application.showHelp, 
						"It is your turn to play, all of your units can perform 2 actions per turn. "
						+ "You can either move and attack or attack twice without moving. "
						+ "Some units have special abilities. Press the ? button to view in game help for all units", true);
						
					HelpBalloon.queueCenterOnDisplayObject(Application.application.endTurnButton,						
						"If you have moved all your units press the End Turn button. After this your opponent can move all of his units."
						+ (playByMail? "\n\n" + (receiveGameEventsByMail? TEXT_NOTIFICATIONS_ON : TEXT_NOTIFICATIONS_OFF) : ""), true);
				}
			}
			
			// Check if other player wants to draw
			if (winningFaction == Faction.invalid 
				&& drawRequested[Faction.opposite(localPlayer) - 1])
			{
				// Play sound
				SoundSystem.play(requestDrawClass);
				
				// Allow local user to accept
				Application.application.msgBox.showAcceptDecline(getPlayer(Faction.opposite(localPlayer)).name + " requests a draw!",
					function() : void
					{
						request(new ActionRequestDraw(localPlayer));
					},
					null);
					
				// Reset state so we don't show this window again
				drawRequested[Faction.opposite(localPlayer) - 1] = false;
			}		
			
			// Check if turn should be auto ended
			if (pendingActions.length == 0
				&& !loading
				&& (currentUIState == null || !currentUIState.isPlaying())
				&& turnNumber > 0
				&& canPerformAction()
				&& !anyUnitCanPerformAction)
			{
				request(new ActionEndTurn());
			}
		}
		
		
		/**
		 * Clear unit texts from previous action
		 */
		public function clearUnitTexts(clearAttackableUnits : Boolean) : void
		{
			for each (var u : UnitState in getUnits())
				if (clearAttackableUnits || u.graphics.getTextColor() != BitmapText.colorRed)
				{
					if (u.canPerformAction)
						u.graphics.setTextColor(BitmapText.colorWhite);
					else
						u.graphics.setTextColor(BitmapText.colorGray);
				}
		}
		
		/**
		 * Clear selection from previous action
		 */
		public function clearTileSelection() : void
		{
			for (var x : int = 0; x < mapConfig.sizeX; ++x)
				for (var y : int = 0; y < mapConfig.sizeY; ++y)
					mapConfig.getTile(x, y).setSelectionImage(MapTile.tileSelectionNone);
		}
				
		/**
		 * Calculate visibility of units
		 */
		public function updateFogOfWar() : void
		{
			var gameOver : Boolean = winningFaction != Faction.invalid && undoneActions.length == 0;
			var fow : Boolean = (fogOfWarEnabled && !gameOver) || localPlayer == Faction.none; 
			var factionToShow : int = localPlayer == Faction.none? currentPlayer : localPlayer;
				
			var unit : UnitState;
			
			// Calculate fogged area			
			if (fow)
			{
				var fogState : Array = Tools.createArray(mapConfig.sizeX, mapConfig.sizeY, -1000);
				for each (unit in getUnits())
					if (unit.faction == factionToShow)
						updateFogOfWarRecursive(fogState, unit.locationX, unit.locationY, unit.unitConfig.visionRange);
			}
				
			// Update tiles
			for (var x : int = 0; x < mapConfig.sizeX; ++x)
				for (var y : int = 0; y < mapConfig.sizeY; ++y)
					mapConfig.getTile(x, y).setShowFog(fow && fogState[x][y] < 0);

			// Change enemy units into question marks and update detected state
			for each (unit in getUnits())
				if (unit.faction == factionToShow)
				{
					// Friendly units are always visible
					unit.graphics.setStateUnknown(false);
					
					if (unit.unitConfig.beDetectedRange < 1000)
					{
						// Check if unit can be detected
						var detected : Boolean = false;
						for each (var otherUnit : UnitState in getUnits())
							if (otherUnit.faction != factionToShow
								&& HexagonGrid.getDistance(unit.locationX, unit.locationY, otherUnit.locationX, otherUnit.locationY) <= unit.unitConfig.beDetectedRange)
							{
								detected = true;
								break;
							}
						unit.graphics.setStateDetected(detected);
					}
					else
					{
						// Unit is always detected
						unit.graphics.setStateDetected(true);
					}
				}
				else
				{
					// Enemy units change into question marks if they're in the fog of war
					unit.graphics.setStateUnknown(fow && fogState[unit.locationX][unit.locationY] < 0);
					unit.graphics.setStateDetected(true);
				}
		}
		
		/**
		 * Calculate fogged area
		 */
		private function updateFogOfWarRecursive(fogState : Array, x : int, y : int, range : int) : void
		{
			// Check if any range left
			if (range < 0)
				return;
				
			// Check if we got here via a shorter or similar length path
			if (fogState[x][y] >= range)
				return;

			// Store this as best so far
			fogState[x][y] = range;

			// Recurse to neighbours
			for each (var n : Object in mapConfig.getNeighbours(x, y))
				updateFogOfWarRecursive(fogState, n.x, n.y, range - 1)
		}
		
		/**
		 * Calculate if units can perform action
		 */
		public function updateCanPerformAction() : void
		{
			// Go through all units
			anyUnitCanPerformAction = false; 
			for each (var unit : UnitState in getUnits())
			{
				unit.canPerformAction = canUnitPerformAction(unit);
				if (unit.canPerformAction)
					anyUnitCanPerformAction = true;
			}
		}

		/**
		 * Create feedback report
		 */
		public function feedbackReport() : void
		{
			Tools.navigateTo("FeedbackCreate.do", "gameId=" + Application.application.parameters.gameId, "_blank");
		}
	}
}

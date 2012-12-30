package onlinefrontlines.lobby
{
	import mx.core.Application;
	import mx.controls.Image;
	import mx.controls.Label;
	import mx.containers.*;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.net.*;
	import flash.utils.*;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.lobby.actions.*;
	import onlinefrontlines.game.Faction;
	
	/*
	 * Current state of the lobby
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
		/**
		 * Constants indicating how often to poll
		 */
		private const MIN_DELAY_BETWEEN_CLIENT_POLLS : int = 5 * 1000;
		private const MAX_DELAY_BETWEEN_CLIENT_POLLS : int = 50 * 1000;
		private const TIME_BETWEEN_MIN_AND_MAX_DELAY : int = 10 * 60 * 1000;

		/**
		 * Constant texts
		 */		
		private const TEXT_NOTIFICATIONS_ON : String = "You have turned on e-mail notifications so you will receive an e-mail when your game starts.";
		private const TEXT_NOTIFICATIONS_OFF : String = "You do not have e-mail notifications turned on, so you will have to check back regularly on this map to see if your game started.";
		
		/**
		 * Lobby config
		 */
		private var lobbyConfig : LobbyConfig;
		
		/**
		 * Maps country config id to name
		 */
		private var countryConfigs : Object;
		
		/**
		 * Friends as a map (friends[userid] != null when user is a friend)
		 */
		private var friends : Object;
		
		/**
		 * If connected user receives e-mail notifications
		 */
		private var receiveGameEventsByMail : Boolean;
		
		/**
		 * State of all countries
		 */
		private var countries : Array; // of Country
		
		/**
		 * Distance to enemy country for each country
		 */
		private var distanceToEnemy : Array;
		private var requiredDistance : int = 1;
		
		/**
		 * List of all users
		 */
		private var users : Array = new Array(); // of LobbyUser
		
		/**
		 * Local user id
		 */
		public var localUserId : int;
		
		/**
		 * Cached local user
		 */
		private var localUser : LobbyUser;
		
		/**
		 * Last received change count, used by the server to determine which things to send
		 */
		private var changeCount : int = 0;
		
		/**
		 * Lobby creation time
		 */
		private var creationTime : int = 0;
		
		/**
		 * Loading state
		 */		
		private var actionsToExecute : Array = new Array();		 
		private var loading : Boolean = false;
		private var nextLoadTime : int;
		private var lastActionSentTime : int = getTimer();
		
		/**
		 * State
		 */
		private var defendLocationX : int = -1;
		private var defendLocationY : int;
		private var defendLocationCachedAttacker : LobbyUser;
		private var attackLocationX : int = -1;
		private var attackLocationY : int;
		private var resetLocations : Boolean = false;
		private var gamesInProgress : int = 0;

		/**
		 * Constructor
		 */
		public function LobbyState(lobbyConfig : LobbyConfig, localUserId : int, countryConfigs : Object, friends : Object, receiveGameEventsByMail : Boolean)
		{
			// Store parameters
			this.lobbyConfig = lobbyConfig;
			this.localUserId = localUserId;
			this.countryConfigs = countryConfigs;
			this.friends = friends;
			this.receiveGameEventsByMail = receiveGameEventsByMail; 

			// Create countries
			countries = Tools.createArray(lobbyConfig.sizeX, lobbyConfig.sizeY, null);
			
			// Create countries for areas where battle could take place
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
				{
					var id : int = lobbyConfig.getCountryConfigId(x, y);
					if (id > 0)
					{
						countries[x][y] = new Country(x, y, countryConfigs[id]);
					}
				}		

			// Init timer
			nextLoadTime = getTimer();
		}

		/**
		 * Find user by id
		 */
		public function findUser(userId : int) : LobbyUser
		{
			if (userId == 0)
				return null;
				
			for each (var u : LobbyUser in users)
				if (u.userId == userId)
					return u;
					
			return null;
		}
		
		/**
		 * Get amount of users logged in
		 */
		public function getLobbyUserCount() : int
		{
			var count : int = 0;
			
			for each (var u : LobbyUser in users)
			{
				if (u.isConnected)
					count++;
			}
			
			return count;
		}
		
		/**
		 * Check if user is playing a game
		 */
		public function isUserInGame(userId : int) : Boolean
		{
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
				{
					var c : Country = countries[x][y];
					if (c != null
						&& c.currentGameUserId == userId)
						return true;
				}
				
			return false;
		}
		
		/**
		 * Test if user is a friend
		 */
		public function isFriend(userId : int) : Boolean
		{
			return friends[userId] != null;
		}
		
		/**
		 * Get a country
		 */
		public function getCountry(x : int, y : int) : Country 
		{
			if (x >= 0 && y >= 0 && x < lobbyConfig.sizeX && y < lobbyConfig.sizeY)
				return countries[x][y];
			else
				return null;
		}
		
		/**
		 * Get number of countries per army
		 */
		public function getNumberOfCountriesPerArmy() : Array
		{
			var count : Array = [ 0, 0 ];
			
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
				{
					var c : Country = countries[x][y];
					if (c != null)
					{
						count[c.army]++;
					}
				}
				
			return count;
		}
		
		/**
		 * Execute an action
		 */
		public function executeAction(action : Action) : void
		{
			actionsToExecute.push(action);
		}
		
		/**
		 * Start loading
		 */
		private function startLoad() : void
		{
			Logger.log("startLoad()");
			
			// We're loading
			loading = true;
			Application.application.throbber.show();

			// Create request				
			var data : String = "lobbyId=" + Application.application.parameters.lobbyId
								+ "&changeCount=" + changeCount;

			if (actionsToExecute.length > 0)
			{
				// Get action
				var action : String = actionsToExecute[0].toString();
				actionsToExecute.splice(0, 1);
				Logger.log("Action - " + action);
				
				// Append action
				data += "&requestedAction=" + encodeURIComponent(action);
	
				// Mark time that last action was sent			
				lastActionSentTime = getTimer();
			}

			// Send update request
			Tools.processRequest("LobbyUpdate.servlet", data, loadComplete, loadFailed);
		}
		
		/**
		 * Called after finished loading the data
		 */
		private function loadComplete(xmlRes : XML) : void 
		{
			// Check success
			if (int(xmlRes.code) != 0)
			{
				var errorMap : Object = new Object();
				errorMap[-1] = "Communication error!";
				errorMap[-2] = "Lobby does not exist!";
				errorMap[-3] = "You do not have the required rank!";
				errorMap[-4] = "Lobby is full!";	
				Application.application.msgBox.show(errorMap[int(xmlRes.code)]);
				return;
			}
			
			// Check creation time
			var newCreationTime : int = int(xmlRes.ctim);
			if (creationTime != 0 && newCreationTime != creationTime)
			{
				Application.application.msgBox.show("Communication error!");
				return;
			}
			creationTime = newCreationTime; 			

			// No longer loading
			loading = false;
			Application.application.throbber.hide();
			var timeBetweenPolls : int = MIN_DELAY_BETWEEN_CLIENT_POLLS + Math.min((getTimer() - lastActionSentTime) * (MAX_DELAY_BETWEEN_CLIENT_POLLS - MIN_DELAY_BETWEEN_CLIENT_POLLS) / TIME_BETWEEN_MIN_AND_MAX_DELAY, MAX_DELAY_BETWEEN_CLIENT_POLLS); 
			nextLoadTime = getTimer() + timeBetweenPolls;
			
			// Store new change count
			changeCount = int(xmlRes.cgct);
			
			// Get new country state
			var elements : Array;
			for each (var cNode : XML in xmlRes.cnty.c)
			{
				elements = cNode.toString().split(",");
				var locationX : int = int(elements[0]);
				var locationY : int = int(elements[1]);
				var c : Country = countries[locationX][locationY];
				c.army = int(elements[2]);
				c.ownerUserId = int(elements[3]);
				c.ownerExclusiveTime = getTimer() + int(elements[4]);
				c.currentGameId = int(elements[5]);
				c.currentGameUserId = int(elements[6]);
			}		
			
			// Get user change list
			for each (var uNode : XML in xmlRes.usrs.u)
			{
				elements = uNode.toString().split(",");
				var userId : int = int(elements[0]);
				
				// Find or create user
				var u : LobbyUser = findUser(userId);
				if (u == null)
				{
					u = new LobbyUser(this, userId, unescape(elements[1]));
					users.push(u);
				}
				
				// Update state
				u.army = int(elements[2]);				
				u.isConnected = int(elements[3]) != 0;
				u.setCountries(getCountry(int(elements[4]), int(elements[5])), getCountry(int(elements[6]), int(elements[7])));
				u.hasAcceptedChallenge = int(elements[8]) != 0;
				u.rank = int(elements[9]);
				u.level = int(elements[10]);
				u.leaderboardPosition = int(elements[11]);
				u.feedbackScore = int(elements[12]);
				u.gamesWon = int(elements[13]);
				u.gamesLost = int(elements[14]);
				u.autoDefendOwnedCountry = int(elements[15]) != 0;
				u.autoDeclineFriendlyDefender = int(elements[16]) != 0;
			}
			
			// Get text messages
			for each (var mNode : XML in xmlRes.msgs.m)
			{
				elements = mNode.toString().split(",");
				userId = int(elements[0]);
				var message : String = unescape(elements[1]);
				
				Application.application.receivedMessages.text += message + "\n";
			}
			
			// Cache local user
			localUser = findUser(localUserId);
			
			// Check if defend / attack location is still valid
			checkDefendLocation();
			checkAttackLocation();
			resetLocations = false;

			// Update user interface
			updateCountriesUI();
			updateUsersUI();
			updateBottomBarUI();
			
			// Check pending challenges
			checkChallenges();
			
			// Check if we started a game
			checkGamesInProgress();		

			// Show help
			if (localUser != null)
			{
				var myColor : String = localUser.army == Army.red? "red" : "blue";
				var opponentColor : String = localUser.army == Army.red? "blue" : "red";
				HelpBalloon.queue(Application.application.width / 2, Application.application.height / 2, 
					"You are part of the " + myColor + " army. \n\n"
					+ "You can start a game by selecting a " + myColor + " country to defend and then clicking on a " 
					+ opponentColor + " country to attack.", true);

				HelpBalloon.queueCenterOnDisplayObject(Application.application.playGameButton, 
					"To quickly get into a game press the 'Start Game' button.", true);
			}
			else
			{
				HelpBalloon.queueCenterOnDisplayObject(Application.application.playGameButton, 
					"You are here as a guest, press the 'Start Game' button to register and start playing.", true);
			}
		}
		
		/**
		 * Called after finished loading the data on failure
		 */
		private function loadFailed() : void 
		{
			// Display error
			Application.application.msgBox.show("Could not reach server!");
		}
		
		/**
		 * General callback when tile receives rollover event
		 */
		public function tileRollOver(tile : LobbyTile) : void
		{
			var c : Country = countries[tile.locationX][tile.locationY];			
			if (c != null)
			{				
				Application.application.mapName.text = c.countryConfig.name;
				Application.application.mapType.text = c.countryConfig.countryType;
				Application.application.armyUnits.text = c.countryConfig.numUnits;
				
				updateUserUI(c);			
			}
			else
			{
				Application.application.mapName.text = "";
				Application.application.mapType.text = "";
				Application.application.armyUnits.text = "";
				
				updateUserUI(null);
			}
		}
		
		/**
		 * General callback when tile receives rollout event
		 */
		public function tileRollOut(tile : LobbyTile) : void
		{
			Application.application.mapName.text = "";
			Application.application.mapType.text = "";
			Application.application.armyUnits.text = "";
		}
		
		/**
		 * General callback when tile was pressed
		 */
		public function tilePressed(tile : LobbyTile) : void
		{
			var dlx : int, dly : int, alx : int, aly : int, userId : int;
			
			if (localUser == null)
				return;
				
			var c : Country = countries[tile.locationX][tile.locationY];			
			if (c != null)
			{
				if (c.currentGameId != 0)
				{
					if (c.currentGameUserId == localUserId)
						Tools.navigateTo("GamePlay.do", "gameId=" + c.currentGameId, "_top");
				}
				else if (c.getOwnerExclusiveTimeLeft() > 0 
					&& c.ownerUserId != localUserId)
				{
					HelpBalloon.queueCenterOnHexagonGrid(lobbyConfig, tile.locationX, tile.locationY, 
						"This tile has just been conquered, it takes a day before it becomes available again.", true); 
				}
				else if (c.army == localUser.army
						&& !c.countryConfig.isCapturePoint 
						&& c.defender == null 
						&& (distanceToEnemy[c.locationX][c.locationY] == requiredDistance
							|| c.attacker != null))
				{
					if (localUser.getAttackedCountry() != null || localUser.getDefendedCountry() != null)
						executeAction(new ActionCancel());
						
					setDefendLocation(tile.locationX, tile.locationY);
					resetAttackLocation();
					updateCountriesUI();
					
					if (c.attacker != null)
					{
						// Store state for callback 
						userId = c.attacker.userId;
						dlx = defendLocationX;
						dly = defendLocationY;
						
						Application.application.challengeWindow.show(
							"Defend this country?",
							c,
							c.attacker,
							function() : void
							{
								defend(dlx, dly, userId);
							},
							function() : void
							{
								resetDefendLocation();
								updateCountriesUI();
							});
					}
				}						
				else if (c.army != localUser.army
						&& !c.countryConfig.isCapturePoint
						&& c.defender == null 
						&& c.attacker == null 
						&& defendLocationX != -1 
						&& HexagonGrid.getDistance(defendLocationX, defendLocationY, c.locationX, c.locationY) == requiredDistance)
				{
					setAttackLocation(tile.locationX, tile.locationY);
					updateCountriesUI();
					
					// Store state for callback
					dlx = defendLocationX;
					dly = defendLocationY;
					alx = attackLocationX;
					aly = attackLocationY;
					
					// Get defender
					var d : LobbyUser = c.defender;
					if (d == null)
					{
						// Check if owner will defend
						var owner : LobbyUser = findUser(c.ownerUserId);
						if (owner != null
							&& owner.autoDefendOwnedCountry 
							&& (owner.getAttackedCountry() == null || !owner.hasAcceptedChallenge))
							d = owner;
					}
										
					Application.application.challengeWindow.show(
						"Attack this country?",
						c,
						d,
						function() : void
						{
							attack(dlx, dly, alx, aly);
						},
						function() : void
						{
							resetAttackLocation();
							updateCountriesUI();
						});
				}
				else if (c.army != localUser.army
						&& !c.countryConfig.isCapturePoint 
						&& c.defender == null 
						&& c.attacker != null
						&& c.attacker.userId != localUserId
						&& !c.attacker.autoDeclineFriendlyDefender)
				{
					if (localUser.getAttackedCountry() != null || localUser.getDefendedCountry() != null)
						executeAction(new ActionCancel());
						
					setDefendLocation(tile.locationX, tile.locationY);
					resetAttackLocation();
					updateCountriesUI();
					
					// Store state for callback 
					userId = c.attacker.userId;
					dlx = defendLocationX;
					dly = defendLocationY;

					Application.application.challengeWindow.show(
						"Create friendly game?",
						c,
						c.attacker,
						function() : void
						{
							defendFriendly(dlx, dly, userId);
						},
						function() : void
						{
							resetDefendLocation();
							updateCountriesUI();
						});
				}						
				else
				{
					if (localUser.getAttackedCountry() != null || localUser.getDefendedCountry() != null)
						executeAction(new ActionCancel());

					resetAttackLocation();
					resetDefendLocation();
					updateCountriesUI();
				}
			}
		}
		
		/**
		 * General callback when tile was released
		 */
		public function tileReleased(tile : LobbyTile) : void
		{
		}
	
		/**
		 * Check if defend location is still valid
		 */
		private function checkDefendLocation() : void
		{
			if (defendLocationX == -1)
				return;
				
			var c : Country = getCountry(defendLocationX, defendLocationY);
			if (resetLocations
				|| c == null
				|| c.defender != null
				|| c.attacker != defendLocationCachedAttacker)
			{
				resetDefendLocation();
				resetAttackLocation();
				return;
			}
		}
		
		/**
		 * Reset current defended position
		 */
		private function resetDefendLocation() : void
		{
			setDefendLocation(-1, -1);
		}
		
		/**
		 * Set defend location
		 */
		private function setDefendLocation(x : int, y : int) : void
		{
			if (defendLocationX == x && defendLocationY == y)
				return;
				
			if (defendLocationX != -1)
			{
				var ox : int = defendLocationX;
				var oy : int = defendLocationY;				
				defendLocationX = -1;
				updateTileState(ox, oy);
			}
		
			if (x != -1)
			{
				defendLocationX = x;
				defendLocationY = y;
				var c : Country = getCountry(x, y);
				defendLocationCachedAttacker = c != null? c.attacker : null; 
				updateTileState(defendLocationX, defendLocationY);
			}			
		}
		
		/**
		 * Check if attack location is still valid
		 */
		private function checkAttackLocation() : void
		{
			if (attackLocationX == -1)
				return;
				
			var c : Country = getCountry(attackLocationX, attackLocationY);
			if (resetLocations
				|| c == null
				|| c.army == localUser.army
				|| c.defender != null
				|| c.attacker != null
				|| HexagonGrid.getDistance(defendLocationX, defendLocationY, attackLocationX, attackLocationY) != requiredDistance)
			{
				resetAttackLocation();
				return;
			}
		}
		
		/**
		 * Reset current attacked position
		 */
		private function resetAttackLocation() : void
		{
			setAttackLocation(-1, -1);
		}
		
		/**
		 * Set attack location
		 */
		private function setAttackLocation(x : int, y : int) : void
		{
			if (attackLocationX == x && attackLocationY == y)
				return;
				
			if (attackLocationX != -1)
			{
				var ox : int = attackLocationX;
				var oy : int = attackLocationY;				
				attackLocationX = -1;
				updateTileState(ox, oy);
			}
		
			if (x != -1)
			{
				attackLocationX = x;
				attackLocationY = y;
				updateTileState(attackLocationX, attackLocationY);
			}			
		}

		/**
		 * Check if there are any pending challenges
		 */
		public function checkChallenges() : void
		{
			// Don't do anything if window still visible or user has already accepted
			if (localUser == null
				|| localUser.hasAcceptedChallenge
				|| Application.application.challengeWindow.visible)
				return;
				
			// Check if a challenge is waiting for us
			var c : Country = localUser.getAttackedCountry();				
			if (c != null && c.defender != null)
			{
				var userId : int = c.defender.userId;
				Application.application.challengeWindow.show(
					c.defender.army != c.attacker.army? "Accept this defender?" : "Accept this friendly game?",
					c,
					c.defender,
					function() : void
					{
						executeAction(new ActionAcceptChallenge(userId)); 
					},
					function() : void
					{
						executeAction(new ActionDeclineChallenge(userId));
					});
			}
		}
		
		/**
		 * Check if server started game for us
		 */
		public function checkGamesInProgress() : void
		{
			if (localUser == null)
				return;
				
			// Get number of games in progress
			var count : int = 0;	
			var gameId : int = 0;		
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
				{ 
					var c : Country = countries[x][y];
					
					if (c != null 
						&& c.currentGameUserId == localUserId)
					{
						gameId = c.currentGameId;
						count++;
					}
				}
				
			// If amount changed go to continue screen
			if (gamesInProgress < count)
				Application.application.msgBox.showYesNo("Your game has started, do you want to go to the game?",
					function() : void
					{
						if (count == 1)
							Tools.navigateTo("GamePlay.do", "gameId=" + gameId, "_top");
						else
							Tools.navigateTo("GameContinue.do", null, "_top");
					},
					null);
			
			// Store count so we don't show this popup unless something changes
			gamesInProgress = count;
		}
						
		/**
		 * Called when new frame is rendered	
		 */
		public function enterFrame() : void
		{
			// Kick off loading
			if (!loading 
				&& (getTimer() - nextLoadTime > 0
					|| actionsToExecute.length > 0))
				startLoad();			
		}
		
		/**
		 * Update countries
		 */
		private function updateCountriesUI() : void
		{
			determineDefendableCountries();
			
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
					updateTileState(x, y)
		}
		
		/**
		 * Update state for a single tile
		 */
		private function updateTileState(x : int, y : int) : void
		{
			var tile : LobbyTile = lobbyConfig.getTile(x, y);
			var c : Country = countries[x][y];
			
			if (c == null)
			{
				tile.setVisible(false);
			}
			else
			{
				tile.setVisible(true);
				
				// Set state
				var state : int = LobbyTile.stateRed;
				
				// Special cases to show correct icons while server is processing my actions
				if (c.locationX == defendLocationX && c.locationY == defendLocationY)
					state = LobbyTile.stateDefendedByMeRed;
				else if (c.locationX == attackLocationX && c.locationY == attackLocationY)
					state = LobbyTile.stateIAttackedWaitingForDefenderRed;
					
				else if (c.countryConfig.isCapturePoint)
				{
					// This is a capture point
					state = LobbyTile.stateCapturePointRed;
				}
				else if (c.currentGameId != 0)
				{
					// Game in progress
					state = LobbyTile.stateGameInProgressRed;
				}
				else if (localUser == null)
				{
					if (c.defender != null)
					{
						// Defended
						state = LobbyTile.stateDefendedByOtherRed;
					}
					else if (c.attacker != null)
					{
						// Attacked
						state = LobbyTile.stateOtherAttackedICanDefendRed;
					}
					else
					{
						// Nothing going on
						state = LobbyTile.stateRed;
					}
				}
				else if (c.army == localUser.army)
				{
					// Country owned by my army 
					if (c.defender == localUser)
					{
						// I have selected this country to defend
						state = LobbyTile.stateDefendedByMeRed;
					}
					else if (c.defender != null)
					{
						// Someone of my faction is defending so this tile is unusable for me
						state = LobbyTile.stateUnusableRed;
					}
					else 
					{ 
						// No defender yet
						if (c.attacker != null)
						{
							// Someone from other faction is attacking and I can defend				
							state = LobbyTile.stateOtherAttackedICanDefendRed;
						}
						else
						{
							if (localUser.getDefendedCountry() == null 
								&& defendLocationX == -1 
								&& distanceToEnemy[c.locationX][c.locationY] == requiredDistance)
							{
								// User could defend this tile
								state = LobbyTile.stateDefendableRed;
							}
							else
							{
								// Nothing going on
								state = LobbyTile.stateRed;
							}
						}
					}
				}
				else if (c.army != localUser.army)
				{
					// Country owned by opposite army
					if (c.attacker == localUser)
					{
						// I am attacking
						if (c.defender != null)
						{
							// There is a defender that I need to accept
							state = LobbyTile.stateDefendedByOtherRed;
						}
						else
						{
							// No defender yet 
							state = LobbyTile.stateIAttackedWaitingForDefenderRed;
						}
					}
					else if (c.defender == localUser)
					{
						// I have selected this country to defend
						state = LobbyTile.stateDefendedByMeRed;
					}
					else if (c.attacker != null)
					{
						// Someone of my faction is attacking, I could create a friendly game
						state = LobbyTile.stateDefendableFriendlyGameRed;
					}
					else
					{
						// No attacker yet
						if (c.defender != null)
						{
							// Someone from other faction is defending
							state = LobbyTile.stateDefendedByOtherRed;
						}
						else
						{
							if (defendLocationX != -1 
								&& attackLocationX == -1
								&& getCountry(defendLocationX, defendLocationY).attacker == null
								&& HexagonGrid.getDistance(defendLocationX, defendLocationY, c.locationX, c.locationY) == requiredDistance)
							{
								// User could attack
								state = LobbyTile.stateAttackableRed;
							}
							else
							{
								// Nothing going on
								state = LobbyTile.stateRed;
							}
						}
					}
				}

				tile.setState(c.army == Army.red? state : state + 1);
				
				// Set ownership
				var ownership : int = LobbyTile.ownershipNone;
				if (localUserId != 0 && c.ownerUserId == localUserId)
					ownership = LobbyTile.ownershipMe;
				else if (isFriend(c.ownerUserId))
					ownership = LobbyTile.ownershipFriend;
				else if (c.getOwnerExclusiveTimeLeft() > 0)
					ownership = LobbyTile.ownershipOther;
				tile.setOwnership(ownership);
			}
		}					
		
		/**
		 * Update local user info
		 */
		private function updateUserUI(country : Country) : void
		{
			var user : LobbyUser;
			if (country.currentGameUserId != 0)
				user = findUser(country.currentGameUserId);
			else
				user = country.defender != null? country.defender : country.attacker;
			
			if (user != null)
			{
				Application.application.playerArmy.source = Army.getSmallIcon(user.army);
				Application.application.playerName.text = user.username;
				Application.application.playerLevel.text = user.level.toString();
				Application.application.playerRank.source = RankImage.getImageClass(user.rank);
				Application.application.playerLeaderboardPosition.text = user.getLeaderboardPosition();
				Application.application.playerFeedbackScore.text = user.feedbackScore.toString();
				Application.application.playerGamesWon.text = user.gamesWon.toString();
				Application.application.playerGamesLost.text = user.gamesLost.toString();

				Application.application.playerArmy.visible = true;
				Application.application.playerName.visible = true;
				Application.application.playerLevel.visible = true;
				Application.application.playerRank.visible = true;
				Application.application.playerLeaderboardPosition.visible = true;
				Application.application.playerFeedbackScore.visible = true;
				Application.application.playerGamesWon.visible = true;
				Application.application.playerGamesLost.visible = true;			
			}
			else
			{
				Application.application.playerArmy.visible = false;
				Application.application.playerName.visible = false;
				Application.application.playerLevel.visible = false;
				Application.application.playerRank.visible = false;
				Application.application.playerLeaderboardPosition.visible = false;
				Application.application.playerFeedbackScore.visible = false;
				Application.application.playerGamesWon.visible = false;
				Application.application.playerGamesLost.visible = false;			
			}
		}
		
		/**
		 * Update bottom bar
		 */
		private function updateBottomBarUI() : void
		{
			// Get number of online players
			Application.application.onlineCount.text = getLobbyUserCount().toString();
			
			// Get country balance
			var count : Array = getNumberOfCountriesPerArmy();
			var total : int = count[0] + count[1];			
			Application.application.countryBarRed.width = total > 0? (85 * count[Army.red]) / total : 0;			
			Application.application.countryBarBlue.width = total > 0? (85 * count[Army.blue]) / total : 0;
		}
		
		/**
		 * Update user list
		 */
		private function updateUsersUI() : void
		{
			Application.application.userList.removeAllChildren();
		
			var y : int = 0;
			
			for (var i : int = 0; i < users.length; ++i)
			{
				var u : LobbyUser = users[i];
				
				if (!u.isConnected)
					continue;
				
				var armyImage : Image = new Image();
				armyImage.x = 0;
				armyImage.y = y + 2;
				armyImage.source = Army.getSmallIcon(u.army);
				Application.application.userList.addChild(armyImage);
				
				var rankImage : Image = new Image;
				rankImage.x = 13;
				rankImage.y = y + 2;
				rankImage.source = RankImage.getImageClass(u.rank);
				Application.application.userList.addChild(rankImage);

				var levelLabel : Label = new Label();
				levelLabel.x = 28;
				levelLabel.y = y - 4;
				levelLabel.styleName = "default";
				levelLabel.text = u.level.toString();
				Application.application.userList.addChild(levelLabel);

				var nameLabel : Label = new Label();
				nameLabel.x = 50;
				nameLabel.y = y - 4;
				nameLabel.styleName = "default";
				nameLabel.text = u.username;
				Application.application.userList.addChild(nameLabel);
				
				y += 10;
			}
		}
		
		/**
		 * Determine countries that are defendable
		 * 
		 * After this call all countries for which distanceToEnemy[x][y] == requiredDistance
		 * are defendable (if the country is owned by the correct army and there is no other 
		 * defender / attacker yet)
		 */
		private function determineDefendableCountries() : void
		{
			if (localUser == null)
				return;

			// Init array
			distanceToEnemy = Tools.createArray(lobbyConfig.sizeX, lobbyConfig.sizeY, 10000);
			requiredDistance = 10000;

			// Populate list with all countries that are attackable
			var list : Array = new Array();			
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
				{ 
					var c : Country = countries[x][y];
					if (c != null
						&& c.army != localUser.army 
						&& !c.countryConfig.isCapturePoint 
						&& c.currentGameId == 0
						&& (c.attacker == null || c.attacker == localUser))
					{
						distanceToEnemy[x][y] = 0;
						list.push({ x : x, y : y, d : 0 });
					}
				}
			
			// Loop while there are still countries left
			while (list.length > 0)
			{
				// Get first element from the list
				x = list[0].x;
				y = list[0].y;
				var d : int = list[0].d;
				list.splice(0, 1);
								
				// Check if other shorter path has been found
				if (distanceToEnemy[x][y] < d)
					continue;
					
				// Check if this is the shortest path to an attackable country so far
				if (d < requiredDistance)
				{
					c = countries[x][y];
					if (c.army == localUser.army
						&& !c.countryConfig.isCapturePoint
						&& c.currentGameId == 0
						&& (c.defender == null || c.defender == localUser))
						requiredDistance = d;
				}				
					
				// Add all neighbours to the list to be searched
				d++;
				var neighbours : Array = lobbyConfig.getNeighbours(x, y);
				for each (var n : Object in neighbours)
					if (countries[n.x][n.y] != null
						&& distanceToEnemy[n.x][n.y] > d)
					{
						distanceToEnemy[n.x][n.y] = d;
						list.push({ x : n.x, y : n.y, d : d });
					}
			}
		}

		/**
		 * Algorithm:
		 * If a challenge from an enemy exists -> join it
		 * Else if there is a country that is automatically defended -> attack it
		 * Else if there is a friendly challenge -> join it
		 * Else create random challenge
		 */		
		public function playGame() : void
		{
			var challenge : Object;
			
			if (localUser == null)
				return;
			
			// Cancel any outstanding challenges 
			if (localUser.getAttackedCountry() != null || localUser.getDefendedCountry() != null)
			{
				executeAction(new ActionCancel());
				resetDefendLocation();
				resetAttackLocation();
			}
			
			// Make sure the defendable countries list is up to date
			determineDefendableCountries();

			// Find all pending challenges
			var enemyChallenges : Array = new Array();
			var friendlyChallenges : Array = new Array();
			for each (var u : LobbyUser in users)
				if (u != localUser
					&& u.getAttackedCountry() != null
					&& u.getAttackedCountry().defender == null)
				{
					challenge = { x : u.getAttackedCountry().locationX, y : u.getAttackedCountry().locationY, id: u.userId };
					if (u.army != localUser.army)
						enemyChallenges.push(challenge);
					else if (!u.autoDeclineFriendlyDefender)
						friendlyChallenges.push(challenge);
				}
			
			// 1. Enemy challenge
			if (enemyChallenges.length > 0)
			{
				challenge = enemyChallenges[Tools.randRange(0, enemyChallenges.length - 1)];
				defend(challenge.x, challenge.y, challenge.id);	
				return;
			}
							
			// Find possible new challenges
			var autoDefendChallenge : Array = new Array();
			var normalChallenge : Array = new Array();			
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
				{
					var dc : Country = getCountry(x, y);
					if (dc != null
						&& dc.army == localUser.army
						&& dc.defender == null
						&& dc.attacker == null
						&& dc.currentGameId == 0
						&& !dc.countryConfig.isCapturePoint
						&& distanceToEnemy[x][y] == requiredDistance
						&& (dc.getOwnerExclusiveTimeLeft() == 0 || dc.ownerUserId == localUserId))
					{
						for (var y2 : int = y - requiredDistance; y2 <= y + requiredDistance; ++y2)
							for (var x2 : int = x - requiredDistance; x2 <= x + requiredDistance; ++x2)
							{
								var ac : Country = getCountry(x2, y2);
								if (ac != null
									&& ac.army != localUser.army
									&& ac.attacker == null
									&& ac.defender == null
									&& ac.currentGameId == 0
									&& !ac.countryConfig.isCapturePoint
									&& HexagonGrid.getDistance(x, y, x2, y2) == requiredDistance
									&& ac.getOwnerExclusiveTimeLeft() == 0)
								{
									challenge = { x : x, y : y, x2 : x2, y2 : y2 };
									
									// Check if owner will defend
									var owner : LobbyUser = findUser(ac.ownerUserId);
									if (owner != null 
										&& owner.autoDefendOwnedCountry
										&& owner.army != localUser.army)										
										autoDefendChallenge.push(challenge);
									else
										normalChallenge.push(challenge);										
								}
							}
					}
				}
							
			// 2. Create challenge that will be auto defended
			if (autoDefendChallenge.length > 0)
			{
				challenge = autoDefendChallenge[Tools.randRange(0, autoDefendChallenge.length - 1)];
				attack(challenge.x, challenge.y, challenge.x2, challenge.y2);
				return;
			}
						
			// 3. Friendly challenge
			if (friendlyChallenges.length > 0)
			{
				challenge = friendlyChallenges[Tools.randRange(0, friendlyChallenges.length - 1)];
				defendFriendly(challenge.x, challenge.y, challenge.id);
				return;
			}

			// 4. Create undefended challenge
			if (normalChallenge.length > 0)
			{
				challenge = normalChallenge[Tools.randRange(0, normalChallenge.length - 1)];
				attack(challenge.x, challenge.y, challenge.x2, challenge.y2);	
				return;
			}
			
			Application.application.msgBox.showOk("No challenges available!", null);			
		}
		
		/**
		 * Defend country at (x, y) attacked by userId
		 */
		private function defend(x : int, y : int, userId : int) : void
		{
			executeAction(new ActionDefend(x, y, userId));
			setDefendLocation(x, y);
			resetAttackLocation();
			updateCountriesUI();
			resetLocations = true;
		}
		
		/**
		 * Defend country at (x, y) attacked by userId (friendly game popup)
		 */
		private function defendFriendly(x : int, y : int, userId : int) : void
		{
			executeAction(new ActionDefend(x, y, userId));
			setDefendLocation(x, y);
			resetAttackLocation();
			updateCountriesUI();
			resetLocations = true;
		}
		
		/**
		 * Attack country (alx, aly) from (dlx, dly)
		 */
		private function attack(dlx : int, dly : int, alx : int, aly : int) : void
		{
			executeAction(new ActionAttack(dlx, dly, alx, aly)); 
			setDefendLocation(dlx, dly);
			setAttackLocation(alx, aly);
			updateCountriesUI();
			resetLocations = true; 
			
			HelpBalloon.queueCenterOnHexagonGrid(lobbyConfig, alx, aly, 
				"You have attacked this country!\n\n"
				+ "Please wait until an opponent accepts this challenge. "
				+ "The challenge will stay active for 3 days, you don't need to be logged in during that time.\n\n"
				+ (receiveGameEventsByMail? TEXT_NOTIFICATIONS_ON : TEXT_NOTIFICATIONS_OFF), true); 
		}		
	}
}
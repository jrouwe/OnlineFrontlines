package onlinefrontlines.game
{
	import mx.core.Application;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.MouseEvent;
	import flash.net.*;
	import mx.effects.IEffectInstance;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Main class for the game
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
	public class Main
	{
		// State of the game
		private static var gameState : GameState;
		
		// Enable counter
		private static var disableCount : int = 0;
		
		// Enable / disable user interface
		private static function enable(enable : Boolean) : void
		{
			// Filter out multiple enables / disables
			if (enable)
				disableCount--;
			else
				disableCount++;
			enable = disableCount == 0;	
			
			Application.application.greyOut.visible = !enable;
			Application.application.endTurnButton.enabled = enable;
			Application.application.toLobbyButton.enabled = enable;
			Application.application.navBeginButton.enabled = enable;
			Application.application.navTurnBackButton.enabled = enable;
			Application.application.navBackButton.enabled = enable;
			Application.application.navStopButton.enabled = enable;
			Application.application.navPlayButton.enabled = enable;
			Application.application.navForwardButton.enabled = enable;
			Application.application.navTurnForwardButton.enabled = enable;
			Application.application.navEndButton.enabled = enable;
			Application.application.drawButton.enabled = enable;
			Application.application.surrenderButton.enabled = enable;
			Application.application.feedbackButton.enabled = enable;
			
			if (gameState != null)
				gameState.enable(enable);
			}

		// Called after finished loading the data
		private static function loadComplete(xmlRes : XML) : void 
		{
			// Check success
			if (xmlRes.code != 0)
			{
				Application.application.msgBox.show("Error loading map!");
				return;
			}

			// Setup faction color			
			Faction.faction1IsRed = int(xmlRes.f1r) != 0;

			// Load terrain config
			TerrainConfig.loadAll(xmlRes);
			
			// Load units
			UnitConfig.loadAll(xmlRes);
			
			// Load tips
			TipsWindow.loadAll(xmlRes);
			
			// Load map data
			var mapConfig : MapConfig = Application.application.mapConfig;
			mapConfig.createTiles(
				function(sel : MapTile) : void { gameState.tilePressed(sel); }, 
				function(sel : MapTile) : void { gameState.tileReleased(sel); }, 
				function(sel : MapTile) : void { gameState.tileRollOver(sel); }, 
				function(sel : MapTile) : void { gameState.tileRollOut(sel); },
				true);
			mapConfig.loadFromNode(xmlRes.map);
						
			// Create game state
			gameState = new GameState(mapConfig, int(xmlRes.f1s) != 0, int(xmlRes.rge) != 0);
			
			// Give chat window game state
			Application.application.chatWindow.setGameState(gameState);
			
			// Get settings
			gameState.scoreLimit = int(xmlRes.slm);
			gameState.fogOfWarEnabled = int(xmlRes.fow) != 0;
			gameState.lobbyId = int(xmlRes.lbi);
			gameState.playByMail = int(xmlRes.pbm) != 0;
			
			// Start in set up state
			gameState.toState(new UIStateSettingUp());

			// Execute loaded actions
			gameState.executeAll(xmlRes, false);
			
			// Go back in time
			if (gameState.localPlayer == Faction.none)
				gameState.goBegin();
			else
				gameState.goTurnBack();
			
			// Make sure UI is good for first frame
			gameState.updateUI();
			gameState.clearUnitTexts(true);
			gameState.updateFogOfWar();
			
			// Set up event listeners
			Application.application.showGraveyard.addEventListener(Event.CHANGE, 
				function(e : Event) : void
				{
					if (Application.application.showGraveyard.selected)
						Application.application.graveyardWindow.show(gameState);
					else
						Application.application.graveyardWindow.close();
				});
			Application.application.showOwner.addEventListener(Event.CHANGE, 
				function(e : Event) : void
				{
					mapConfig.ownerIconRoot.visible = Application.application.showOwner.selected;
					LocalSettings.getInstance().saveOwnerState(); 
				});
			Application.application.showChat.selected = false;
			Application.application.showChat.addEventListener(Event.CHANGE, 
				function(e : Event) : void
				{
					Application.application.chatWindow.visible = Application.application.showChat.selected;
				});
			Application.application.showScore.selected = false;
			Application.application.showScore.addEventListener(Event.CHANGE, 
				function(e : Event) : void
				{
					if (Application.application.showScore.selected)
						Application.application.scoreWindow.show(gameState);
					else
						Application.application.scoreWindow.close();
				});
			Application.application.enableSound.addEventListener(Event.CHANGE, 
				function(e : Event) : void
				{
					LocalSettings.getInstance().saveSoundState(); 
				});
			Application.application.showInfo.addEventListener(Event.CHANGE, 
				function(e : Event) : void
				{
					LocalSettings.getInstance().saveShowInfoState(); 
				});
			Application.application.showHelp.addEventListener(Event.CHANGE, 
				function(e : Event) : void
				{
					if (Application.application.showHelp.selected)
						Application.application.helpWindow.show();
					else
						Application.application.helpWindow.close();
				});
			Application.application.endTurnButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					if (gameState.canPerformAction())
					{
						if (gameState.turnNumber == 0)
						{
							if (!gameState.hasTeleportedUnit && gameState.getUnitCount(gameState.localPlayer) > 0)
								Application.application.msgBox.showYesNo("You haven't dragged any of your units, are you sure you want to start?",
									function () : void
									{
										gameState.request(new ActionPlayerReady(gameState.getLocalPlayerId()));
									},
									null);
							else if (!gameState.hasTeleportedBase && gameState.getBaseCount(gameState.localPlayer) > 0)
								Application.application.msgBox.showYesNo("You haven't dragged any of your bases, are you sure you want to start?",
									function () : void
									{
										gameState.request(new ActionPlayerReady(gameState.getLocalPlayerId()));
									},
									null);
							else
								gameState.request(new ActionPlayerReady(gameState.getLocalPlayerId()));
						}
						else 
							gameState.request(new ActionEndTurn());
					}
				});
			Application.application.toLobbyButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					Tools.navigateTo("Lobby.do", "lobbyId=" + gameState.lobbyId, "_top");
				});
			Application.application.navBeginButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.goBegin();
				});
			Application.application.navTurnBackButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.goTurnBack();
				});
			Application.application.navBackButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.goBack();
				});
			Application.application.navStopButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.setPlaybackEnabled(false);
					gameState.updateUI();
				});
			Application.application.navPlayButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.setPlaybackEnabled(true);
					gameState.updateUI();
				});
			Application.application.navForwardButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.goForward();
				});
			Application.application.navTurnForwardButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.goTurnForward();
				});
			Application.application.navEndButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.goEnd();
				});
			Application.application.drawButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					Application.application.msgBox.showYesNo("Do you want to request a draw?",
						function() : void
						{
							gameState.request(new ActionRequestDraw(gameState.localPlayer));
						},
						null);
				});
			Application.application.surrenderButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					Application.application.msgBox.showYesNo("Do you want to surrender?",
						function() : void
						{
							gameState.request(new ActionSurrender(gameState.localPlayer));
						},
						null);
				});
			Application.application.feedbackButton.addEventListener(MouseEvent.CLICK, 
				function(e : Event) : void
				{
					gameState.feedbackReport();
				});
			Application.application.addEventListener(Event.ENTER_FRAME,
				function (e : Event) : void
				{
					gameState.enterFrame();
				});

			// Set defaults based on player experience
			if (gameState.getLocalPlayer() != null)				
				LocalSettings.getInstance().setDefaults(gameState.getLocalPlayer().level);		

			// Restore states of windows
			LocalSettings.getInstance().restoreChatWindowState();		
			LocalSettings.getInstance().restoreScoreWindowState();		
			LocalSettings.getInstance().restoreGraveyardState();		
			LocalSettings.getInstance().restoreOwnerState(); 
			LocalSettings.getInstance().restoreSoundState(); 
			LocalSettings.getInstance().restoreShowInfoState();
			Application.application.showChat.selected = true; 

			// Remove popups
			Application.application.msgBox.close();
			Application.application.slideBox.closeAll();
			Application.application.victoryWindow.close();

			// Start the game
			startGame();

			// Display help
			if (gameState.localPlayer == Faction.none)
				HelpBalloon.queueCenterOnDisplayObject(Application.application.navPlayButton,
					"You are currently replaying a match. Use these time controls to go forwards and backwards in the game.", true);
		}
		
		// Called to start the game
		public static function startGame() : void
		{
			// Start play button pulsate effect
			var effect : IEffectInstance = Application.application.pulsateNavPlayButton.createInstance(Application.application.navPlayButton);
			effect.play();
			
			// Enable sounds
			SoundSystem.enabled = true;			

			// Show tips
			if (!Application.application.tipsWindow.hasUserDisabledTips())
				Application.application.tipsWindow.show(
					function() : void
					{
						gameState.setPlaybackEnabled(true);
					});
			else
				gameState.setPlaybackEnabled(true);
		}	
		
		// Called after finished loading the data on failure
		private static function loadFailed() : void 
		{
			Application.application.msgBox.show("Could not reach server!");
		}
		
		// Main loop for the game
		public static function run() : void
		{
			// Initialize logger
			Logger.init();
			
			// Init the tool window system
			ToolWindow.init();
			
			// Setup enableUI function
			MsgBox.enableUI = enable;
			BaseMenu.enableUI = enable;
			BattleView.enableUI = enable;
			
			// Create request				
			var data : String = "gameId=" + Application.application.parameters.gameId
			 					 + "&localPlayer=" + Application.application.parameters.localPlayer;

			// Send load request
			Application.application.msgBox.show("Loading game...");
			Tools.processRequest("GameLoad.do", data, loadComplete, loadFailed);
		}
	}
}

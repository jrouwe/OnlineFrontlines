package onlinefrontlines.lobby
{
	import mx.core.Application;
	import mx.collections.ArrayCollection;
	import mx.events.ListEvent;
	import mx.events.FlexEvent;
	import flash.net.*;
	import flash.ui.Keyboard;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.KeyboardEvent;
	import flash.events.IOErrorEvent;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.lobby.*;
	import onlinefrontlines.lobby.actions.*;
	
	/*
	 * Main class for the lobby
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
		// Local user id
		private static var localUserId : int = 0;
		
		// Configuration of the lobby
		private static var lobbyConfig : LobbyConfig;
		
		// State of the lobby
		private static var lobbyState : LobbyState;
		
		// Enable counter
		private static var disableCount : int = 0;

		// Enable / disable user interface
		public static function enable(enable : Boolean) : void
		{
			// Filter out multiple enables / disables
			if (enable)
				disableCount--;
			else
				disableCount++;
			enable = disableCount == 0;	

			Application.application.greyOut.visible = !enable;
			Application.application.lobbyConfig.enabled = enable;
			Application.application.editMessage.enabled = enable && localUserId != 0;
			Application.application.sendButton.enabled = enable && localUserId != 0;
			Application.application.playGameButton.enabled = enable;
		}
		
		// Send text message
		private static function sendText() : void
		{
			var msg : String = Application.application.editMessage.text;
			if (msg == "@log")
				Logger.show();
			else if (msg != "")
				lobbyState.executeAction(new ActionTextMessage(lobbyState.localUserId, msg));
			Application.application.editMessage.text = "";
		}		

		// Called after finished loading the lobby data
		private static function loadComplete(xmlRes : XML) : void 
		{
			// Check success
			if (xmlRes.code != 0)
			{
				var errorMap : Object = new Object();
				errorMap[-1] = "Communication error!";
				errorMap[-2] = "Lobby does not exist!";
				errorMap[-3] = "You do not have the required rank!";
				Application.application.msgBox.show(errorMap[int(xmlRes.code)]);
				return;
			}
			
			// Get data
			lobbyConfig.loadFromNode(xmlRes.lobby);
			
			// Get local user id
			localUserId = int(xmlRes.uid);
			
			// Get army
			var army : int = int(xmlRes.army);
			
			// Get if email notifications are on
			var receiveGameEventsByMail : Boolean = int(xmlRes.rge) != 0;
			
			// Get country config names
			var countryConfigs : Object = new Object();
			for each (var ccNode : XML in xmlRes.ccfg)
			{
				var c : CountryConfig = new CountryConfig();
				c.id = ccNode.id;
				c.mapId = ccNode.mid;
				c.name = ccNode.name;
				c.isCapturePoint = int(ccNode.cpt) != 0;
				c.countryType = ccNode.type;
				c.countryTypeDescription = ccNode.desc;
				c.numUnits = ccNode.nunit;
				countryConfigs[ccNode.id] = c; 
			}
			
			// Get friends  
			var friends : Object = new Object();
			for each (var f : String in xmlRes.frd.toString().split(","))
				if (f.length > 0)
					friends[int(f)] = 1;

			// Create lobby state
			lobbyState = new LobbyState(lobbyConfig, localUserId, countryConfigs, friends, receiveGameEventsByMail);
						
			// Enter frame handler
			Application.application.addEventListener(Event.ENTER_FRAME,
				function (e : Event) : void
				{
					lobbyState.enterFrame();
				});
				
			// Send button
			Application.application.sendButton.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void
				{
					sendText();
				});		
				
			// Play game button
			Application.application.playGameButton.addEventListener(MouseEvent.CLICK,
				function (e : MouseEvent) : void
				{
					lobbyState.playGame();
				});	
			
			// Shortcut key for send
			Application.application.editMessage.addEventListener(KeyboardEvent.KEY_DOWN,
				function (e : KeyboardEvent) : void
				{
					if (e.keyCode == Keyboard.ENTER)
						sendText();
				});
				
			// Fix scrolling
			Application.application.receivedMessages.addEventListener(FlexEvent.UPDATE_COMPLETE,
				function (e : FlexEvent) : void
				{
					Application.application.receivedMessages.verticalScrollPosition = Application.application.receivedMessages.maxVerticalScrollPosition;
				});				

			// Setup legend
			Application.application.legendImage1.addChild(LobbyTile.getHexagonStateBitmap(0, 0, LobbyTile.stateRed));
			Application.application.legendImage1.addChild(LobbyTile.getHexagonStateBitmap(19, 0, LobbyTile.stateBlue));
			Application.application.legendImage2.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army == Army.red? LobbyTile.stateDefendableRed : LobbyTile.stateDefendableBlue));
			Application.application.legendImage3.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army == Army.red? LobbyTile.stateAttackableRed : LobbyTile.stateAttackableBlue));
			Application.application.legendImage4.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army == Army.red? LobbyTile.stateIAttackedWaitingForDefenderRed : LobbyTile.stateIAttackedWaitingForDefenderBlue));
			Application.application.legendImage5.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army == Army.red? LobbyTile.stateOtherAttackedICanDefendRed : LobbyTile.stateOtherAttackedICanDefendBlue));
			Application.application.legendImage6.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army != Army.red? LobbyTile.stateDefendableFriendlyGameRed : LobbyTile.stateDefendableFriendlyGameBlue));
			Application.application.legendImage7.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army == Army.red? LobbyTile.stateDefendedByMeRed : LobbyTile.stateDefendedByMeBlue));
			Application.application.legendImage8.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army != Army.red? LobbyTile.stateDefendedByOtherRed : LobbyTile.stateDefendedByOtherBlue));
			Application.application.legendImage9.addChild(LobbyTile.getHexagonStateBitmap(10, 0, army == Army.red? LobbyTile.stateUnusableRed : LobbyTile.stateUnusableBlue));
			Application.application.legendImage10.addChild(LobbyTile.getHexagonStateBitmap(0, 0, LobbyTile.stateCapturePointRed));
			Application.application.legendImage10.addChild(LobbyTile.getHexagonStateBitmap(19, 0, LobbyTile.stateCapturePointBlue));
			Application.application.legendImage11.addChild(LobbyTile.getHexagonStateBitmap(0, 0, LobbyTile.stateGameInProgressRed));
			Application.application.legendImage11.addChild(LobbyTile.getHexagonStateBitmap(19, 0, LobbyTile.stateGameInProgressBlue));
			
			// Remove loading popup
			Application.application.msgBox.close();
		}

		// Called after finished loading the data on failure
		private static function loadFailed() : void 
		{
			Application.application.msgBox.show("Could not reach server!");
		}

		// Main loop for the editor
		public static function run() : void
		{
			// Initialize logger
			Logger.init();
			
			// Setup enableUI function
			MsgBox.enableUI = enable;
			ChallengeWindow.enableUI = enable;
			
			// Configure lobby object
			lobbyConfig = Application.application.lobbyConfig;
			lobbyConfig.createTiles(
				function(sel : LobbyTile) : void { lobbyState.tilePressed(sel); }, 
				function(sel : LobbyTile) : void { lobbyState.tileReleased(sel); }, 
				function(sel : LobbyTile) : void { lobbyState.tileRollOver(sel); }, 
				function(sel : LobbyTile) : void { lobbyState.tileRollOut(sel); },
				false);
			lobbyConfig.setAllTilesVisible(false);

			// Create request				
			var data : String; 
			if (Application.application.parameters.lobbyId != null)
				data = "lobbyId=" + Application.application.parameters.lobbyId;

			// Send load request
			Application.application.msgBox.show("Loading lobby...");
			Tools.processRequest("LobbyLoad.do", data, loadComplete, loadFailed);
		}
	}
}
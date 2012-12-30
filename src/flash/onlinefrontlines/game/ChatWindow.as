package onlinefrontlines.game
{
	import mx.core.Application;
	import mx.containers.Canvas;
	import mx.controls.Button;
	import mx.controls.TextInput;
	import mx.controls.TextArea;
	import mx.controls.Image;
	import mx.events.FlexEvent;
	import flash.ui.Keyboard;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Window that allows users to chat
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
	public class ChatWindow extends ToolWindow
	{
		// Background image
		[Embed(source='../../assets/chat_window/chatwindow.png')]
		private static var backgroundClass : Class;

		// Game state
		private var gameState : GameState;
		
		// Output text area
		private var textArea : TextArea;
		private var textInput : TextInput;
		private var sendButton : Button;

		// Constructor
		public function ChatWindow()
		{
			// Construct base class
			super(200, 139);

			// Create background
			var backgroundImage : Image = new Image();
			backgroundImage.source = backgroundClass;
			clientCanvas.addChild(backgroundImage);

			// Create text area
			textArea = new TextArea();
			textArea.x = 10;
			textArea.y = 9;
			textArea.width = 181;
			textArea.height = 91;
			textArea.editable = false;
			textArea.styleName = "chatTextField";
			clientCanvas.addChild(textArea);

			// Create text input
			textInput = new TextInput();
			textInput.x = 10;
			textInput.y = 110;
			textInput.width = 135;
			textInput.height = 20;
			textInput.styleName = "chatTextField";
			clientCanvas.addChild(textInput);
			
			// Create send button
			sendButton = new Button();
			sendButton.x = 146;
			sendButton.y = 113;
			sendButton.width = 48;
			sendButton.height = 17;
			sendButton.label = "Send";
			sendButton.styleName = "sendButton";
			sendButton.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void 
				{ 
					sendText();
				});
			clientCanvas.addChild(sendButton);

			// Shortcut key for send
			textInput.addEventListener(KeyboardEvent.KEY_DOWN,
				function (e : KeyboardEvent) : void
				{
					if (visible && e.keyCode == Keyboard.ENTER)
						sendText();
				});
				
			// Fix scrolling
			textArea.addEventListener(FlexEvent.UPDATE_COMPLETE,
				function (e : FlexEvent) : void
				{
					textArea.verticalScrollPosition = textArea.maxVerticalScrollPosition;
				});	

			// Initially hide
			visible = false;
		}
		
		// Close callback
		public override function onClosed() : void
		{
			Application.application.showChat.selected = false;
		}
		
		// Callback that state changed and state might need to be saved
		public override function onStateChanged() : void
		{
			LocalSettings.getInstance().saveChatWindowState();
		} 

		// Set game state
		public function setGameState(gameState : GameState) : void
		{
			this.gameState = gameState;
			
			// Disable chat when player is only watching
			textInput.enabled = gameState.localPlayer != Faction.none;
			sendButton.enabled = gameState.localPlayer != Faction.none;
		}
		
		// Add text
		public function addText(text : String) : void
		{
			// Add string
			textArea.text += text + "\n";
			
			// Show window
			Application.application.showChat.selected = true;
		}

		// Send text input		
		private function sendText() : void
		{
			var msg : String = textInput.text; 
			if (msg == "@tips")
			{
				Application.application.tipsWindow.show(null);
			}
			else if (msg == "@log")
			{
				Logger.show();
			}
			else if (msg == "@annotate")
			{
				Application.application.annotation.visible = !Application.application.annotation.visible;
			}				
			else if (msg == "@fow")
			{
				gameState.fogOfWarEnabled = !gameState.fogOfWarEnabled;
				gameState.updateFogOfWar();
			}
			else if (msg != "")
			{
				// Split escaped strings into multiple text messages if too long 
				var maxLen : int = 200;
				var esc : String = escape(msg);
				do
				{	
					// Don't split in the middle of an encoded character 
					var splitPos : int = esc.lastIndexOf("%", maxLen);
					if (maxLen - splitPos > 2)
						splitPos = maxLen;
						
					// Split line and send
					var line : String = esc.substr(0, splitPos);
					esc = esc.substr(splitPos); 			
					gameState.request(new ActionTextMessage(gameState.getLocalPlayerId(), unescape(line)));
				}
				while (esc.length > 0);
				
				// Show help balloon if opponent offline
				if (!gameState.isPlayerConnected(gameState.localPlayer == Faction.f1? 1 : 0))
					HelpBalloon.queueCenterOnDisplayObject(gameState.localPlayer == Faction.f1? Application.application.playerName2 : Application.application.playerName1,
						"Your opponent is offline (his name is red). Your message will be delivered when he goes online.", true);
			}
			textInput.text = "";
		}
	}
}
package onlinefrontlines.game
{
	import mx.core.Application;
	import mx.controls.Label;
	import mx.controls.Image;
	import mx.containers.Canvas;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import mx.controls.Button;
	
	/*
	 * Window that shows the current score
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
	public class ScoreWindow extends ToolWindow
	{
		// Background image
		[Embed(source='../../assets/score_window/score_window.png')]
		private static var backgroundClass : Class;
		[Embed(source='../../assets/score_window/button_background.png')]
		private static var buttonBackgroundClass : Class;

		// Data
		private var gameState : GameState;
		private var textElements : Array = new Array();
		
		// Constructor
		public function ScoreWindow()
		{
			// Construct base class
			super(168 * 2, 266);

			// Initially hide
			visible = false;
		}
				
		// Show menu
		public function show(gameState : GameState) : void
		{
			// Store game state
			this.gameState = gameState;
			
			// Close previous menu
			close();	
			
			// Create panels
			createPanel(0, gameState.getPlayer(Faction.f1));
			createPanel(168, gameState.getPlayer(Faction.f2));
			
			// Add button background
			var background : Image = new Image();
			background.y = 238;
			background.source = buttonBackgroundClass;
			clientCanvas.addChild(background);
			
			// Add close button			
			var button : Button = new Button();
			button.x = 120;
			button.y = 238;
			button.width = 96;
			button.height = 17;
			button.styleName = "messageBoxButton";
			button.label = "Close";
			button.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void 
				{ 	
					onClosed();
				});
			clientCanvas.addChild(button);

			// Set texts 
			updateText(Faction.f1);
			updateText(Faction.f2);

			// Add listeners
			Application.application.addEventListener(Event.ENTER_FRAME, onEnterFrame);
				
			// Show
			visible = true;
		}

		// Close the window
		public function close() : void
		{
			if (visible)
			{
				// Remove listeners		
				Application.application.removeEventListener(Event.ENTER_FRAME, onEnterFrame);
				
				// Get rid of data
				clientCanvas.removeAllChildren();
				textElements = new Array();
				
				// Hide
				visible = false;
			}
		}
		
		// Close callback
		public override function onClosed() : void
		{
			// Uncheck
			Application.application.showScore.selected = false;
		}
		
		// Callback that state changed and state might need to be saved
		public override function onStateChanged() : void
		{
			LocalSettings.getInstance().saveScoreWindowState();
		} 

		// Add one panel
		private function createPanel(x : int, player : Player) : void
		{
			// Add user panel
			UserPanel.create(clientCanvas, x, 0, player);
		
			// Add background
			var background : Image = new Image();
			background.x = x;
			background.y = 69;
			background.source = backgroundClass;
			clientCanvas.addChild(background);			

			// Add text fields
			addText(background, 38, 30, 3);
			addText(background, 122, 30, 4);
			addText(background, 38, 56, 3);
			addText(background, 122, 56, 4);
			addText(background, 38, 82, 3);
			addText(background, 122, 82, 4);
			addText(background, 122, 107, 4);
			addText(background, 122, 132, 4);
		}
		
		// Add a label on the screen
		private function addText(image : Image, x : int, y : int, numDigits : int) : void
		{
			var text : BitmapText = new BitmapText(image, numDigits, 0, BitmapText.typeMenu);
			text.setPosition(x, y);
			textElements.push(text);
		}

		// Enter frame event handler
		private function onEnterFrame(e : Event) : void
		{
			updateText(Faction.f1);
			updateText(Faction.f2);
		}
		
		// Update text for one faction
		private function updateText(faction : int) : void
		{
			var base : int = (faction - 1) * 8;
			var score : Score = gameState.getScore(faction);
			
			textElements[base + 0].setValue(score.numberOfUnitsDestroyed);
			textElements[base + 1].setValue(score.victoryPointsForUnits);
			textElements[base + 2].setValue(score.numberOfBasesDestroyed);
			textElements[base + 3].setValue(score.victoryPointsForBases);
			textElements[base + 4].setValue(score.numberOfTilesOwned);
			textElements[base + 5].setValue(score.victoryPointsForTiles);
			textElements[base + 6].setValue(score.getTotalScore());
			textElements[base + 7].setValue(gameState.scoreLimit);
		}
	}
}
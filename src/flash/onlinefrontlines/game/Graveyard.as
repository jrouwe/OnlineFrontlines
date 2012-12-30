package onlinefrontlines.game
{
	import mx.core.Application;
	import mx.controls.Label;
	import mx.controls.Image;
	import mx.containers.Canvas;
	import flash.events.Event;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Window that displays deceased units
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
	public class Graveyard extends ToolWindow
	{
		// Background image
		[Embed(source='../../assets/graveyard/graveyard.png')]
		private static var backgroundClass : Class;

		// Data
		private var gameState : GameState;
		private var numUnitsInGraveyard : int;
		
		// Constructor
		public function Graveyard()
		{
			// Construct base class
			super(168 * 2, 273);

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
			createPanels();

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
				
				// Hide
				visible = false;
			}
		}
		
		// Close callback
		public override function onClosed() : void
		{
			Application.application.showGraveyard.selected = false;
		}
		
		// Callback that state changed and state might need to be saved
		public override function onStateChanged() : void
		{
			LocalSettings.getInstance().saveGraveyardState();
		} 

		// Create both panels
		private function createPanels() : void
		{
			createPanel(0, Faction.f1);
			createPanel(168, Faction.f2);

			numUnitsInGraveyard = gameState.getScore(Faction.f1).graveyard.length + gameState.getScore(Faction.f2).graveyard.length;
		}
		
		// Add one panel
		private function createPanel(x : int, faction : int) : void
		{
			var player : Player = gameState.getPlayer(faction);
			var score : Score = gameState.getScore(faction);

			// Add user panel
			UserPanel.create(clientCanvas, x, 0, player);

			// Add background
			var background : Image = new Image();
			background.x = x;
			background.y = 69;
			background.source = backgroundClass;
			clientCanvas.addChild(background);
			
			// Add canvas for units
			var unitCanvas : Canvas = new Canvas();
			unitCanvas.x = x;
			unitCanvas.y = 78;
			unitCanvas.width = 162;
			unitCanvas.height = 186;
			unitCanvas.clipContent = true;
			unitCanvas.horizontalScrollPolicy = "off";
			unitCanvas.verticalScrollPolicy = "auto";
			clientCanvas.addChild(unitCanvas);			
			
			// Add layer for units
			var unitRoot : Image = new Image();
			unitCanvas.addChild(unitRoot);
			
			// Display images
			var ix : int = 0;
			var iy : int = 0;
			for each (var u : UnitState in score.graveyard)
			{
				var gx : int = 12 + ix * 26;
				var gy : int = iy * 26;
				
				var unitGraphics : UnitGraphics = new UnitGraphics(u, unitRoot, null, false, null);
				unitGraphics.moveTo(gx, gy);
				unitGraphics.setFacingLeft(faction == Faction.f2);
				
				var toolTip : Label = new Label();
				toolTip.x = gx;
				toolTip.y = gy;
				toolTip.width = 26;
				toolTip.height = 26;
				toolTip.toolTip = u.unitConfig.name;
				unitCanvas.addChild(toolTip); 
							
				ix++;
				if (ix >= 5)
				{
					ix = 0;
					iy++;
				}
			}			 
		}
		
		// Enter frame event handler
		private function onEnterFrame(e : Event) : void
		{
			// Recreate panels if something changed
			var newNumUnitsInGraveyard : int = gameState.getScore(Faction.f1).graveyard.length + gameState.getScore(Faction.f2).graveyard.length;
			if (newNumUnitsInGraveyard != numUnitsInGraveyard)
				createPanels();
		}
	}
}
package onlinefrontlines.game
{
	import mx.controls.Image;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import mx.core.Application;
	
	/*
	 * Window when game has ended
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
	public class VictoryWindow extends MsgBox
	{
		// Images
		[Embed(source='../../assets/end_game/win_screen_red.png')]
		private static var winRedClass : Class;
		[Embed(source='../../assets/end_game/win_screen_blue.png')]
		private static var winBlueClass : Class;
		[Embed(source='../../assets/end_game/lose_screen_red.png')]
		private static var loseRedClass : Class;
		[Embed(source='../../assets/end_game/lose_screen_blue.png')]
		private static var loseBlueClass : Class;
		[Embed(source='../../assets/end_game/draw_screen.png')]
		private static var drawClass : Class;
		[Embed(source='../../assets/end_game/end_result_screen.png')]
		private static var statsBackgroundClass : Class;

		// Constructor
		public function VictoryWindow()
		{
			// Initially hide
			visible = false;
		}
				
		// Add a label on the screen
		private function addText(image : Image, x : int, y : int, numDigits : int, dotLocation : int, value : int) : void
		{
			var text : BitmapText = new BitmapText(image, numDigits, dotLocation, BitmapText.typeMenu);
			text.setPosition(x, y);
			text.setValue(value);
		}

		// Show menu
		public function showVictory(gameState : GameState) : void
		{
			// Check if local player is not watching
			if (gameState.localPlayer == Faction.f1 || gameState.localPlayer == Faction.f2)
			{			
				// Close previous box
				close();	
				
				// Disable other user interface elements
				enableUI(false);
				
				// Hide score window (it will contain incorrect info as score has not been updated)
				Application.application.showScore.selected = false;
				
				// Add background image
				var bg : Image = new Image();
				bg.source = statsBackgroundClass;
				addChild(bg);

				// Add victory image
				var scoreMultiplier : Number = 1;
				var image : Image = new Image();
				image.x = 7;
				image.y = 7;
				switch (gameState.winningFaction)
				{
				case Faction.f1:
				case Faction.f2:
					if (gameState.localPlayer == gameState.winningFaction)
					{
						image.source = Faction.faction1IsRed == (gameState.localPlayer == Faction.f1)? winRedClass : winBlueClass;
						scoreMultiplier = 2;
					}
					else
					{
						image.source = Faction.faction1IsRed == (gameState.localPlayer == Faction.f1)? loseRedClass : loseBlueClass;
						scoreMultiplier = 0.5;
					}
					break;
					
				default:
					image.source = drawClass;
					scoreMultiplier = 1;
					break;
				}
				addChild(image);
				
				// Add texts
				var score : Score = gameState.getScore(gameState.localPlayer);
				var player : Player = gameState.getPlayer(gameState.localPlayer);
				addText(bg, 232, 130, 4, 0, player.level);
				addText(bg, 42, 180, 3, 0, score.numberOfUnitsDestroyed);
				addText(bg, 232, 180, 4, 0, score.victoryPointsForUnits);
				addText(bg, 42, 206, 3, 0, score.numberOfBasesDestroyed);
				addText(bg, 232, 206, 4, 0, score.victoryPointsForBases);
				addText(bg, 42, 232, 3, 0, score.numberOfTilesOwned);
				addText(bg, 232, 232, 4, 0, score.victoryPointsForTiles);
				addText(bg, 225, 257, 4, 1, 10 * scoreMultiplier);
				addText(bg, 232, 282, 4, 0, scoreMultiplier * score.getTotalScore());
				addText(bg, 232, 307, 4, 0, player.pointsNeededToNextLevel);
				
				// Create ok button
				createButton((width - 96) / 2, 344, "OK", null);

				// Show window
				visible = true;
			}
		}
	} 
}
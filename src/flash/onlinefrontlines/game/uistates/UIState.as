package onlinefrontlines.game.uistates
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Base class user interface state
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
	public class UIState 
	{
		protected var gameState : GameState;
	
		// Set game state
		public function setGameState(gameState : GameState) : void
		{
			this.gameState = gameState;
		}
		
		// Called when entering this state
		public function onEnterState() : void
		{
		}
		
		// If this state is playing an animation
		public function isPlaying() : Boolean
		{ 
			return false;
		}
		
		// Called when leaving this state
		public function onLeaveState() : void
		{
		}
		
		// Called when mouse is pressed over a tile
		public function onMousePressed(tile : MapTile) : void
		{
		}
		
		// Called when mouse is released over a tile
		public function onMouseReleased(tile : MapTile) : void
		{
		}
		
		// Called when mouse starts hovering over a tile
		public function onMouseRollOver(tile : MapTile) : void
		{
		}
		
		// Called when mouse stops hovering over a tile
		public function onMouseRollOut(tile : MapTile) : void
		{
		}
		
		// Called every frame
		public function onUpdate() : void
		{
		}
	}
}

package onlinefrontlines.game.uistates
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * End turn
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
	public class UIStateEndTurn extends UIState 
	{
		// Sounds
		[Embed(source="../../../assets/sounds/end_turn.mp3")]
		private static var endTurnClass : Class;
		
		// If score should be shown or not
		private var showScore : Boolean;
		
		// Constructor
		public function UIStateEndTurn(showScore : Boolean)
		{ 
			this.showScore = showScore;
		}

		// Called when entering state
		public override function onEnterState() : void
		{
			// Play sound
			SoundSystem.play(endTurnClass);

			// Show results			
			if (showScore && !Application.application.showScore.selected)
				Application.application.showScore.selected = true;

			// Switch state
			gameState.toState(new UIStateSelectingUnit());
		}
	}
}

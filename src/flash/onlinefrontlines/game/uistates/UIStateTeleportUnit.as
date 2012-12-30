package onlinefrontlines.game.uistates
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Teleport unit (during deployment)
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
	public class UIStateTeleportUnit extends UIState 
	{
		// Sounds
		[Embed(source="../../../assets/sounds/unit_teleported.mp3")]
		private static var unitTeleportedClass : Class;

		private var selectedUnit : UnitState;
		private var fromContainer : UnitState;
		
		// Constructor
		public function UIStateTeleportUnit(selectedUnit : UnitState, fromContainer : UnitState)
		{ 
			this.selectedUnit = selectedUnit;
			this.fromContainer = fromContainer;
		}

		// Called when entering state
		public override function onEnterState() : void
		{
			// Update graphics			
			if (fromContainer != null)
				fromContainer.graphics.updateGraphics();
			
			if (selectedUnit.container != null)
			{
				selectedUnit.graphics.setVisible(false);
				selectedUnit.container.graphics.updateGraphics();
			}
			else
			{
				selectedUnit.graphics.setVisible(true);
				selectedUnit.graphics.moveToCurrentTile();
				selectedUnit.graphics.updateGraphics();
			}
			
			// Play sound
			SoundSystem.play(unitTeleportedClass);
			
			// Switch state
			if (selectedUnit.faction == gameState.localPlayer)
				gameState.toState(new UIStateSettingUp());
			else
				gameState.toState(null);
		}
	}
}

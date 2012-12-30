package onlinefrontlines.game.uistates
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Creating / removing unit
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
	public class UIStateCreateRemoveUnit extends UIState 
	{
		private var unit : UnitState;
		private var container : UnitState;
		private var create : Boolean;
		
		// Constructor
		public function UIStateCreateRemoveUnit(unit : UnitState, container : UnitState, create : Boolean)
		{ 
			this.unit = unit;
			this.container = container;
			this.create = create;
		}

		// Called when entering state
		public override function onEnterState() : void
		{
			if (create)
			{
				// Create graphics
				unit.graphics = new UnitGraphics(unit, gameState.mapConfig.unitRoot, gameState.mapConfig.unitTextRoot, true, gameState.mapConfig);

				// Init graphics
				if (container != null)
				{
					unit.graphics.setVisible(false);
					if (container.graphics != null)
						container.graphics.updateGraphics();
				}
				else
				{
					unit.graphics.moveToCurrentTile();
					unit.graphics.setVisible(true);
				}
			}
			else
			{
				// Remove graphics
				if (container != null && container.graphics != null)
					container.graphics.updateGraphics();

				// Destroy graphics
				unit.graphics.destroy();
				unit.graphics = null;
			}

			// Switch state
			if (gameState.turnNumber == 0)
				gameState.toState(new UIStateSettingUp());
			else
				gameState.toState(new UIStateSelectingUnit());
		}
	}
}

package onlinefrontlines.game.uistates
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Revert the effects of an attack
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
	public class UIStateUnitAttackingUndo extends UIState
	{
		private var selectedUnit : UnitState;
		private var targettedUnit : UnitState;
		
		public function UIStateUnitAttackingUndo(selectedUnit : UnitState, targettedUnit : UnitState)
		{
			this.selectedUnit = selectedUnit;
			this.targettedUnit = targettedUnit;
		}

		public override function onEnterState() : void
		{
			selectedUnit.graphics.updateGraphics();	
			selectedUnit.graphics.setVisible(true);
				
			targettedUnit.graphics.updateGraphics();		
			targettedUnit.graphics.setVisible(true);
		}
		
		public override function onLeaveState() : void
		{
		}
		
		public override function onUpdate() : void
		{
			gameState.toState(new UIStateUnitSelected(selectedUnit));
		}
	}
}

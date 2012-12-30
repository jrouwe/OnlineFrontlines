package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * One unit attacks another unit
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
	public class ActionAttackUnit extends Action 
	{
		private var selectedUnit : UnitState;
		private var targettedUnit : UnitState;
		
		// Undo info
		private var oldScores : Array = [ new Score(), new Score() ];
		private var oldStateSelectedUnit : UnitState;
		private var oldStateTargettedUnit : UnitState;
		
		// Redo info
		private var newStateSelectedUnit : UnitState;
		private var newStateTargettedUnit : UnitState;
		
		// Constructor
		public function ActionAttackUnit(selectedUnit : UnitState, targettedUnit : UnitState) : void
		{
			this.selectedUnit = selectedUnit;
			this.targettedUnit = targettedUnit;
		}
	
		// Apply the action
		public override function doAction() : void
		{
			// Save state
			oldStateSelectedUnit = selectedUnit.saveState();
			oldStateTargettedUnit = targettedUnit.saveState();
			oldScores[0] = gameState.scores[0].saveState();
			oldScores[1] = gameState.scores[1].saveState();
			
			// Deduct action point
			selectedUnit.actionsLeft--;
			selectedUnit.lastActionWasMove = false;	
			if (selectedUnit.actionsLeft <= 0)
				selectedUnit.movementPointsLeft = 0;		

			if (newStateTargettedUnit == null)
			{
				// Get terrain types	
				var selectedUnitTerrain : TerrainConfig = gameState.getAttackTerrain(selectedUnit);
				var targettedUnitTerrain : TerrainConfig = gameState.getAttackTerrain(targettedUnit);
	
				// Perform attack	
				selectedUnit.attackAndCounter(targettedUnit, selectedUnitTerrain, targettedUnitTerrain);
			}
			else
			{
				// Apply results
				selectedUnit.armour = newStateSelectedUnit.armour;
				selectedUnit.ammo = newStateSelectedUnit.ammo;
				selectedUnit.faction = newStateSelectedUnit.faction;
				
				targettedUnit.armour = newStateTargettedUnit.armour;
				targettedUnit.ammo = newStateTargettedUnit.ammo;
				targettedUnit.faction = newStateTargettedUnit.faction;
			}
			
			// Check selected unit destroyed
			if (selectedUnit.armour <= 0)
			{
				calculateScore(selectedUnit);
				gameState.removeUnit(selectedUnit);
			}
			else if (selectedUnit.faction != oldStateSelectedUnit.faction)
			{
				calculateScore(selectedUnit);
				selectedUnit.containedUnits = new Array();
			}
			
			// Check targetted unit destroyed
			if (targettedUnit.armour <= 0)
			{
				calculateScore(targettedUnit);
				gameState.removeUnit(targettedUnit);
			}
			else if (targettedUnit.faction != oldStateTargettedUnit.faction)
			{
				calculateScore(targettedUnit);
				targettedUnit.containedUnits = new Array();
			}

			// Set new owner			
			gameState.updateTerrainOwner(selectedUnit);
			gameState.updateTerrainOwner(targettedUnit);

			// Save state
			newStateSelectedUnit = selectedUnit.saveState();
			newStateTargettedUnit = targettedUnit.saveState();
			
			// Return new state	
			gameState.toState(new UIStateUnitAttacking(oldStateSelectedUnit, selectedUnit, oldStateTargettedUnit, targettedUnit));
		}
		
		/**
		 * Recursively determine score for units lost
		 */
		public function calculateScore(unit : UnitState) : void
		{
			// Get score for units opposite faction
			var oppositeScore : Score = gameState.getScore(Faction.opposite(unit.initialFaction));
			
			if (!unit.unitConfig.isBase)
			{
				// Add to graveyard
				var score : Score = gameState.getScore(unit.initialFaction);
				score.graveyard.push(unit);
				
				// Normal unit
				oppositeScore.numberOfUnitsDestroyed++;
				oppositeScore.victoryPointsForUnits += unit.unitConfig.victoryPoints;
			}
			else
			{
				// Base				
				if (unit.faction != unit.initialFaction)
				{
					// Base was captured, add score
					oppositeScore.numberOfBasesDestroyed++;
					oppositeScore.victoryPointsForBases += unit.unitConfig.victoryPoints;
				}
				else
				{
					// Base was recaptured, deduct score
					oppositeScore.numberOfBasesDestroyed--;
					oppositeScore.victoryPointsForBases -= unit.unitConfig.victoryPoints;
				}
			}
			
			for each (var c : UnitState in unit.containedUnits)
				calculateScore(c);
		}		
		
		// Undo the action
		public override function undoAction() : void
		{
			// Restore selected unit			
			if (selectedUnit.armour > 0)
				gameState.removeUnit(selectedUnit);
			selectedUnit.restoreState(oldStateSelectedUnit);
			gameState.addUnit(selectedUnit);

			// Restore targetted unit
			if (targettedUnit.armour > 0)
				gameState.removeUnit(targettedUnit);
			targettedUnit.restoreState(oldStateTargettedUnit);
			gameState.addUnit(targettedUnit);				

			// Restore owner
			gameState.updateTerrainOwner(selectedUnit);
			gameState.updateTerrainOwner(targettedUnit);

			// Restore scores
			gameState.scores[0].restoreState(oldScores[0]);
			gameState.scores[1].restoreState(oldScores[1]);
			
			// Switch state			
			gameState.toState(new UIStateUnitAttackingUndo(selectedUnit, targettedUnit));
		}	
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			// Get selected unit
			selectedUnit = gameState.getUnitById(int(param[1]));
			
			// Get targetted unit
			targettedUnit = gameState.getUnitById(int(param[2]));
			
			// Get new state selected unit
			newStateSelectedUnit = selectedUnit.saveState();
			newStateSelectedUnit.armour = int(param[3]);
			newStateSelectedUnit.ammo = int(param[4]);
			newStateSelectedUnit.faction = int(param[5]);
			
			// Get new state targetted unit
			newStateTargettedUnit = targettedUnit.saveState();
			newStateTargettedUnit.armour = int(param[6]);
			newStateTargettedUnit.ammo = int(param[7]);
			newStateTargettedUnit.faction = int(param[8]);
		}
		
		// Convert action to a string
		public override function toString() : String
		{
			return "a," 
				+ selectedUnit.id + "," 
				+ targettedUnit.id + "," 
				+ "-1," 
				+ "-1," 
				+ "-1," 
				+ "-1," 
				+ "-1," 
				+ "-1";
		}
	}
}

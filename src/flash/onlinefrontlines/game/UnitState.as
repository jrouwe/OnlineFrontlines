package onlinefrontlines.game
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Current state of a unit
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
	public class UnitState
	{
		// State
		public var id : int;
		public var unitConfig : UnitConfig;
		public var initialUnitConfig : UnitConfig;
		public var locationX : int;
		public var locationY : int;
		public var initialFaction : int; // of type Faction
		public var faction : int; // of type Faction
		public var armour : int;
		public var ammo : int;
		public var movementPointsLeft : int = 0;
		public var actionsLeft : int = 0;
		public var lastActionWasMove : Boolean = false;
		public var containedUnits : Array = new Array(); // of UnitState
		public var container : UnitState;
		public var canPerformAction : Boolean;
		public var graphics : UnitGraphics;
		
		/**
		 * Constructor
		 */
		public function UnitState(id : int, locationX : int, locationY : int, faction : int, unitConfig : UnitConfig) : void
		{
			// Fill in properties
			this.id = id;
			this.locationX = locationX;
			this.locationY = locationY;
			this.initialFaction = faction;
			this.faction = faction;
			init(unitConfig);
		}
		
		/**
		 * Initialize from unit config
		 */
		public function init(unitConfig : UnitConfig) : void
		{
			this.unitConfig = unitConfig;
			this.initialUnitConfig = unitConfig;
			this.armour = unitConfig.maxArmour;
			this.ammo = unitConfig.maxAmmo;
		}
		
		/** 
		 * Save state of a unit
		 */
		public function saveState() : UnitState
		{
			var u : UnitState = new UnitState(id, locationX, locationY, faction, unitConfig);

			u.armour = armour;
			u.ammo = ammo;
			u.movementPointsLeft = movementPointsLeft;
			u.actionsLeft = actionsLeft;
			u.lastActionWasMove = lastActionWasMove;			
			u.container = container;
			
			u.containedUnits = new Array();
			for each (var c : UnitState in containedUnits)
				u.containedUnits.push(c);
	
			return u;
		}
		
		/**
		 * Restore state of a unit
		 */
		public function restoreState(s : UnitState) : void
		{
			id = s.id;
			unitConfig = s.unitConfig;
			locationX = s.locationX;
			locationY = s.locationY;
			faction = s.faction;
			armour = s.armour;
			ammo = s.ammo;
			movementPointsLeft = s.movementPointsLeft;
			actionsLeft = s.actionsLeft;
			lastActionWasMove = s.lastActionWasMove;
			container = s.container;
			
			containedUnits = new Array();
			for each (var c : UnitState in s.containedUnits)
				containedUnits.push(c);
		}
		
		/**
		 * Return the highest attack strength possible for this unit
		 */
		public function getMaxStrength() : int
		{
			if (unitConfig.maxArmour == 0)
				return 0;
			else
				return Math.ceil((unitConfig.getMaxStrength(ammo > 0) * armour) / unitConfig.maxArmour);
		}
		
		/**
		 * Get distance to specified tile
		 */
		public function getDistanceTo(x : int, y : int) : int
		{
			return HexagonGrid.getDistance(locationX, locationY, x, y);
		}						
		
		/**
		 * Transform to other unit type
		 */
		public function transform() : void
		{
			// Get new config
			var config : UnitConfig = UnitConfig.allUnitsMap[unitConfig.transformableToUnitId];
			
			// Transform
			armour = (config.maxArmour * armour) / unitConfig.maxArmour;
			unitConfig = config;
		}			
		
		/**
		 * Check if a unit can be contained in this unit
		 */
		public function canHold(unit : UnitState) : Boolean
		{
			return unit.faction == faction
				&& container == null
				&& unit.container == null
				&& unitConfig.containerUnitIds.indexOf(unit.unitConfig.id) >= 0
				&& unitConfig.containerMaxUnits > 0
				&& containedUnits.length < unitConfig.containerMaxUnits;
		}
		
		/**
		 * Add unit as a contained unit
		 */
		public function addUnit(unit : UnitState) : void
		{
			// Add to container
			containedUnits.push(unit);
			unit.container = this;
			unit.locationX = -1000;
			unit.locationY = -1000;
	
			// Transform unit if needed
			var otherConfig : UnitConfig = UnitConfig.allUnitsMap[unit.unitConfig.transformableToUnitId];					
			if (unit.unitConfig.transformableType == TransformableType.inBase
				&& unitConfig.containerUnitIds.indexOf(otherConfig.id) >= 0)
				unit.transform();
		}
		
		/**
		 * Remove unit as contained unit
		 */
		public function removeUnit(unit : UnitState) : void
		{
			// Remove from array
			var i : int = containedUnits.indexOf(unit);
			containedUnits.splice(i, 1);			 
			unit.container = null;
		}

		/**
		 * Check if this unit can attack another unit of type unitClass
		 */
		public function canAttackUnitClass(unitClass : int) : Boolean
		{
			var sp : UnitStrengthProperties = unitConfig.getStrengthProperties(unitClass);
			return sp.getStrength(ammo > 0) > 0;
		}
		
		/**
		 * Check if the unit can attack another unit
		 */
		public function canAttack(unit : UnitState) : Boolean
		{
			return canAttackFrom(locationX, locationY, unit);
		}	
		
		/**
		 * Check if the unit can counter attack another unit
		 */
		public function canCounterAttack(unit : UnitState) : Boolean
		{
			return canCounterAttackFrom(locationX, locationY, unit);
		}	
		
		/**
		 * Check if the unit can attack another unit from position x, y
		 */
		public function canAttackFrom(x : int, y : int, unit : UnitState) : Boolean
		{
			return actionsLeft > 0
				&& canCounterAttackFrom(x, y, unit);
		}	

		/**
		 * Check if the unit can counter attack another unit from position x, y
		 */
		public function canCounterAttackFrom(x : int, y : int, unit : UnitState) : Boolean
		{
			return faction != unit.faction
				&& armour > 0
				&& container == null
				&& unit.container == null
				&& canAttackUnitClass(unit.unitConfig.unitClass)
				&& unit.getDistanceTo(x, y) <= unitConfig.getStrengthProperties(unit.unitConfig.unitClass).attackRange;
		}	
		
		/**
		 * Execute an attack from this unit to another unit (call again with parameters swapped to perform the counter attack)
		 */
		public function attack(unit : UnitState, attackerTerrain : TerrainConfig, defenderTerrain : TerrainConfig, externalModifier : int) : void
		{
			// Check if other unit alive
			if (unit.armour <= 0)
				return;
				
			// Check if unit has armour
			if (unitConfig.maxArmour <= 0)
				return;
				
			// Get strength properties
			var sp : UnitStrengthProperties = unitConfig.getStrengthProperties(unit.unitConfig.unitClass);
				
			// Get strength
			var strength : Number = Number(sp.getStrength(ammo > 0)) * armour / unitConfig.maxArmour;
			if (strength <= 0)
				return;
			
			// Calculate maximum armour loss for target
			var maxArmourLoss : Number = Math.max(strength, 3.0);
			
			// Get terrain modifier multiplier
			var modifier : Number = 1.0 + (Number(attackerTerrain.strengthModifier) - defenderTerrain.strengthModifier) / 100.0;
	
			// Get uniformly distributed random number
			var average : Number = 0.5 * modifier * maxArmourLoss;
			var standardDeviation : Number = 0.15 * maxArmourLoss;
			var rnd : Number = Tools.gaussianRandom(average, standardDeviation);
	
			// Calculate loss
			var armourLoss : int = Math.min(Math.max(Math.ceil(rnd), 0), maxArmourLoss);
			
			// Subtract loss
			unit.armour -= armourLoss;
			if (unit.armour <= 0)
			{
				if (!unit.unitConfig.isBase)
				{
					// This is not a base so it's broken now
					unit.armour = 0;		
				}
				else
				{
					// Bases change faction when destroyed
					unit.faction = faction;
					unit.armour = unit.unitConfig.maxArmour;
				}
			}
	
			// Decrease ammo
			if (sp.maxStrengthWithAmmo > 0)
			{
				--ammo;
				if (ammo < 0)
					ammo = 0;
			}
		}
		
		/**
		 * Execute an attack from this unit to another unit, then perform the counter attack
		 */
		public function attackAndCounter(unit : UnitState, attackerTerrain : TerrainConfig, defenderTerrain : TerrainConfig) : void
		{
			// Attack the unit
			attack(unit, attackerTerrain, defenderTerrain, 0);

			// Counter attack			
			if (unit.armour > 0
				&& unit.canCounterAttack(this))
				unit.attack(this, defenderTerrain, attackerTerrain, -1);
		}		

		/**
		 * Helper function to get a simple path to another position (a path of 1 tile long)
		 */
		public function getSimplePath(otherLocationX : int, otherLocationY : int) : Array
		{
			if (locationX == otherLocationX 
				&& locationY == otherLocationY)
			{
				return null;
			}
			else
			{
				return [
						{ x : locationX, y : locationY },
						{ x : otherLocationX, y : otherLocationY }
					   ];							   
			}
		}		
		
		/**
		 * Dump the state of the game
		 */
		public function dumpState(localFaction : int, level : int) : String
		{
			var rv : String = "";
			
			for (var i : int = 0; i < level; ++i)
				rv += " ";
			
			if (unitConfig.id == UnitConfig.unknownUnit.id)
				rv += "id: " + id + ", "
					+ "configId: unknown, "
					+ "locationX: " + locationX + ", "
					+ "locationY: " + locationY + ", "
					+ "faction: " + faction + "\n";
			else
				rv += "id: " + id + ", "
					+ "configId: " + unitConfig.id + ", "
					+ "locationX: " + locationX + ", "
					+ "locationY: " + locationY + ", "
					+ "faction: " + faction + ", "
					+ "armour: " + armour + ", "
					+ "ammo: " + ammo + ", "
					+ "movementPointsLeft: " + movementPointsLeft + ", "
					+ "actionsLeft: " + actionsLeft + ", "
					+ "lastActionWasMove: " + lastActionWasMove + "\n";
			
			// Sort contained units
			var c : Array = containedUnits.sort(function (u1 : UnitState, u2 : UnitState) : int { return u2.id - u1.id; });
			
	    	// Dump contained units
			for each (var u : UnitState in c)
				rv += u.dumpState(localFaction, level + 1);
			
			return rv;
		}
	}
}

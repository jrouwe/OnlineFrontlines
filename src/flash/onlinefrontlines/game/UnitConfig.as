package onlinefrontlines.game
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * All settings for a unit
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
	public class UnitConfig
	{
		public var id : int;
		public var name : String;
		public var imageNumber : int;
		public var unitClass : int; // of type UnitClass
		public var maxArmour : int;
		private var strengthProperties : Object; // Map<UnitClass, UnitStrengthProperties> maps attack on unit class to strength properties
		public var maxAmmo : int;
		public var visionRange : int;
		public var movementPoints : int;
		public var actions : int;
		private var movementCost : Object; // Map<int, int> maps TerrainConfig.id to movement cost
		public var unitSetupOn : Array = new Array(); // of int
		public var unitSetupNextTo : Array = new Array(); // of int
		public var containerMaxUnits : int;
		public var containerUnitIds : Array = new Array(); // of int
		public var containerArmourPercentagePerTurn : int;
		public var containerAmmoPercentagePerTurn : int;
		public var transformableToUnitId : int;
		public var transformableType : int; // of type TransformableType
		public var victoryPoints : int;
		public var description : String;
		public var isBase : Boolean;
		public var beDetectedRange : int;
			
		// Static access to all units
		public static var allUnits : Array = new Array();
		public static var allUnitsMap : Object = new Object(); // Map<Integer, UnitConfig> maps UnitConfig.id to UnitConfig
		public static var unknownUnit : UnitConfig;

		// Constructor
		public function UnitConfig() : void
		{
			strengthProperties = new Object();
			movementCost = new Object();
		}
		
		// Load all UnitConfig objects from xml
		public static function loadAll(xml : XML) : void
		{
			for each (var unitNode : XML in xml.unit)
			{
				// Load properties
				var u : UnitConfig = new UnitConfig();
				u.id = int(unitNode.id);
				u.name = unitNode.nam;
				u.imageNumber = int(unitNode.img);
				u.unitClass = int(unitNode.cls);
				u.maxArmour = int(unitNode.arm);
				u.maxAmmo = int(unitNode.amm);
				u.visionRange = int(unitNode.vsr);
				u.movementPoints = int(unitNode.mpt);
				u.actions = int(unitNode.nac);
				u.containerMaxUnits = int(unitNode.cmx);
				u.containerArmourPercentagePerTurn = int(unitNode.car);
				u.containerAmmoPercentagePerTurn = int(unitNode.cam);
				u.transformableToUnitId = int(unitNode.tui);
				u.transformableType = int(unitNode.tty);
				u.victoryPoints = int(unitNode.vcp);
				u.description = unitNode.dsc;
				u.isBase = int(unitNode.isb) != 0;
				u.beDetectedRange = int(unitNode.bdr);
				
				// Get set up on
				var son : Array = unitNode.son.split(",");
				for each (var sonIt : String in son)
					if (sonIt.length != 0)
						u.unitSetupOn.push(int(sonIt));

				// Get set up next to
				var snx : Array = unitNode.snx.split(",");
				for each (var snxIt : String in snx)
					if (snxIt.length != 0)
						u.unitSetupNextTo.push(int(snxIt));

				// Load strength properties
				for each (var strengthNode : XML in unitNode.sp)
				{
					var spa : Array = strengthNode.toString().split(",");
					var s : UnitStrengthProperties = new UnitStrengthProperties();						
					var enemyUnitClass : int = int(spa[0]);
					s.maxStrengthWithAmmo = int(spa[1]);
					s.maxStrengthWithoutAmmo = int(spa[2]);
					s.attackRange = int(spa[3]);
					u.strengthProperties[enemyUnitClass] = s;
				}

				// Load movement cost 
				for each (var movementNode : XML in unitNode.mc)
				{
					var mna : Array = movementNode.toString().split(",");
					var terrainId : int = int(mna[0]);
					var movementCost : int = int(mna[1]);
					u.movementCost[terrainId] = movementCost;
				}
				
				// Get containable units
				var cui : Array = unitNode.cui.split(",");
				for each (var cuiIt : String in cui)
					if (cuiIt.length != 0)
						u.containerUnitIds.push(int(cuiIt));

	    		if (u.name.toLowerCase() == "unknown")
	    			unknownUnit = u;
	    			
				allUnitsMap[u.id] = u;
				allUnits.push(u);
			}
		}
		
		// Get strength properties
		public function getStrengthProperties(unitClass : int) : UnitStrengthProperties
		{
			var sp : UnitStrengthProperties = strengthProperties[unitClass];
			if (sp != null)
				return sp;
			else
				return new UnitStrengthProperties();
		}
				
		// Get maximum of strength against any unit class
		public function getMaxStrength(hasAmmo : Boolean) : int
		{
			// Get strength properties
			var lv : int = getStrengthProperties(UnitClass.land).getStrength(hasAmmo);
			var wv : int = getStrengthProperties(UnitClass.water).getStrength(hasAmmo);
			var av : int = getStrengthProperties(UnitClass.air).getStrength(hasAmmo);
			
			// Determine max
			var m : int = 0;
			if (lv > m) m = lv;
			if (wv > m) m = wv;
			if (av > m) m = av;		
			return m;
		}
		
		// Get max amount of tiles this unit can move
		public function getMaxMovement() : int
		{
			var smallest : int = 99999;
			
			for each (var cost : int in movementCost)
				if (cost < smallest)
					smallest = cost;
					
			return movementPoints / smallest;
		}
		
		// Get movement cost for specific terrain
		public function getMovementCost(terrain : TerrainConfig) : int
		{
			if (movementCost[terrain.id] != null)
				return movementCost[terrain.id];
			else
				return movementPoints + 1;
		}
		
		// Check if the unit can move on a specific type of terrain
		public function canMoveOn(terrain : TerrainConfig) : Boolean
		{
			return movementCost[terrain.id] != null;
		}		
	}
}
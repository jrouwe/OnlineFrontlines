package onlinefrontlines.game
{
	/*
	 * Current score in the game
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
	public class Score
	{
		public var numberOfTilesOwned : int;
		public var victoryPointsForTiles : int;
		
		public var numberOfBasesDestroyed : int;
		public var victoryPointsForBases : int;
		
		public var numberOfUnitsDestroyed : int;
		public var victoryPointsForUnits : int;
		
		public var graveyard : Array = new Array; // of UnitState
		
		/** 
		 * Save state
		 */
		public function saveState() : Score
		{
			var s : Score = new Score();

			s.numberOfTilesOwned = numberOfTilesOwned;
			s.victoryPointsForTiles = victoryPointsForTiles;
			s.numberOfBasesDestroyed = numberOfBasesDestroyed;
			s.victoryPointsForBases = victoryPointsForBases;
			s.numberOfUnitsDestroyed = numberOfUnitsDestroyed;
			s.victoryPointsForUnits = victoryPointsForUnits;
			s.graveyard = new Array();
			for each (var u : UnitState in graveyard)
				s.graveyard.push(u);
			
			return s;
		}
		
		/**
		 * Restore state
		 */
		public function restoreState(s : Score) : void
		{
			numberOfTilesOwned = s.numberOfTilesOwned;
			victoryPointsForTiles = s.victoryPointsForTiles;
			numberOfBasesDestroyed = s.numberOfBasesDestroyed;
			victoryPointsForBases = s.victoryPointsForBases;
			numberOfUnitsDestroyed = s.numberOfUnitsDestroyed;
			victoryPointsForUnits = s.victoryPointsForUnits;
			graveyard = s.graveyard;
		}
		
		/**
		 * Get the total score for this player
		 */
		public function getTotalScore() : int
		{
			return victoryPointsForTiles
				+ victoryPointsForBases
				+ victoryPointsForUnits;
		}
		
		/**
		 * Dump state of the game
		 */
		public function dumpState() : String
		{
			return "numberOfTilesOwned: " + numberOfTilesOwned + ", "
					+ "victoryPointsForTiles: " + victoryPointsForTiles + ", "
					+ "numberOfBasesDestroyed: " + numberOfBasesDestroyed + ", "
					+ "victoryPointsForBases: " + victoryPointsForBases + ", "
					+ "numberOfUnitsDestroyed: " + numberOfUnitsDestroyed + ", "
					+ "victoryPointsForUnits: " + victoryPointsForUnits;
		}
	}
}

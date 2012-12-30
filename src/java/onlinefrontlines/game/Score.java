package onlinefrontlines.game;

/**
 * Keeps track of the score for one player
 * 
 * @author jorrit
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
public final class Score 
{
	public int numberOfTilesOwned;
	public int victoryPointsForTiles;
	
	public int numberOfBasesDestroyed;
	public int victoryPointsForBases;
	
	public int numberOfUnitsDestroyed;
	public int victoryPointsForUnits;
	
	/**
	 * Constructor
	 */
	public Score()
	{		
	}
	
	/**
	 * Copy constructor
	 */
	public Score(Score other)
	{
		numberOfTilesOwned = other.numberOfTilesOwned;
		victoryPointsForTiles = other.victoryPointsForTiles;
		numberOfBasesDestroyed = other.numberOfBasesDestroyed;
		victoryPointsForBases = other.victoryPointsForBases;
		numberOfUnitsDestroyed = other.numberOfUnitsDestroyed;
		victoryPointsForUnits = other.victoryPointsForUnits;
	}
	
	/**
	 * Get the total score for this player
	 */
	public int getTotalScore()
	{
		return victoryPointsForTiles
			+ victoryPointsForBases
			+ victoryPointsForUnits;
	}
	
	/**
	 * Dump state of the game
	 */
	public void dumpState(StringBuilder b)
	{
		b.append("numberOfTilesOwned: ");
		b.append(numberOfTilesOwned);
		b.append(", victoryPointsForTiles: ");
		b.append(victoryPointsForTiles);
		b.append(", numberOfBasesDestroyed: ");
		b.append(numberOfBasesDestroyed);
		b.append(", victoryPointsForBases: ");
		b.append(victoryPointsForBases);
		b.append(", numberOfUnitsDestroyed: ");
		b.append(numberOfUnitsDestroyed);
		b.append(", victoryPointsForUnits: ");
		b.append(victoryPointsForUnits);
	}
};

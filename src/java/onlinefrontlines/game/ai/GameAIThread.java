package onlinefrontlines.game.ai;

import java.awt.Point;
import java.util.*;
import org.apache.log4j.Logger;
import onlinefrontlines.Constants;
import onlinefrontlines.game.*;
import onlinefrontlines.game.actions.*;
import onlinefrontlines.utils.*;

/**
 * This thread wakes up every X seconds to check if there are games that need a move by the AI
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
public class GameAIThread extends Thread 
{
	private static final Logger log = Logger.getLogger(GameAIThread.class);
	
	/**
	 * Constructor
	 */
	public GameAIThread()
	{
		super("GameAIThread");
	}
	
	/**
	 * Run the thread
	 */
	public void run()
	{
		log.info("Thread started");
		
		for (;;)
		{
			try
			{
				// Loop through all games that currently need interaction by AI
				List<Integer> games = GameStateDAO.getGamesIdsToContinue(Constants.USER_ID_AI);
				for (Integer g : games)
				{
					try
					{
				    	// Get the game
				    	GameState gameState = GameStateCache.getInstance().get(g);
				    	if (gameState == null)
				    		throw new Exception("No game found");
				    	
				    	// Prevent concurrent access
				    	synchronized (gameState)
				    	{
				    		// Process the game
				    		processGame(gameState);
				    	}					
					}
					catch (Exception e)
					{			
						// Log error, next game
						Tools.logException(e);
					}
				}
			}
			catch (Exception e)
			{			
				// Log error, retry
				Tools.logException(e);
			}

			try
			{
				// Wait for next cycle
				synchronized (this)
				{
					wait(5 * 1000);
				}
			}
			catch (InterruptedException e)
			{
				// Normal termination
				break;
			}
		}

		log.info("Thread stopped");
	}
	
	/**
	 * Process a single game
	 */
	private void processGame(GameState gameState) throws Exception
	{
		// Determine AI faction
		Faction aiFaction = gameState.getPlayerId(Faction.f1) == Constants.USER_ID_AI? Faction.f1 : Faction.f2;
		
		Map<UnitClass, AttackGrid> grids = new TreeMap<UnitClass, AttackGrid>();
		
		// Process all units
		ArrayList<UnitState> units = new ArrayList<UnitState>(gameState.getUnits());
		while (!units.isEmpty() && gameState.winningFaction == Faction.invalid)
		{
			UnitState u = units.get(0);
			
			// We only need to process our own units
			if (u.faction != aiFaction)
			{
				units.remove(u);				
				continue;
			}
			
			// Make unit move
			if (!processUnit(gameState, u, grids))
			{
				units.remove(u);
				continue;
			}
		}
		
		// End the turn
		if (gameState.winningFaction == Faction.invalid)
			gameState.execute(new ActionEndTurn(), true);
	}
			
	private static class AttackGrid
	{
		public HexagonGridImpl<Double> directAttack;
		public HexagonGridImpl<Double> indirectAttack;
		
		public double getWeighted(Point p, double directAttackWeight)
		{
			return directAttackWeight * directAttack.get(p) + (1.0 - directAttackWeight) * indirectAttack.get(p);
		}
	}
	
	private AttackGrid getEnemyAttackPower(GameState gameState, UnitClass unitClass, Faction unitFaction) throws Exception
	{
		//String debugId = "class: " + unitClass.toString() + " faction: " + unitFaction.toString();
		
		final MapConfig mapConfig = gameState.mapConfig;

		// Create grid
		AttackGrid grid = new AttackGrid();
		grid.directAttack = new HexagonGridImpl<Double>(mapConfig.sizeX, mapConfig.sizeY, 0.0);
		grid.indirectAttack = new HexagonGridImpl<Double>(mapConfig.sizeX, mapConfig.sizeY, 0.0);		

		// Loop through all units that are able to attack
		for (final UnitState enemyUnit : gameState.getUnits())
			if (enemyUnit.canAttackUnitClass(unitClass, unitFaction))
			{
				/*
				String enemyDebugId = debugId + " enemy: " + enemyUnit.id + " (" + enemyUnit.unitConfig.name + ")";

				// Identify units
				ArrayList<ActionAnnotateTiles.Tile> tiles = new ArrayList<ActionAnnotateTiles.Tile>();
				tiles.add(new ActionAnnotateTiles.Tile(enemyUnit.locationX, enemyUnit.locationY, Integer.toString(enemyUnit.id)));
				gameState.execute(new ActionAnnotateTiles("EnemyUnit " + enemyDebugId, tiles), false);
				*/
				
				// Get range of enemy unit
				int range = enemyUnit.unitConfig.getStrengthProperties(unitClass).attackRange;
				
				// Calculate direct attack
				List<Point> directAttack = grid.directAttack.extendBy(enemyUnit.getLocation(), range);
				TerrainConfig attackerTerrain = mapConfig.getTerrainAt(enemyUnit.locationX, enemyUnit.locationY);
				for (Point p : directAttack)
				{
					// Get defender terrain
					TerrainConfig defenderTerrain = mapConfig.getTerrainAt(p.x, p.y);
					
					// Add average loss
					double avgLoss = enemyUnit.getAverageArmourLossForAttack(unitClass, attackerTerrain, defenderTerrain);			
					grid.directAttack.set(p, grid.directAttack.get(p) + enemyUnit.unitConfig.actions * avgLoss);
				}
				
				/*
				// Debug output
				gameState.execute(new ActionAnnotateTiles("EnemyDirectAttack " + enemyDebugId, ActionAnnotateTiles.createTiles(directAttack, "D")), false);
				gameState.execute(new ActionAnnotateTiles("EnemyDirectAttackGrid " + enemyDebugId, ActionAnnotateTiles.createTiles(grid.directAttack)), false);
				*/

				// Calculate movement for unit
				List<Point> movementRange = grid.indirectAttack.getReachableArea(enemyUnit.getLocation(), 
					new HexagonGrid.CostFunction() 
					{
						public float getCost(HexagonGrid.AStarNode from, Point to)
						{
							TerrainConfig terrain = mapConfig.getTerrainAt(to.x, to.y);
							return enemyUnit.unitConfig.getMovementCost(terrain);
						}
					}, 
					enemyUnit.unitConfig.movementPoints);

				// If we can move
				if (movementRange.size() > 1)
				{
					// Calculate indirect attack
					List<Point> indirectAttack = grid.indirectAttack.extendBy(movementRange, range);	
					for (Point p : indirectAttack)
					{
						// Get defender terrain
						TerrainConfig defenderTerrain = mapConfig.getTerrainAt(p.x, p.y);
	
						// Find best attacker terrain
						attackerTerrain = null;
						for (Point m : movementRange)
						{
							int d = HexagonGrid.getDistance(p, m);
							if (d > 0 && d <= range)
							{
								TerrainConfig t = mapConfig.getTerrainAt(m.x, m.y);
								if (attackerTerrain == null || t.strengthModifier > attackerTerrain.strengthModifier)
									attackerTerrain = t;					
							}
						}
	
						// Add average loss
						double avgLoss = enemyUnit.getAverageArmourLossForAttack(unitClass, attackerTerrain, defenderTerrain);			
						grid.indirectAttack.set(p, grid.indirectAttack.get(p) + (enemyUnit.unitConfig.actions - 1) * avgLoss);
					}
				}

				/*
				// Debug output
				gameState.execute(new ActionAnnotateTiles("EnemyMovement " + enemyDebugId, ActionAnnotateTiles.createTiles(movementRange, "M")), false);
				gameState.execute(new ActionAnnotateTiles("EnemyIndirectAttack " + enemyDebugId, ActionAnnotateTiles.createTiles(indirectAttack, "I")), false);
				gameState.execute(new ActionAnnotateTiles("EnemyIndirectAttackGrid " + enemyDebugId, ActionAnnotateTiles.createTiles(grid.indirectAttack)), false);
				*/
			}

		/*
		// Debug output
		gameState.execute(new ActionAnnotateTiles("EnemyDirectAttackGrid " + debugId, ActionAnnotateTiles.createTiles(grid.directAttack)), false);
		gameState.execute(new ActionAnnotateTiles("EnemyIndirectAttackGrid " + debugId, ActionAnnotateTiles.createTiles(grid.indirectAttack)), false);
		*/
		
		return grid;
	}
	
	private boolean checkAttack(GameState gameState, UnitState unitState, Map<UnitClass, AttackGrid> grids) throws Exception
	{
		// Check if we have an action left
		if (unitState.actionsLeft < 1)
			return false;
		
		/*
		// Debug output
		ArrayList<ActionAnnotateTiles.Tile> hisTiles = new ArrayList<ActionAnnotateTiles.Tile>();
		ArrayList<ActionAnnotateTiles.Tile> myTiles = new ArrayList<ActionAnnotateTiles.Tile>();
		ArrayList<ActionAnnotateTiles.Tile> scoreTiles = new ArrayList<ActionAnnotateTiles.Tile>();
		*/

		// Check if unit can attack
		double bestScore = 0;
		UnitState bestUnit = null;
		for (UnitState enemyUnit : gameState.getUnits())
			if (unitState.canAttack(enemyUnit))
			{
				// Get terrain
				TerrainConfig attackerTerrain = gameState.getAttackTerrain(unitState);
				TerrainConfig defenderTerrain = gameState.getAttackTerrain(enemyUnit);					
				
				// Calculate on average how much armour enemy would lose
				double hisAvgArmourLoss = unitState.getAverageArmourLossForAttack(enemyUnit.unitConfig.unitClass, attackerTerrain, defenderTerrain);
				double hisArmour = Math.max(enemyUnit.armour - hisAvgArmourLoss, 0);
				
				// Estimate on average how much armour I would lose in the counter attack 
				double myAvgArmourLoss = enemyUnit.canCounterAttack(unitState)? enemyUnit.getAverageArmourLossForAttack(unitState.unitConfig.unitClass, defenderTerrain, attackerTerrain) : 0;
				myAvgArmourLoss *=  hisArmour / enemyUnit.armour;
				
				// Scale to victory points (also account for units contained in the unit)
				double hisLoss = enemyUnit.getTotalVictoryPoints() * Math.min(hisAvgArmourLoss, enemyUnit.armour) / enemyUnit.armour;
				double myLoss = unitState.getTotalVictoryPoints() * Math.min(myAvgArmourLoss, unitState.armour) / unitState.armour;
				
				// Calculate score
				double score = hisLoss - myLoss;

				/*
				// Debug output
				hisTiles.add(new ActionAnnotateTiles.Tile(enemyUnit.getLocation(), hisLoss));
				myTiles.add(new ActionAnnotateTiles.Tile(enemyUnit.getLocation(), myLoss));
				scoreTiles.add(new ActionAnnotateTiles.Tile(enemyUnit.getLocation(), score));
				*/
				
				// Check if score is better
				if (bestUnit == null || score > bestScore)
				{
					bestScore = score;
					bestUnit = enemyUnit;
				}
			}
		if (bestUnit == null)
			return false;

		/*
		// Debug output
		gameState.execute(new ActionAnnotateTiles("CheckAttack hisLoss: " + unitState.id, hisTiles), false);
		gameState.execute(new ActionAnnotateTiles("CheckAttack myLoss: " + unitState.id, myTiles), false);
		gameState.execute(new ActionAnnotateTiles("CheckAttack score: " + unitState.id, scoreTiles), false);
		*/

		// Perform attack
		gameState.execute(new ActionAttackUnit(unitState, bestUnit), true);
		
		// Grid is now invalid
		grids.remove(bestUnit.unitConfig.unitClass);
		
		return true;
	}
	
	private boolean followPath(GameState gameState, UnitState unitState, ArrayList<Point> path) throws Exception
	{
		final MapConfig mapConfig = gameState.mapConfig;

		int max = 1;
		
		// Get part of the path that we have movement points for
		int movementLeft = unitState.movementPointsLeft;
		for (int i = 1; i < path.size(); ++i)
		{
			Point p = path.get(i);
			TerrainConfig terrain = mapConfig.getTerrainAt(p.x, p.y);
			movementLeft -= unitState.unitConfig.getMovementCost(terrain);
			if (movementLeft >= 0)
				max++;
			else
				break;
		}
		
		// Get part of the path that won't result in us getting in collision
		for (int i = max - 1; i > 0; --i)
		{
			Point p = path.get(i);
			if (gameState.getUnit(p.x, p.y) != null)
				--max;
			else
				break;
		}

		// Remove extra elements
		while (path.size() > max)
			path.remove(path.size() - 1);
		if (path.size() < 2)
			return false;
		
		// Make unit move
		gameState.execute(new ActionMoveUnit(unitState, path), true);
		return true;
	}
	
	private boolean checkMoveThenAttack(GameState gameState, final UnitState unitState, AttackGrid grid) throws Exception
	{
		// Check if still able to move and attack
		if (unitState.actionsLeft < 2)
			return false;

		// Check if unit can move at all
		final int minCost = unitState.unitConfig.getMinimumMovementCost();
		if (minCost > unitState.movementPointsLeft)
			return false;
		
		final MapConfig mapConfig = gameState.mapConfig;

		/*
		// Debug output
		ArrayList<ActionAnnotateTiles.Tile> hisTiles = new ArrayList<ActionAnnotateTiles.Tile>();
		ArrayList<ActionAnnotateTiles.Tile> myTiles = new ArrayList<ActionAnnotateTiles.Tile>();
		ArrayList<ActionAnnotateTiles.Tile> scoreTiles = new ArrayList<ActionAnnotateTiles.Tile>();
		*/

		// Calculate movement for unit
		List<Point> movementRange = mapConfig.getReachableArea(unitState.getLocation(), 
			new HexagonGrid.CostFunction() 
			{
				public float getCost(HexagonGrid.AStarNode from, Point to)
				{
					TerrainConfig terrain = mapConfig.getTerrainAt(to.x, to.y);
					return unitState.unitConfig.getMovementCost(terrain);
				}
			}, 
			unitState.movementPointsLeft);

		// Loop through all enemy units that this unit can attack
		Point bestPoint = null;
		double bestScore = 0;
		for (UnitState enemyUnit : gameState.getUnits())
			if (unitState.canAttackFrom(0, enemyUnit))
			{
				// Get range of unit
				int range = unitState.unitConfig.getStrengthProperties(enemyUnit.unitConfig.unitClass).attackRange;

				// Evaluate from all locations
				Point bestUnitPoint = null;
				//double bestUnitHisLoss = 0;
				//double bestUnitMyLoss = 0;
				double bestUnitScore = 0;
				for (Point p : movementRange)
					if (gameState.getUnit(p.x, p.y) == null
						&& enemyUnit.getDistanceTo(p.x, p.y) <= range)
					{
						TerrainConfig attackerTerrain = gameState.getAttackTerrain(unitState, p.x, p.y);
						TerrainConfig defenderTerrain = gameState.getAttackTerrain(enemyUnit);					
	
						// Calculate on average how much armour enemy would lose
						double hisAvgArmourLoss = unitState.getAverageArmourLossForAttack(enemyUnit.unitConfig.unitClass, attackerTerrain, defenderTerrain);
	
						// Scale to victory points (also account for units contained in the unit)
						double hisLoss = enemyUnit.getTotalVictoryPoints() * Math.min(hisAvgArmourLoss, enemyUnit.armour) / enemyUnit.armour;
						double myLoss = unitState.getTotalVictoryPoints() * Math.min(grid.getWeighted(p, 0.66) / unitState.armour, 2.0);
						
						// Calculate score
						double score = hisLoss - myLoss;
						
						// Check if score is better
						if (bestUnitPoint == null || score > bestUnitScore)
						{
							bestUnitScore = score;
							//bestUnitHisLoss = hisLoss;
							//bestUnitMyLoss = myLoss;
							bestUnitPoint = p;
						}
					}
	
				if (bestUnitPoint != null)
				{
					/*
					// Debug output
					hisTiles.add(new ActionAnnotateTiles.Tile(enemyUnit.getLocation(), bestUnitHisLoss));
					myTiles.add(new ActionAnnotateTiles.Tile(enemyUnit.getLocation(), bestUnitMyLoss));
					scoreTiles.add(new ActionAnnotateTiles.Tile(enemyUnit.getLocation(), bestUnitScore));
					*/
	
					// Check if score is better
					if (bestPoint == null || bestUnitScore > bestScore)
					{
						bestScore = bestUnitScore;
						bestPoint = bestUnitPoint;
					}
				}			
			}
		if (bestPoint == null)
			return false;
		
		/*
		// Debug output
		gameState.execute(new ActionAnnotateTiles("MoveThenAttack hisLoss: " + unitState.id, hisTiles), false);
		gameState.execute(new ActionAnnotateTiles("MoveThenAttack myLoss: " + unitState.id, myTiles), false);
		gameState.execute(new ActionAnnotateTiles("MoveThenAttack score: " + unitState.id, scoreTiles), false);
		*/

		// Plan shortest path
		HexagonGrid.PlanResult result = mapConfig.planPath(unitState.getLocation(), bestPoint, 
			new HexagonGrid.CostFunction() 
			{
				public float getCost(HexagonGrid.AStarNode from, Point to)
				{
					TerrainConfig terrain = mapConfig.getTerrainAt(to.x, to.y);
					return unitState.unitConfig.getMovementCost(terrain);
				}
			}, 
			new HexagonGrid.CostEstimator() 
			{						
				public float getEstimatedCost(Point from, Point goal)
				{
					return MapConfig.getDistance(from, goal) * minCost;
				}				
			});
		if (result == null)
			return false;

		// Move unit into place
		return followPath(gameState, unitState, result.path);
	}
	
	private boolean checkMoveTowardsEnemy(final GameState gameState, final UnitState unitState, AttackGrid grid) throws Exception
	{
		// Only do this when we have all our actions left
		if (unitState.actionsLeft != unitState.unitConfig.actions)
			return false;

		// Check if unit can move
		final int minCost = unitState.unitConfig.getMinimumMovementCost();		
		if (minCost > unitState.movementPointsLeft)
			return false;

		final MapConfig mapConfig = gameState.mapConfig;
		
		// Loop through all enemy units that this unit can attack
		HexagonGrid.PlanResult best = null;
		for (UnitState enemyUnit : gameState.getUnits())
			if (unitState.canAttackFrom(0, enemyUnit))
			{					
				// Plan shortest path
				HexagonGrid.PlanResult result = mapConfig.planPath(unitState.getLocation(), enemyUnit.getLocation(), 
					new HexagonGrid.CostFunction() 
					{
						public float getCost(HexagonGrid.AStarNode from, Point to)
						{
							TerrainConfig terrain = mapConfig.getTerrainAt(to.x, to.y);
							float cost = unitState.unitConfig.getMovementCost(terrain);
							
							// If we've just expended all our movement points end ended up in another unit, add a bit to the cost
							if (from.costFromStart <= unitState.movementPointsLeft
								&& from.costFromStart + cost > unitState.movementPointsLeft)
							{
								UnitState unit = gameState.getUnit(from.point.x, from.point.y);
								if (unit != null)
									return cost + 2 * minCost;
							}
							
							return cost;							
						}
					}, 
					new HexagonGrid.CostEstimator() 
					{						
						public float getEstimatedCost(Point from, Point goal)
						{
							return MapConfig.getDistance(from, goal) * minCost;
						}				
					});
				
				// Store best so far
				if (result != null && (best == null || best.cost > result.cost))
					best = result;				
			}		
		if (best == null)
			return false;

		// Check if we can be attacked
		boolean isUnderAttack = grid.directAttack.get(unitState.getLocation()) > 0;
		if (!isUnderAttack)
		{
			// Get top speed of nearby units
			int otherMax = unitState.unitConfig.getMaxMovement();
			for (UnitState friendlyUnit : gameState.getUnits())
				if (friendlyUnit != unitState
					&& friendlyUnit.faction == unitState.faction
					&& friendlyUnit.container == null
					&& friendlyUnit.getDistanceTo(unitState.locationX, unitState.locationY) < 4
					&& friendlyUnit.unitConfig.getMaxMovement() > 0
					&& grid.directAttack.get(friendlyUnit.getLocation()) == 0)
					otherMax = Math.min(otherMax, friendlyUnit.unitConfig.getMaxMovement());

			// Limit our speed
			while (best.path.size() > otherMax + 1)
				best.path.remove(best.path.size() - 1);			
		}
		
		return followPath(gameState, unitState, best.path);
	}

	private boolean processUnit(final GameState gameState, final UnitState unitState, Map<UnitClass, AttackGrid> grids) throws Exception
	{		
		// We only process units that have actions left
		if (unitState.actionsLeft <= 0)
			return false;
		
		// Cannot do anything with dead units
		if (unitState.armour <= 0)
			return false;
		
		// Don't do anything with contained units (yet)
		if (unitState.container != null)
			return false;
		
		// Check if unit can attack
		if (checkAttack(gameState, unitState, grids))
			return true;
		
		// Get enemy influence map
		UnitClass unitClass = unitState.unitConfig.unitClass;
		AttackGrid grid = grids.get(unitClass);
		if (grid == null)
		{
			grid = getEnemyAttackPower(gameState, unitClass, unitState.faction);
			grids.put(unitClass, grid);
		}
		
		// Check if we can move to attack
		if (checkMoveThenAttack(gameState, unitState, grid))
			return true;
		
		// Move in general direction of enemy
		if (checkMoveTowardsEnemy(gameState, unitState, grid))
			return true;
		
		return false;
	}
}

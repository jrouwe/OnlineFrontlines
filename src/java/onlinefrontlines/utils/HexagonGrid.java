package onlinefrontlines.utils;

import java.util.*;
import java.awt.Point;

/**
 * Tile layout for a game
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
public class HexagonGrid
{
	/**
	 * Number of tiles in the horizontal direction
	 */
	public final int sizeX;
	
	/**
	 * Number of tiles in the vertical direction
	 */
	public final int sizeY;
	
	/**
	 * Neighbour table  
	 */
	private static Point neighbourTable[][] = {
								 { // Odd rows
								  new Point(0, -1),
								  new Point(1, -1),
								  new Point(1, 0),
								  new Point(1, 1),
								  new Point(0, 1),
								  new Point(-1, 0)
								 },
								 { // Even rows
								  new Point(-1, -1),
								  new Point(0, -1),
								  new Point(1, 0),
								  new Point(0, 1),
								  new Point(-1, 1),
								  new Point(-1, 0)
								 }
								};

	/**
	 * Constructor
	 * 
	 * @param sizeX Horizontal amount of tiles
	 * @param sizeY Vertical amount of tiles
	 */
	public HexagonGrid(int sizeX, int sizeY)
	{
		this.sizeX = sizeX;
		this.sizeY = sizeY;		
	}
	
	/**
	 * Copy constructor
	 */
	public HexagonGrid(HexagonGrid other)
	{
		sizeX = other.sizeX;
		sizeY = other.sizeY;
	}
	
	/**
	 * Get shortest distance from one tile to the next
	 */
	public static int getDistance(int x1, int y1, int x2, int y2)
	{
		// Trivial calculation if on same row
		if (y1 == y2)
			return Math.abs(x2 - x1);
			
		// Otherwise start looking for neighbours
		int bestX = x1;
		int bestY = y1;
			
		Point n[] = neighbourTable[y1 & 1];

		for (int i = 0; i < n.length; ++i)
		{
			// Try out this neighbour
			int xr = x1 + n[i].x;
			int yr = y1 + n[i].y;
			
			// Check if it is the closest result so far
			if (Math.abs(x2 - xr) <= Math.abs(x2 - bestX) // Find the best match in X direction
				&& Math.abs(y2 - yr) < Math.abs(y2 - y1)) // Must be in the row closer to our destination
			{
				// Store it
				bestX = xr;
				bestY = yr;
			}
		}
			
		// Recurse to best candidate
		return getDistance(bestX, bestY, x2, y2) + 1;
	}
	
	/**
	 * Get shortest distance from one tile to the next
	 */
	public static int getDistance(Point from, Point to)
	{
		return getDistance(from.x, from.y, to.x, to.y);
	}
	
	/**
	 * Check if tile is in range
	 */
	public boolean isTileInRange(int x, int y)
	{
		return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
	}
	
	/**
	 * Check if tile is in playable area
	 */
	public boolean isTileInPlayableArea(int x, int y)
	{
		if (y <= 0 || y >= sizeY - 1) 
			return false;
			
		if ((y & 1) == 0)
			return x >= 0 && x < sizeX - 1;
		else
			return x >= 1 && x < sizeX;
	}
	
	/**
	 * Check if tile is in playable area
	 */
	public boolean isTileInPlayableArea(Point p)
	{
		return isTileInPlayableArea(p.x, p.y);
	}
	
	/**
	 * Get neighbouring tiles
	 */
	public ArrayList<Point> getNeighbours(int x, int y)
	{
		ArrayList<Point> r = new ArrayList<Point>();
		
		Point n[] = neighbourTable[y & 1];
		
		for (int i = 0; i < n.length; ++i)
		{
			int xr = x + n[i].x;
			int yr = y + n[i].y;
			
			if (isTileInRange(xr, yr))
				r.add(new Point(xr, yr));
		}		
		
		return r;
	}
	
	/**
	 * Get neighbouring tiles
	 */
	public ArrayList<Point> getNeighbours(Point p)
	{
		return getNeighbours(p.x, p.y);
	}
	
	/**
	 * Helper class representing a node in the A* algorithm
	 */
	public class AStarNode implements Comparable<AStarNode> 
	{
		public Point point;
		public float costFromStart;
		public float estimatedCostToGoal;
		public AStarNode cameFrom;		
		
		public AStarNode(Point point, float costFromStart, float estimatedCostToGoal, AStarNode pathParent)
		{
			this.point = point;
			this.costFromStart = costFromStart;
			this.estimatedCostToGoal = estimatedCostToGoal;
			this.cameFrom = pathParent;
		}

		public float getCost() 
		{
			return costFromStart + estimatedCostToGoal;
		}

		@Override
		public int compareTo(AStarNode other) 
		{
			float diff = getCost() - other.getCost();
			return diff > 0? 1 : (diff < 0? -1 : 0);
		}
	}  
	
	/**
	 * Get cost going from 'from' to 'to'
	 */
	public interface CostFunction
	{
		public float getCost(AStarNode from, Point to);
	}
	
	/**
	 * Get (under)estimated cost to goal  
	 */
	public interface CostEstimator
	{		
		public float getEstimatedCost(Point from, Point goal);
	}	
	
	/**
	 * The result of the plan action
	 */
	public static class PlanResult
	{
		public ArrayList<Point> path;
		public float cost;
		
		public PlanResult(ArrayList<Point> path, float cost)
		{
			this.path = path;
			this.cost = cost;
		}
	}

	/**
	 * Find path using cost function
	 */
	public PlanResult planPath(Point from, Point to, CostFunction costFunction, CostEstimator costEstimator) 
	{
		Queue<AStarNode> openSet = new PriorityQueue<AStarNode>();
		Set<Point> closedSet = new HashSet<Point>();
		Map<Point, AStarNode> pointToNode = new HashMap<Point, AStarNode>();

		// Add first node
		AStarNode startNode = new AStarNode(from, 0, costEstimator.getEstimatedCost(from, to), null);
		openSet.add(startNode);
		pointToNode.put(startNode.point, startNode);

		while (!openSet.isEmpty()) 
		{
			// Get best from open set
			AStarNode node = openSet.poll();

			// Add to closed list
			closedSet.add(node.point);
			
			// Check if path found
			if (node.point.equals(to))
			{
				// Remember cost
				float cost = node.costFromStart;

				// Construct the path from start to goal
				ArrayList<Point> path = new ArrayList<Point>();
				while (node != null) 
				{
					path.add(node.point);
					node = node.cameFrom;
				}
				Collections.reverse(path);
				
				// Return path
				return new PlanResult(path, cost);
			}

			for (Point n : getNeighbours(node.point))
			{
				// Don't consider points outside playable area
				if (!isTileInPlayableArea(n))
					continue;
				
				// Don't consider if already in closed list
				if (closedSet.contains(n))
					continue;
				
				// Calculate cost using this path
				float tentativeCost = node.costFromStart + costFunction.getCost(node, n);
				
				// Check if we already have a node
				AStarNode current = pointToNode.get(n);
				if (current == null)
				{
					// Create new node
					AStarNode newNode = new AStarNode(n, tentativeCost, costEstimator.getEstimatedCost(n, to), node);
					openSet.add(newNode);
					pointToNode.put(newNode.point, newNode);
				}
				else if (tentativeCost < current.costFromStart)
				{
					// Update new cost
					current.cameFrom = node;
					current.costFromStart = tentativeCost;
				}
			}
		}

		// No path found
		return null;
	}
	
	/**
	 * Take a list of points and get the set of points less than or equal to distance away
	 */
	public List<Point> extendBy(List<Point> points, int distance)
	{
		HashSet<Point> set = new HashSet<Point>(points);
		
		for (int i = 0; i < distance; ++i)
			for (Point p : new ArrayList<Point>(set))
				for (Point n : getNeighbours(p))
					if (isTileInPlayableArea(n))
						set.add(n);
		
		return new ArrayList<Point>(set);
	}
	
	/**
	 * Take a single point and get the set of points less than or equal to distance away
	 */
	public List<Point> extendBy(Point point, int distance)
	{
		ArrayList<Point> list = new ArrayList<Point>();
		list.add(point);
		return extendBy(list, distance);
	}
	
	/**
	 * Find path using cost function
	 */
	public ArrayList<Point> getReachableArea(Point from, CostFunction costFunction, float maxCost) 
	{
		Queue<AStarNode> openSet = new PriorityQueue<AStarNode>();
		Set<Point> closedSet = new HashSet<Point>();
		Map<Point, AStarNode> pointToNode = new HashMap<Point, AStarNode>();

		// Add first node
		AStarNode startNode = new AStarNode(from, 0, 0, null);
		openSet.add(startNode);
		pointToNode.put(startNode.point, startNode);

		while (!openSet.isEmpty()) 
		{
			// Get best from open set
			AStarNode node = openSet.poll();

			// Add to closed list
			closedSet.add(node.point);
			
			for (Point n : getNeighbours(node.point))
			{
				// Don't consider points outside playable area
				if (!isTileInPlayableArea(n))
					continue;
				
				// Don't consider if already in closed list
				if (closedSet.contains(n))
					continue;
				
				// Calculate cost using this path
				float tentativeCost = node.costFromStart + costFunction.getCost(node, n);
				if (tentativeCost > maxCost)
					continue;
				
				// Check if we already have a node
				AStarNode current = pointToNode.get(n);
				if (current == null)
				{
					// Create new node
					AStarNode newNode = new AStarNode(n, tentativeCost, 0, node);
					openSet.add(newNode);
					pointToNode.put(newNode.point, newNode);
				}
				else if (tentativeCost < current.costFromStart)
				{
					// Update new cost
					current.cameFrom = node;
					current.costFromStart = tentativeCost;
				}
			}
		}

		// Return list of visited nodes
		return new ArrayList<Point>(closedSet);
	}
}

package onlinefrontlines.game
{
	import flash.utils.*;
	import flash.media.SoundChannel;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;

	/*
	 * Helper class for movement animations
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
	public class UnitMovementHelper
	{
		// Base sounds
		[Embed(source="../../assets/sounds/base_enter.mp3")]
		private static var baseEnterClass : Class;
		[Embed(source="../../assets/sounds/base_leave.mp3")]
		private static var baseLeaveClass : Class;

		private var mapConfig : MapConfig;
		private var unit : UnitState;
		private var fromContainer : UnitState;
		private var path : Array;

		private var startTime : int;		
		public var hasFinished : Boolean = false;
		
		private var speed : Number = 3;		

		private var moveSoundClass : Class;
		private var moveSound : SoundChannel;

		// Constructor
		public function UnitMovementHelper(unit : UnitState, fromContainer : UnitState, path : Array, mapConfig : MapConfig) : void
		{
			this.unit = unit;
			this.fromContainer = fromContainer;
			this.path = path;
			this.mapConfig = mapConfig;
		}
		
		// Start
		public function start() : void
		{
			if (path == null)
			{
				// No path means we're finished
				hasFinished = true;
			}
			else
			{
				// Mark start time
				startTime = getTimer();			
	
				// Start moving graphic
				unit.graphics.doMoveStart();

				// Play leave base sound
				if (fromContainer != null)
					SoundSystem.play(baseLeaveClass);

				// Create movement sound	
				moveSoundClass = getMoveSoundClass();			
				moveSound = SoundSystem.play(moveSoundClass, true);
			}
		}		
		
		// Update call
		public function update() : void
		{
			// Check if already done
			if (hasFinished)
				return;
				
			// Update movement sound	
			var newMoveSoundClass : Class = getMoveSoundClass();
			if (moveSoundClass != newMoveSoundClass)
			{
				moveSoundClass = newMoveSoundClass;
				SoundSystem.stop(moveSound);			
				moveSound = SoundSystem.play(moveSoundClass, true);
			}

			// Calculate position along path
			var curPos : Number = speed * (getTimer() - startTime) / 1000.0;			
			if (curPos >= path.length - 1)
			{
				// Stop
				stop();
			}
			else
			{
				// Move graphic to current position on path
				var intCurPos : int = curPos;
				var p1 : Object = mapConfig.getTileLocation(path[intCurPos].x, path[intCurPos].y);			
				var p2 : Object = mapConfig.getTileLocation(path[intCurPos + 1].x, path[intCurPos + 1].y);			
				
				// Determine facing direction
				if (p2.x > p1.x)
					unit.graphics.setFacingLeft(false);
				else if (p2.x < p1.x)
					unit.graphics.setFacingLeft(true);					
				
				// Move unit
				var fraction : Number = curPos - intCurPos;
				unit.graphics.moveTo(p1.x * (1 - fraction) + p2.x * fraction, p1.y * (1 - fraction) + p2.y * fraction);				
			}			
		}
		
		// Stop animation and move unit to end position
		public function stop() : void
		{
			// Check if already done
			if (hasFinished)
				return;

			// Stop movement sound
			SoundSystem.stop(moveSound);
			
			// Determine final facing direction
			var p1 : Object = mapConfig.getTileLocation(path[path.length - 2].x, path[path.length - 2].y);			
			var p2 : Object = mapConfig.getTileLocation(path[path.length - 1].x, path[path.length - 1].y);			
			if (p2.x > p1.x)
				unit.graphics.setFacingLeft(false);
			else if (p2.x < p1.x)
				unit.graphics.setFacingLeft(true);
									
			// Move unit
			unit.graphics.doMoveStop();
			
			// Play enter base sound
			if (unit.container != null)
				SoundSystem.play(baseEnterClass);
			
			// Finished now;
			hasFinished = true;
		}	

		// Movement sound
		private function getMoveSoundClass() : Class
		{
			return unit.graphics.getStateUnknown()? UnitGraphics.moveUnknownClass : UnitGraphics.getUnitProperties(unit).movementSound;
		}
	}
}
package onlinefrontlines.utils
{
	import flash.display.Bitmap;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	import mx.core.Application;

	/*
	 * Animating bitmap
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
	public class AnimatedBitmap extends Bitmap
	{
		// Loop modes
		public static var LOOP_OFF : int = 0;
		public static var LOOP_ON : int = 1;
		public static var LOOP_PING_PONG : int = 2;
	
		// Data
		private var animatedBitmapData : AnimatedBitmapData;
		
		// Animation state
		private var curFrame : int = -1;
		private var playing : Boolean = false;
		private var playDirection : int;
		private var looping : int;
		private var autoDestroy : Boolean;
		private var timer : Timer;
	
		// Constructor
		public function AnimatedBitmap(animatedBitmapData : AnimatedBitmapData)
		{
			this.animatedBitmapData = animatedBitmapData;
			
			setFrame(0);
		}
		
		// Remove and destroy
		public function destroy() : void
		{
			autoDestroy = false;

			stop();
			
			if (parent != null)
				parent.removeChild(this);
		}
		
		// Set current frame
		public function setFrame(frame : int) : void
		{
			if (curFrame == frame)
				return;
			curFrame = frame;
			
			bitmapData = animatedBitmapData.getFrame(curFrame);
		}
		
		// Get current frame
		public function getFrame() : int
		{
			return curFrame;
		}
		
		// Get total number of frames in the animation
		public function getNumFrames() : int
		{
			return animatedBitmapData.getNumFrames();
		}		
		
		// Play from current frame with specified delay
		public function play(frameDelay : int, looping : int, autoDestroy : Boolean) : void
		{
			this.autoDestroy = false;
		
			stop();
			
			this.looping = looping;
			this.autoDestroy = autoDestroy;
			
			timer = new Timer(frameDelay, 0);
			timer.addEventListener(TimerEvent.TIMER, onTimerTick);
			timer.start();

			playing = true;
			playDirection = 1;
		}
		
		// Stop playback
		public function stop() : void
		{
			if (playing)
			{
				playing = false;
				
				timer.removeEventListener(TimerEvent.TIMER, onTimerTick);
				timer = null;

				if (autoDestroy)
					destroy();
			}
		}
		
		// Check if animation is still playing
		public function isPlaying() : Boolean
		{
			return playing;
		}
		
		// Timer handler
		private function onTimerTick(event : Event) : void
		{
			var frame : int = curFrame + playDirection;
			if (frame < 0 || frame >= getNumFrames())
			{
				switch (looping)
				{
				case LOOP_OFF:
					stop();
					return;
					
				case LOOP_ON:
					frame = 0;
					break;
					
				case LOOP_PING_PONG:
					if (frame < 0)
					{
						frame = Math.min(1, getNumFrames() - 1);
						playDirection = 1;
					}
					else
					{
						frame = Math.max(getNumFrames() - 2, 0);
						playDirection = -1;
					}
					break;
				} 
			}
			
			setFrame(frame);			
		}
	}
}
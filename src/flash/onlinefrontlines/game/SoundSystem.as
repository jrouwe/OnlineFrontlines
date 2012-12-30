package onlinefrontlines.game
{
	import mx.core.Application;
	import flash.media.SoundChannel;
	
	/*
	 * Primitive sound system
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
	public class SoundSystem
	{
		// Overriding enabled state
		public static var enabled : Boolean = false;
	
		// Play a sound
		public static function play(sound : Class, loop : Boolean = false) : SoundChannel
		{
			if (sound != null && enabled && Application.application.enableSound.selected)
				return new sound().play(0, loop? 1000 : 0);
			else
				return null;
		}
		
		// Stop a sound
		public static function stop(sound : SoundChannel) : void
		{
			if (sound != null)
				sound.stop();
		}
	}
}

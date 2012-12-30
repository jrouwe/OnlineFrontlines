package onlinefrontlines.lobby
{
	import mx.controls.Image;
	
	/*
	 * Displays rank of a user
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
	public class RankImage extends Image
	{
		[Embed(source='../../../../web/assets/ranks_small/rank01.png')]
		private static var rank01 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank02.png')]
		private static var rank02 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank03.png')]
		private static var rank03 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank04.png')]
		private static var rank04 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank05.png')]
		private static var rank05 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank06.png')]
		private static var rank06 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank07.png')]
		private static var rank07 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank08.png')]
		private static var rank08 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank09.png')]
		private static var rank09 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank10.png')]
		private static var rank10 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank11.png')]
		private static var rank11 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank12.png')]
		private static var rank12 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank13.png')]
		private static var rank13 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank14.png')]
		private static var rank14 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank15.png')]
		private static var rank15 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank16.png')]
		private static var rank16 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank17.png')]
		private static var rank17 : Class;
		[Embed(source='../../../../web/assets/ranks_small/rank18.png')]
		private static var rank18 : Class;
		
		private static var ranks : Array = 
		[
			rank01,
			rank02,
			rank03,
			rank04,
			rank05,
			rank06,
			rank07,
			rank08,
			rank09,
			rank10,
			rank11,
			rank12,
			rank13,
			rank14,
			rank15,
			rank16,
			rank17,
			rank18,
		];
				
		// Return image class of specific image
		public static function getImageClass(number : int) : Class
		{
			return ranks[number - 1];
		}
	}
}
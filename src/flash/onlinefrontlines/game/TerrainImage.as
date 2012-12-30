package onlinefrontlines.game
{
	import mx.controls.Image;
	import flash.display.Sprite;
	import onlinefrontlines.utils.*;
	
	/*
	 * Images for the terrain
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
	public class TerrainImage extends Image
	{
		// Terrain tile images
		[Embed(source='../../../../web/assets/terrain_images/tile_01.png')]
		private static var tile01 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_02.png')]
		private static var tile02 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_03.png')]
		private static var tile03 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_04.png')]
		private static var tile04 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_05.png')]
		private static var tile05 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_06.png')]
		private static var tile06 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_07.png')]
		private static var tile07 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_08.png')]
		private static var tile08 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_09.png')]
		private static var tile09 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_10.png')]
		private static var tile10 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_11_animated.png')]
		private static var tile11 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_12_animated.png')]
		private static var tile12 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_13_animated.png')]
		private static var tile13 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_14.png')]
		private static var tile14 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_15.png')]
		private static var tile15 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_16.png')]
		private static var tile16 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_17.png')]
		private static var tile17 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_18.png')]
		private static var tile18 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_19.png')]
		private static var tile19 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_20.png')]
		private static var tile20 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_21.png')]
		private static var tile21 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_22.png')]
		private static var tile22 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_23.png')]
		private static var tile23 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_24.png')]
		private static var tile24 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_25.png')]
		private static var tile25 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_26.png')]
		private static var tile26 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_27.png')]
		private static var tile27 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_28.png')]
		private static var tile28 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_29.png')]
		private static var tile29 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_30.png')]
		private static var tile30 : Class;
		[Embed(source='../../../../web/assets/terrain_images/tile_31.png')]
		private static var tile31 : Class;
		
		private static var tiles : Array = 
		[
			new AnimatedBitmapData(tile01, 1, 1),
			new AnimatedBitmapData(tile02, 1, 1),
			new AnimatedBitmapData(tile03, 1, 1),
			new AnimatedBitmapData(tile04, 1, 1),
			new AnimatedBitmapData(tile05, 1, 1),
			new AnimatedBitmapData(tile06, 1, 1),
			new AnimatedBitmapData(tile07, 1, 1),
			new AnimatedBitmapData(tile08, 1, 1),
			new AnimatedBitmapData(tile09, 1, 1),
			new AnimatedBitmapData(tile10, 1, 1),
			new AnimatedBitmapData(tile11, 3, 1),
			new AnimatedBitmapData(tile12, 3, 1),
			new AnimatedBitmapData(tile13, 3, 1),
			new AnimatedBitmapData(tile14, 1, 1),
			new AnimatedBitmapData(tile15, 1, 1),
			new AnimatedBitmapData(tile16, 1, 1),
			new AnimatedBitmapData(tile17, 1, 1),
			new AnimatedBitmapData(tile18, 1, 1),
			new AnimatedBitmapData(tile19, 1, 1),
			new AnimatedBitmapData(tile20, 1, 1),
			new AnimatedBitmapData(tile21, 1, 1),
			new AnimatedBitmapData(tile22, 1, 1),
			new AnimatedBitmapData(tile23, 1, 1),
			new AnimatedBitmapData(tile24, 1, 1),
			new AnimatedBitmapData(tile25, 1, 1),
			new AnimatedBitmapData(tile26, 1, 1),
			new AnimatedBitmapData(tile27, 1, 1),
			new AnimatedBitmapData(tile28, 1, 1),
			new AnimatedBitmapData(tile29, 1, 1),
			new AnimatedBitmapData(tile30, 1, 1),
			new AnimatedBitmapData(tile31, 1, 1)
		];
		
		// Sprite to attach bitmap to
		private var sprite : Sprite;

		// Set the unit image
		public function setImage(number : int) : void
		{
			// Remove previous sprite
			if (sprite != null)
				removeChild(sprite);
				
			// Add new sprite
			sprite = new Sprite();
			addChild(sprite);

			// Add bitmap
			var animatedBitmap : AnimatedBitmap = TerrainImage.createAnimatedBitmap(number);
			sprite.addChild(animatedBitmap);
		}		
				
		// Get number of different tiles
		public static function getNumTiles() : int
		{
			return tiles.length;
		}
		
		// Return animated bitmap data for tile
		public static function getAnimatedBitmapData(number : int) : AnimatedBitmapData
		{
			return tiles[number - 1];
		}
		
		// Return static bitmap data for tile
		public static function createStaticBitmap(number : int) : AnimatedBitmap
		{
			return new AnimatedBitmap(tiles[number - 1]);
		}		

		// Return animated bitmap data for tile
		public static function createAnimatedBitmap(number : int) : AnimatedBitmap
		{
			var animatedBitmap : AnimatedBitmap = new AnimatedBitmap(tiles[number - 1]);
			animatedBitmap.play(1000, AnimatedBitmap.LOOP_PING_PONG, false);
			return animatedBitmap;
		}		
	}
}
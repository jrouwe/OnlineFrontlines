package onlinefrontlines.utils
{
	import mx.core.Application;
	import flash.geom.Rectangle;
	import flash.geom.Point;
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Shape;
	import flash.display.Graphics;
	import flash.utils.ByteArray;
	import flash.net.*;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.TimerEvent;
	import flash.utils.Timer;

	/*
	 * Basic utilities
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
	public class Tools
	{
		// Table to convert easily to hex		
        private static const hexTab : String = "0123456789abcdef";

		/**
		 * Get random number between min and max
		 */
		public static function randRange(min : Number, max : Number) : Number 
		{
			return Math.floor(Math.random() * (max - min + 1)) + min;
		}
		
		/**
		 * Get random value with standard normally distributed values (P(x) = 1 / sqrt(2 PI) * exp(-x^2 / 2))
		 * 
		 * @see http://en.wikipedia.org/wiki/Gaussian_distribution
		 */
		public static function standardGaussianRandom() : Number
		{
			return Math.sqrt(-2.0 * Math.log(Math.random())) * Math.cos(2 * Math.PI * Math.random());
		}
		
		/**
		 * Get random value with normally distributed values (P(x) = 1 / (sigma * sqrt(2 PI)) * exp(-(x - mu)^2 / (2 * sigma^2)))
		 * 
		 * @param mu Value the distribution centers around
		 * @param sigma Standard deviation of distribution
		 */
		public static function gaussianRandom(mu : Number, sigma : Number) : Number
		{
			return standardGaussianRandom() * sigma + mu;
		}
		
		/**
		 * Create two dimensional array
		 */
		public static function createArray(numRows : int, numCols : int, defaultVal : Object) : Array
		{ 
			var a : Array = new Array(numRows); 
			
			for (var i : int = 0; i < numRows; i++) 
			{ 
				a[i] = new Array(numCols); 
				
				for (var j : int = 0; j < numCols; j++) 
				{ 
					a[i][j] = defaultVal; 
				} 
			} 
			
			return a; 
		} 
		
		/**
		 * Create string from timer that indicates number of milli seconds left
		 */
		public static function timerToString(timeLeft : int) : String
		{
			var rv : String;

			if (timeLeft < 0)
				return "0";
			
			// Make seconds
			timeLeft /= 1000;
			
			// Store seconds
			var seconds : int = timeLeft % 60;
			timeLeft /= 60;
			if (timeLeft > 0 && seconds < 10)
				rv = "0" + seconds;
			else
				rv = seconds.toString();
			if (timeLeft <= 0)
				return rv;
				
			// Store minutes
			var minutes : int = timeLeft % 60;
			timeLeft /= 60;
			if (timeLeft > 0 && minutes < 10)
				rv = "0" + minutes + ":" + rv;
			else
				rv = minutes + ":" + rv;
			if (timeLeft <= 0)
				return rv;
				
			// Store hours
			var hours : int = timeLeft % 24;
			timeLeft /= 24;
			if (timeLeft > 0 && hours < 10)
				rv = "0" + hours + ":" + rv;
			else
				rv = hours + ":" + rv;
			if (timeLeft <= 0)
				return rv;
				
			// Store days
			rv = timeLeft + " " + rv;
			return rv;
		}
		
		/**
		 * Convert modifier to string (add + when number is positive)
		 */		
		public static function modifierToString(modifier : int) : String
		{
			if (modifier <= 0)
				return String(modifier);
			else
				return "+" + modifier;
		}
		
		/**
		 * Create rectangle
		 */
		public static function createRectangle(width : int, height : int, color : int = 0xffffff, alpha : Number = 1) : Shape
		{
			var s : Shape = new Shape();
			var g : Graphics = s.graphics;
			g.beginFill(color, alpha);
			g.moveTo(0, 0);
			g.lineTo(width, 0);
			g.lineTo(width, height);
			g.lineTo(0, height);
			g.lineTo(0, 0);
			g.endFill();
			return s;
		}
		
		/**
		 * Create sub bitmap
		 */
		public static function subBitmap(bitmapClass : Class, x : int, y : int, w : int, h : int) : BitmapData
		{
			var source : Bitmap = new bitmapClass();

			var data : BitmapData = new BitmapData(w, h);
			data.copyPixels(source.bitmapData, new Rectangle(x, y, w, h), new Point(0, 0));

			return data;
		}

		/**
		 * Process a request
		 */
		public static function processRequest(action : String, data : String, requestComplete : Function, requestFailed : Function) : void
		{
			// Generate url
			var fullUrl : String = Application.application.parameters.appUrl != null? Application.application.parameters.appUrl + "/" + action : action;
			
			// Create request				
			var req : URLRequest = new URLRequest(fullUrl);
			req.data = data;
			
			// Use post method
			req.method = URLRequestMethod.POST;
			
			// Send request
			var ldr : URLLoader = new URLLoader();
			ldr.addEventListener(Event.COMPLETE,
				function (evt : Event) : void
				{
					var xmlRes : XML = new XML(evt.target.data);			
			
					if (xmlRes.code == -100)
					{
						// This is a redirect, redo the request
						processRequest(action, data, requestComplete, requestFailed)
					}
					else
					{
						// Success
						requestComplete(xmlRes);
					}
				});
			ldr.addEventListener(IOErrorEvent.IO_ERROR, 
				function (evt : IOErrorEvent) : void
				{
					Logger.log("processRequest failed: " + evt.text);
					
					// Failure
					requestFailed();
				});
			ldr.load(req);
		}
		
		/**
		 * Navigate to page
		 */
		public static function navigateTo(action : String, data : String, target : String) : void
		{
			// Generate url
			var fullUrl : String = Application.application.parameters.appUrl + "/" + action;
			
			// Create request				
			var req : URLRequest = new URLRequest(fullUrl);
			req.data = data;
			
			// Show browser
			navigateToURL(req, target);
		}

		/**
		 * Get full URL from application relative path (i.e. Login.do becomes http://localhost/OnlineFrontlines/Login.do)
		 */
		public static function getAppURL(relativePath : String) : String
		{
			return Application.application.parameters.appUrl + "/" + relativePath;
		}

		/**
		 * Get full URL from uploaded images relative path (i.e. image.bmp becomes http://localhost/uploaded_images/image.bmp)
		 */
		public static function getImagesURL(relativePath : String) : String
		{
			return Application.application.parameters.imagesUrl + "/" + relativePath;
		}
		
		/**
		 * Get full URL from assets relative path (i.e. test.bmp becomes http://localhost/OnlineFronlines/assets/test.bmp)
		 */
		public static function getAssetsURL(relativePath : String) : String
		{
			return Application.application.parameters.assetsUrl + "/" + relativePath;
		}
		
		/**
		 * Convert byte array to hex string
		 */
		public static function byteArrayToHexString(input : ByteArray) : String 
		{
	        var output : String = "";	        
			input.position = 0;
			while (input.bytesAvailable)
			{
				var b : int = input.readUnsignedByte();
				output += hexTab.charAt(b >> 4) + hexTab.charAt(b & 0xf);
			}			
			return output;
    	}
	}
}
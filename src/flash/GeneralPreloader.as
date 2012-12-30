package
{
    import flash.display.*;
    import flash.utils.*;
    import flash.events.*;
    import mx.preloaders.*;
    import mx.events.*;

	/*
	 * Downloading progress bar
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
    public class GeneralPreloader extends DownloadProgressBar
    {
    	// Progress bar
        private var inside_mc : MovieClip;

		// Constructor    
        public function GeneralPreloader()
        {   
			var w : int = 990;
			var h : int = 595;
			
			var background_mc : MovieClip = new MovieClip();
			addChild(background_mc);
			var back : Graphics = background_mc.graphics;
			back.beginFill(0x000000);
            back.moveTo(0, 0);
            back.lineTo(w, 0);
            back.lineTo(w, h);
            back.lineTo(0, h);
            back.lineTo(0, 0);
            back.endFill();

            inside_mc = new MovieClip();
            inside_mc.x = 10;
            inside_mc.y = h - 20;
            addChild(inside_mc);
            var inside : Graphics = inside_mc.graphics;
            inside.beginFill(0x3B5D19);
            inside.moveTo(0, 0);
            inside.lineTo(w - 20, 0);
            inside.lineTo(w - 20, 10);
            inside.lineTo(0, 10);
            inside.lineTo(0, 0);
            inside.endFill();
            inside_mc.scaleX = 0;

            var outline_mc : MovieClip = new MovieClip();            
            addChild(outline_mc);
            var outline : Graphics = outline_mc.graphics;
            outline.lineStyle(1, 0x505050);
            outline.moveTo(10, h - 20);
            outline.lineTo(w - 10, h - 20);
            outline.lineTo(w - 10, h - 10);
            outline.lineTo(10, h - 10);
            outline.lineTo(10, h - 20);
        }
    
        // Define the event listeners for the preloader events.
        override public function set preloader(p : Sprite) : void 
        {
            // Listen for the relevant events
            p.addEventListener(ProgressEvent.PROGRESS, myHandleProgress);   
            p.addEventListener(FlexEvent.INIT_COMPLETE, myHandleInitEnd);
        }
    
        // Event listeners for the ProgressEvent.PROGRESS event.
        private function myHandleProgress(event : ProgressEvent) : void 
        {
			inside_mc.scaleX = Number(event.bytesLoaded) / Number(event.bytesTotal);
        }
    
        // Event listeners for the FlexEvent.INIT_COMPLETE event.
        private function myHandleInitEnd(event : Event) : void 
        {
        	dispatchEvent(new Event(Event.COMPLETE));
        }        
    }
}

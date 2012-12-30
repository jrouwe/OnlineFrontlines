package onlinefrontlines.admin.web;

import onlinefrontlines.web.*;

/**
 * This action runs the garbage collector and tries to free up as much memory as possible
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
public class ForceGCAction extends WebAction 
{
	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	Runtime r = Runtime.getRuntime();
		r.runFinalization();
		r.gc();

        return getSuccessView();
    }
}

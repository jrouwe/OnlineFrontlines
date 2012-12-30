package onlinefrontlines.countryedit.web;

import java.util.*;
import onlinefrontlines.web.*;
import onlinefrontlines.game.CountryConfigDAO;

/**
 * This action queries the country configs in the database and presents a list to the jsp page.
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
public class CountryListAction extends WebAction 
{
	/**
	 * Access to all country configs in the database
	 */
	public ArrayList<CountryConfigDAO.Summary> configs;
		
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	configs = CountryConfigDAO.list(user.id, Integer.MAX_VALUE, false, false, true, false);
    	
    	return getSuccessView();
    }
}

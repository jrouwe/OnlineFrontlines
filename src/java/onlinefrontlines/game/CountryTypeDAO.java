package onlinefrontlines.game;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;
import org.apache.log4j.Logger;

/**
 * This class communicates with the database and manages reading/writing CountryType objects
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
public class CountryTypeDAO 
{
	private static final Logger log = Logger.getLogger(CountryTypeDAO.class);

	/**
	 * Loads all country types from the database
	 * 
	 * @throws SQLException
	 */
	public static ArrayList<CountryType> loadAll() throws SQLException
	{
		ArrayList<CountryType> list = new ArrayList<CountryType>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("SELECT id, name, description FROM country_types");
	    	helper.executeQuery();

	    	while (helper.nextRecord())
		        list.add(new CountryType(helper.getInt(1), helper.getString(2), helper.getString(3)));
        }
        finally
        {
        	helper.close();
        }
        
        log.info("Loaded " + list.size() + " country types");
        
        return list;
	}
}

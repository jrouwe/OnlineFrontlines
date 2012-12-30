package onlinefrontlines.game;

import java.util.*;

/**
 * Type of country 
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
public final class CountryType 
{
	/**
	 * Id of the country type
	 */
	private final int id;
	
	/**
	 * Name of the country type
	 */
	private final String name;
	
	/**
	 * Description of the country type
	 */
	private final String description;
	
	/**
	 * Constructor
	 */
	public CountryType(int id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Get id of country type
	 */
	public final int getId()
	{
		return id;
	}

	/**
	 * Get country type name
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Get country type description
	 */
	public final String getDescription()
	{
		return description;
	}
	
	/**
     * Maps id to country type
     */
	public static HashMap<Integer, CountryType> allTypesMap = new HashMap<Integer, CountryType>(); 

	/**
	 * All country types
	 */
	public static ArrayList<CountryType> allTypes = new ArrayList<CountryType>();
	
	/**
	 * Load all types from the database
	 * 
	 * @throws SQLException
	 */
	static public void loadAll() throws java.sql.SQLException
	{
		allTypesMap.clear();
    	allTypes = CountryTypeDAO.loadAll();
    	
    	for (CountryType c : allTypes)
    		allTypesMap.put(c.id, c);
	}
	
	/**
	 * Remove all loaded country types
	 */
	static public void unloadAll()
	{
		allTypesMap.clear();
		allTypes.clear();
	}		
}

package onlinefrontlines.game;

/**
 * This exception is used to indicate that not all units could be deployed 
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
public class DeploymentFailedException extends Exception 
{
	static final long serialVersionUID = 0;
	
	private UnitConfig unitConfig;
	private Faction faction;
	
	/**
	 * Constructor
	 * 
	 * @param message Exception message
	 */
	public DeploymentFailedException(UnitConfig unitConfig, Faction faction, CountryConfig countryConfig)
	{
		super("Could not place unit '" + unitConfig.name + "' (id=" + unitConfig.id + ") "
				+ "on map '" + countryConfig.getMapConfig().name + "' (id=" + countryConfig.getMapConfig().id + ") "
				+ "using country '" + countryConfig.name + "' (id=" + countryConfig.id + ") "
				+ "for faction " + faction + " created by " + countryConfig.creatorUserId);

		this.unitConfig = unitConfig;
		this.faction = faction;
	}
	
	/**
	 * Get unit name
	 */
	public String getUnit()
	{
		return unitConfig.name;
	}
	
	/**
	 * Get unit faction
	 */
	public String getFaction()
	{
		return faction.toString();
	}
}

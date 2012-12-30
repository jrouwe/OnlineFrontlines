package onlinefrontlines.armystats;

import java.sql.SQLException;
import onlinefrontlines.Army;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and gets stats on an army
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
public class ArmyStatsDAO 
{
	/**
	 * Get stats for an army
	 * 
	 * @param army Army to get stats for
	 */
	public static ArmyStats getStats(Army army) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	ArmyStats a = new ArmyStats();

	    	helper.prepareQuery("SELECT COUNT(1), SUM(gamesPlayed), SUM(gamesWon), SUM(totalPoints) FROM users LEFT JOIN user_stats ON user_stats.userId=users.id WHERE army=?");
	    	helper.setInt(1, Army.toInt(army));
	    	helper.executeQuery();	    	
	    	if (helper.nextRecord())
	    	{
	    		a.members = helper.getInt(1);
	    		a.gamesPlayed = helper.getInt(2);
	    		a.gamesWon = helper.getInt(3);
	    		a.totalPoints = helper.getInt(4);
	    	}
	    	
	    	helper.prepareQuery("SELECT COUNT(1) FROM lobby_country_state JOIN users ON lobby_country_state.ownerUserId=users.id WHERE users.army=?");
	    	helper.setInt(1, Army.toInt(army));
	    	helper.executeQuery();	    	
	    	if (helper.nextRecord())
	    	{
	    		a.countriesOwned = helper.getInt(1);
	    	}

	    	return a;
        }
        finally
        {
        	helper.close();
        }		
	}
}
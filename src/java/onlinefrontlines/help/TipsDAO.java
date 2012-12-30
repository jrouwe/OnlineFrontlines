package onlinefrontlines.help;

import java.util.ArrayList;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing of the Tips object
 * 
 * @see onlinefrontlines.help.Tips
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
public class TipsDAO 
{
	/**
	 * Gets all tips entries
	 */
	public static ArrayList<Tips.Entry> loadTips() throws SQLException
	{
		ArrayList<Tips.Entry> tips = new ArrayList<Tips.Entry>();

		DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		// Find all sections
	    	helper.prepareQuery("SELECT id, image, text, showInGame FROM tips");
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		Tips.Entry s = new Tips.Entry();
	    		s.id = helper.getInt(1);
	    		s.image = helper.getString(2);
	    		s.text = helper.getString(3);
	    		s.showInGame = helper.getInt(4) != 0;
	            tips.add(s);
	    	}
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }
        
        return tips;
	}
}

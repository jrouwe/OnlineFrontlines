package onlinefrontlines.userstats.web;

import java.sql.SQLException;
import onlinefrontlines.utils.CacheException;
import onlinefrontlines.userstats.*;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.web.*;

/**
 * This action shows the leaderboard
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
public class UserStatsListAction extends WebAction 
{
	public UserLeaderboard getTotalPoints()
	{
		try
		{
			return UserLeaderboardCache.getInstance().get(UserLeaderboardCache.BOARD_TOTAL_POINTS);
		}
		catch (CacheException e)
		{
			Tools.logException(e);
	
			return null;
		}
	}
	
	public UserLeaderboard getUnitsDestroyed()
	{
		try
		{
			return UserLeaderboardDAO.getLeaderboardUnitsDestroyed();
		}
		catch (SQLException e)
		{
			Tools.logException(e);
	
			return null;
		}
	}

	public UserLeaderboard getWinPercentage()
	{
		try
		{
			return UserLeaderboardDAO.getLeaderboardWinPercentage();
		}
		catch (SQLException e)
		{
			Tools.logException(e);
	
			return null;
		}
	}
	
	public UserLeaderboard getTotalCaptures()
	{
		try
		{
			return UserLeaderboardDAO.getLeaderboardTotalCaptures();
		}
		catch (SQLException e)
		{
			Tools.logException(e);
	
			return null;
		}
	}

	public UserLeaderboard getTotalCountries()
	{
		try
		{
			return UserLeaderboardDAO.getLeaderboardTotalCountries();
		}
		catch (SQLException e)
		{
			Tools.logException(e);
	
			return null;
		}
	}
}

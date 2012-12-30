package onlinefrontlines.game.web;
import onlinefrontlines.web.*;
import java.util.ArrayList;
import onlinefrontlines.game.*;

/**
 * This action shows the game balance
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
public class CountryBalanceAction extends WebAction 
{
	/**
     * Access to the balance table
     */
    public String[][] balanceTable;

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	ArrayList<CountryConfigDAO.Summary> summary = CountryConfigDAO.list(0, Integer.MAX_VALUE, true, false, false, false);
    	
    	balanceTable = new String[summary.size() + 1][7];

    	balanceTable[0][0] = "ID";
    	balanceTable[0][1] = "Country Name";
    	balanceTable[0][2] = "Points Player 1 (units)";
    	balanceTable[0][3] = "Points Player 2 (units)";
    	balanceTable[0][4] = "Points Player 1 (terrain)";
    	balanceTable[0][5] = "Points Player 2 (terrain)";
    	balanceTable[0][6] = "Point Limit";

    	int line = 1;
    	for (CountryConfigDAO.Summary s : summary)
    	{
    		CountryConfig c = CountryConfigCache.getInstance().get(s.id);    		
    		MapConfig m = c.getMapConfig();    		
    		DeploymentConfig c1 = DeploymentConfigCache.getInstance().get(c.deploymentConfigId[0]);
    		DeploymentConfig c2 = DeploymentConfigCache.getInstance().get(c.deploymentConfigId[1]);
    		
    		int vpu1 = c1.getTotalVictoryPoints();
    		int vpu2 = c2.getTotalVictoryPoints();
    		int vpt1 = m.getVictoryPoints(Faction.f1);
    		int vpt2 = m.getVictoryPoints(Faction.f2);
    		
    		balanceTable[line][0] = Integer.toString(c.id);
    		balanceTable[line][1] = c.getName();
    		balanceTable[line][2] = Integer.toString(vpu1);
       		balanceTable[line][3] = Integer.toString(vpu2);
       		balanceTable[line][4] = Integer.toString(vpt1);
       		balanceTable[line][5] = Integer.toString(vpt2);
       		balanceTable[line][6] = Integer.toString(c.scoreLimit);
    		++line;
    	}
   		
        return getSuccessView();
    }
}

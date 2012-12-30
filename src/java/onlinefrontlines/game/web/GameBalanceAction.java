package onlinefrontlines.game.web;

import onlinefrontlines.web.*;
import java.util.Random;
import java.text.*;
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
public class GameBalanceAction extends WebAction 
{
	/**
     * Access to the balance talbe
     */
    public String[][] balanceTable;

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	Random random = new Random();
    	
    	DecimalFormat f = new DecimalFormat("#.#");

		balanceTable = new String[UnitConfig.allUnits.size() + 1][UnitConfig.allUnits.size() + 1];
    	
		for (int i = 0; i < UnitConfig.allUnits.size(); ++i)
		{
			UnitConfig u1 = UnitConfig.allUnits.get(i);
			TerrainConfig t1 = u1.unitClass == UnitClass.air? TerrainConfig.airTerrain : TerrainConfig.plainsTerrain;
			
			balanceTable[i + 1][0] = u1.name;
			balanceTable[0][i + 1] = u1.name;

	    	for (int j = 0; j < UnitConfig.allUnits.size(); ++j)
	    	{
				UnitConfig u2 = UnitConfig.allUnits.get(j);
				TerrainConfig t2 = u2.unitClass == UnitClass.air? TerrainConfig.airTerrain : TerrainConfig.plainsTerrain;
				
				final int numSamples = 100;
				final int maxNumAttacks = 15;

				// Armour loss
				double avgArmourLoss = 0; 
				int armourLoss[] = new int[numSamples];
				
				// Wins
				double avgAttacksToWin = 0;
				int attacksToWin[] = new int[numSamples];
				int numWins = 0;
				
				// Sample a number of times
				for (int k = 0; k < numSamples; ++k)
				{
					UnitState ui1 = new UnitState(-1, 0, 0, Faction.f1, u1);
					UnitState ui2 = new UnitState(-1, 1, 0, Faction.f2, u2);
					
					// First attack calculates max damage
					armourLoss[k] = ui2.armour;
					if (ui1.canAttackUnitClass(ui2.unitConfig.unitClass, ui2.faction))
						ui1.attackAndCounter(ui2, t1, t2, random);
					armourLoss[k] -= ui2.armour;
					avgArmourLoss += armourLoss[k];
					
					// Finish first attack
					int numAttacks = 1;
					for (int a = 1; a < u1.actions; ++a)
						if (ui1.canAttackUnitClass(ui2.unitConfig.unitClass, ui2.faction))
							ui1.attackAndCounter(ui2, t1, t2, random);
					for (int a = 0; a < u2.actions; ++a)
						if (ui2.canAttackUnitClass(ui1.unitConfig.unitClass, ui1.faction))
							ui2.attackAndCounter(ui1, t2, t1, random);

					// Subsequent attacks determine winner
					for (int l = 0; l < maxNumAttacks; ++l)
					{
						// Check if one of the units is dead
						if (ui2.armour <= 0 || ui2.faction == Faction.f1)
						{
							// Winner
							attacksToWin[numWins] = numAttacks;
							avgAttacksToWin += numAttacks;
							numWins++;
							break;
						}
						if (ui1.armour <= 0 || ui1.faction == Faction.f2)
						{
							// Loser
							break;
						}
						
						// Perform another attack
						for (int a = 0; a < u1.actions; ++a)
							if (ui1.canAttackUnitClass(ui2.unitConfig.unitClass, ui2.faction))
								ui1.attackAndCounter(ui2, t1, t2, random);
						for (int a = 0; a < u2.actions; ++a)
							if (ui2.canAttackUnitClass(ui1.unitConfig.unitClass, ui1.faction))
								ui2.attackAndCounter(ui1, t2, t1, random);
						numAttacks++;
					}
				}
				
				// Complete calculating averages
				avgArmourLoss /= numSamples;
				if (numWins > 0)
					avgAttacksToWin /= numWins;
				
				// Calculate armour loss deviation
				double armourLossDeviation = 0;
				int armourLossMin = Integer.MAX_VALUE;
				int armourLossMax = 0;
				for (int k = 0; k < numSamples; ++k)
				{
					armourLossDeviation += (avgArmourLoss - armourLoss[k]) * (avgArmourLoss - armourLoss[k]);
					armourLossMin = Math.min(armourLossMin, armourLoss[k]);
					armourLossMax = Math.max(armourLossMax, armourLoss[k]);
				}
				armourLossDeviation = Math.sqrt(armourLossDeviation / numSamples);

				// Calculate attacks to win deviation
				double attacksToWinDeviation = 0;
				int attacksToWinMin = Integer.MAX_VALUE;
				int attacksToWinMax = 0;
				for (int k = 0; k < numWins; ++k)
				{
					attacksToWinDeviation += (avgAttacksToWin - attacksToWin[k]) * (avgAttacksToWin - attacksToWin[k]);
					attacksToWinMin = Math.min(attacksToWinMin, attacksToWin[k]);
					attacksToWinMax = Math.max(attacksToWinMax, attacksToWin[k]);
				}
				if (numWins > 0)
					attacksToWinDeviation = Math.sqrt(attacksToWinDeviation / numWins);
				
				// Calculate win percentage
				double win_percentage = numWins * 100.0 / numSamples;

				// Format result
				String result = "X";
				if (avgArmourLoss > 0.01)
				{
					result = "DMG: " + f.format(avgArmourLoss) + "\u00B1" + f.format(armourLossDeviation) + " [" + armourLossMin + "," + armourLossMax + "]\n";
					result += "WIN: " + f.format(win_percentage) + "%\n";
					if (win_percentage > 0.1)
						result += "#RND: " + f.format(avgAttacksToWin) + "\u00B1" + f.format(attacksToWinDeviation) + " [" + attacksToWinMin + "," + attacksToWinMax + "]\n";
				}
    			balanceTable[i + 1][j + 1] = result;
    		}
    	}
   		
        return getSuccessView();
    }
}

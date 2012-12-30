package onlinefrontlines.game;

/**
 * Indicates current state in publish flow
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
public enum PublishState
{
	unpublished,
	requestToPublish,
	published;
	
	/**
	 * Convert publish state to integer value
	 */
	public static int toInt(PublishState state)
	{
		switch (state)
		{
		case unpublished:
			return 0;
		case requestToPublish:
			return 1;
		case published:
			return 2;
		default:
			return 0;
		}
	}
	
	/**
	 * Convert integer to publish state
	 */
	public static PublishState fromInt(int value)
	{
		switch (value)
		{
		case 0:
			return unpublished;
		case 1:
			return requestToPublish;
		case 2:
			return published;
		default:
			return unpublished;
		}
	}
}

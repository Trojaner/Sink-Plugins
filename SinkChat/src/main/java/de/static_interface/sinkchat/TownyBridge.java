/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkchat;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import de.static_interface.sinklibrary.SinkLibrary;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TownyBridge
{
    /**
     * Get resident by name
     *
     * @param name Name of the resident
     * @return null when resident not found or offline
     */
    public static Resident getResident(String name)
    {
        for ( Resident resident : SinkChat.getTowny().getTownyUniverse().getActiveResidents() )
        {
            if ( resident.getName().equals(name) )
            {
                return resident;
            }
        }
        return null;
    }

    /**
     * Get Towny prefix of plaYER
     *
     * @param player Player
     * @return {@code ChatColor.GRAY + "[" + "Nation Tag" + ChatColor.GRAY + "] "} (Nation Tag in gray brackets)
     */
    public static String getTownyPrefix(Player player)
    {
        if ( SinkChat.isTownyAvailable() && SinkLibrary.getSettings().isTownyEnabled() )
        {
            Resident res = getResident(player.getName());

            if ( res == null ) return "";

            Town town;

            try
            {
                town = res.getTown();
            }
            catch ( NotRegisteredException ignored )
            {
                return "";
            }

            if ( town == null ) return "";
            if ( !town.hasNation() ) return "";

            Nation nation;
            try
            {
                nation = town.getNation();
            }
            catch ( NotRegisteredException e )
            {
                return "";
            }

            return ChatColor.GRAY + "[" + ChatColor.stripColor(nation.getTag()) + ChatColor.GRAY + "] ";
        }
        return "";
    }
}
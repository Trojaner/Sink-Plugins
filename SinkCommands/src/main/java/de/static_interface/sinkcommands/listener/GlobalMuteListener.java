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

package de.static_interface.sinkcommands.listener;

import de.static_interface.sinkcommands.SinkCommands;
import de.static_interface.sinkcommands.commands.GlobalmuteCommand;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GlobalMuteListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        SinkUser user = SinkLibrary.getUser(event.getPlayer());
        if ( SinkCommands.globalmuteEnabled && !user.hasPermission("sinkcommands.globalmute.bypass") )
        {
            event.getPlayer().sendMessage(GlobalmuteCommand.PREFIX + "Du kannst nicht schreiben wenn der globale Mute aktiviert ist.");
            event.setCancelled(true);
        }
    }
}

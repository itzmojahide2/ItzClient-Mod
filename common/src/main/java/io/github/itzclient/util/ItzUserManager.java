/*
 * Copyright © 2025 itzmojahide2 & Contributors
 *
 * This file is part of ItzClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */
package io.github.itzclient.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItzUserManager {
    
    // A thread-safe Set is used to prevent issues in multiplayer environments.
    private static final Set<UUID> ITZ_USERS = Collections.synchronizedSet(new HashSet<>());

    /**
     * Adds a player's UUID to the list of known ItzClient users.
     * This is called when we receive a plugin message from a player.
     * @param uuid The UUID of the player.
     */
    public static void addUser(UUID uuid) {
        ITZ_USERS.add(uuid);
    }

    /**
     * Removes a player's UUID, typically when they disconnect.
     * @param uuid The UUID of the player.
     */
    public static void removeUser(UUID uuid) {
        ITZ_USERS.remove(uuid);
    }
    
    /**
     * Checks if a player is a known ItzClient user. This is called by the rendering mixins.
     * @param uuid The UUID of the player to check.
     * @return true if the player is using ItzClient, false otherwise.
     */
    public static boolean isItzUser(UUID uuid) {
        return ITZ_USERS.contains(uuid);
    }

    /**
     * Clears all users from the list, called when disconnecting from a server.
     */
    public static void clear() {
        ITZ_USERS.clear();
    }
}

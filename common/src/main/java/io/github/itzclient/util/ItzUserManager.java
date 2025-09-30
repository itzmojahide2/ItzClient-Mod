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

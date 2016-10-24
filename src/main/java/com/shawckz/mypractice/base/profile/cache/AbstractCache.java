/*
 * Copyright (c) Jonah Seguin (Shawckz) 2016.  You may not copy, re-sell, distribute, modify, or use any code contained in this document or file, collection of documents or files, or project.  Thank you.
 */

package com.shawckz.mypractice.base.profile.cache;

import com.shawckz.mypractice.base.database.mongo.AutoMongo;
import com.shawckz.mypractice.base.profile.reflex.PracticePlayer;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Jonah on 6/11/2015.
 */
public abstract class AbstractCache implements Listener {

    private final ConcurrentMap<String, PracticePlayer> players = new ConcurrentHashMap<>(); // Player username as key
    private final ConcurrentMap<String, PracticePlayer> playersUUID = new ConcurrentHashMap<>(); // Player UniqueID as key
    private final Set<Player> onlinePlayers = new HashSet<>();
    private final Plugin plugin;
    private final Class<? extends PracticePlayer> aClass;

    public AbstractCache(Plugin plugin) {
        this.plugin = plugin;
        this.aClass = PracticePlayer.class;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected ConcurrentMap<String, PracticePlayer> getPlayers() {
        return players;
    }

    public Set<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    /**
     * Returns a PracticePlayer instance if in local cache, if not in local cache this will attempt to load their PracticePlayer object from the database
     * If they are not in the database or the name is null, will return null.
     *
     * @param name The Player's name to get a PracticePlayer instance of
     *
     * @return The PracticePlayer, null if not found in database && cache
     *
     * Please note that the name is case sensitive.
     */
    protected PracticePlayer getBasePlayer(String name) {
        if (players.containsKey(name)) {
            return players.get(name);
        }
        else {
            PracticePlayer cp = loadReflexPlayer(name);
            if (cp != null) {
                return cp;
            }
            else {
                return null;
            }
        }
    }

    protected PracticePlayer getBasePlayerByUniqueId(String uuid) {
        if (playersUUID.containsKey(uuid)) {
            return playersUUID.get(uuid);
        }
        else {
            PracticePlayer cp = loadReflexPlayerByUniqueId(uuid);
            if (cp != null) {
                put(cp);
                return cp;
            }
            else {
                return null;
            }
        }
    }

    public PracticePlayer loadReflexPlayer(String name) {
        final String key = "username"; //Matching the field in PracticePlayer
        List<AutoMongo> autoMongos = PracticePlayer.select(new Document(key, name), aClass);
        for (AutoMongo mongo : autoMongos) {
            if (mongo instanceof PracticePlayer) {
                PracticePlayer PracticePlayer = (PracticePlayer) mongo;
                return PracticePlayer;
            }
        }
        return null;
    }

    public PracticePlayer loadReflexPlayerByUniqueId(String id) {
        final String key = "uuid"; //Matching the field in PracticePlayer
        List<AutoMongo> autoMongos = PracticePlayer.select(new Document(key, id), aClass);
        for (AutoMongo mongo : autoMongos) {
            if (mongo instanceof PracticePlayer) {
                PracticePlayer PracticePlayer = (PracticePlayer) mongo;
                return PracticePlayer;
            }
        }
        return null;
    }

    public PracticePlayer getBasePlayer(Player p) {
        return getBasePlayer(p.getName());
    }

    /**
     * Gets if the player by name is in the local cache
     *
     * @param name The Player's name
     *
     * @return true if in the cache, false if not
     */
    public boolean contains(String name) {
        return players.containsKey(name);
    }

    /**
     * Adds a PracticePlayer to the local cache.  Does not get cleared until server restart.
     *
     * @param PracticePlayer The PracticePlayer to add to the local cache
     */
    public void put(PracticePlayer PracticePlayer) {
        players.put(PracticePlayer.getName(), PracticePlayer);
        playersUUID.put(PracticePlayer.getUniqueId(), PracticePlayer);
    }

    /**
     * Clear the local cache.
     * Used in onDisable to prevent memory leaks (due to the cache being static)
     */
    public void clear() {
        players.clear();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCache(final AsyncPlayerPreLoginEvent e) {
        final String name = e.getName();
        final String uuid = e.getUniqueId().toString();
        PracticePlayer cp = loadReflexPlayer(e.getName());
        if (cp != null) {
            put(cp);
        }
        else {
            cp = create(name, uuid);
            put(cp);
            cp.update();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (contains(p.getName())) {
            PracticePlayer cp = getBasePlayer(p);
            cp.setOnline(true);
            init(p, cp);
        }
        onlinePlayers.add(p);
    }

    public abstract PracticePlayer create(String name, String uuid);

    public abstract void init(Player player, PracticePlayer PracticePlayer);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (contains(p.getName())) {
            PracticePlayer practicePlayer = getBasePlayer(p);
            practicePlayer.setOnline(false);
            save(p);
        }
        if (onlinePlayers.contains(p)) {
            onlinePlayers.remove(p);
        }
    }

    public void save(final Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveSync(p);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void saveSync(Player p) {
        if (contains(p.getName())) {
            final PracticePlayer practicePlayer = getBasePlayer(p);
            practicePlayer.update();
        }
    }

}

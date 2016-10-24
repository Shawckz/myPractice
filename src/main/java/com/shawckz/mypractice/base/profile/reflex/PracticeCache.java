/*
 * Copyright (c) Jonah Seguin (Shawckz) 2016.  You may not copy, re-sell, distribute, modify, or use any code contained in this document or file, collection of documents or files, or project.  Thank you.
 */

package com.shawckz.mypractice.base.profile.Practice;

import com.shawckz.mypractice.Practice;
import com.shawckz.mypractice.base.profile.cache.AbstractCache;
import com.shawckz.mypractice.base.profile.reflex.PracticePlayer;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

/**
 * Created by Jonah on 6/15/2015.
 */
public class PracticeCache extends AbstractCache {

    public PracticeCache(Practice plugin) {
        super(plugin);
    }

    //See superclass for documentation
    public PracticePlayer getPracticePlayer(String name) {
        PracticePlayer cachePlayer = getBasePlayer(name);
        if (cachePlayer != null) {
            return getBasePlayer(name);
        }
        return null;
    }

    public PracticePlayer getPracticePlayerByUniqueId(String uuid) {
        PracticePlayer cachePlayer = getBasePlayerByUniqueId(uuid);
        if (cachePlayer != null) {
            return getPracticePlayerByUniqueId(uuid);
        }
        return null;
    }

    //See superclass for documentation
    public PracticePlayer getPracticePlayer(Player p) {
        if (p != null) {
            return getPracticePlayer(p.getName());
        }
        return null;
    }

    public Set<PracticePlayer> getOnlinePracticePlayers() {
        return getPlayers().values().stream().filter(PracticePlayer::isOnline).collect(Collectors.toSet());
    }

    @Override
    public PracticePlayer create(String name, String uuid) {
        return new PracticePlayer(name, uuid);
    }

    @Override
    public void init(Player p, PracticePlayer practicePlayer) {
        practicePlayer.setBukkitPlayer(p);

        // TODO
    }
}

/*
 * Copyright (c) Jonah Seguin (Shawckz) 2016.  You may not copy, re-sell, distribute, modify, or use any code contained in this document or file, collection of documents or files, or project.  Thank you.
 */

package com.shawckz.mypractice.base.profile.reflex;


import com.shawckz.mypractice.base.database.mongo.annotations.CollectionName;
import com.shawckz.mypractice.base.database.mongo.annotations.MongoColumn;
import com.shawckz.mypractice.base.profile.cache.CachePlayer;
import lombok.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@AllArgsConstructor
@CollectionName(name = "practice_players")
@Getter
@Setter
public class PracticePlayer extends CachePlayer {

    @MongoColumn(name = "username")
    @NonNull
    private String name;

    @MongoColumn(name = "uuid", identifier = true)
    @NonNull
    private String uniqueId;

    // Non-persistent
    private Player bukkitPlayer = null;
    private boolean online = false;

    public PracticePlayer() { //So that AutoMongo can instantiate without throwing an InstantiationException

    }

    public void msg(String msg) {
        bukkitPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

}

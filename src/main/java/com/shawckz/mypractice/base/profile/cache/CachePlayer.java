/*
 * Copyright (c) Jonah Seguin (Shawckz) 2016.  You may not copy, re-sell, distribute, modify, or use any code contained in this document or file, collection of documents or files, or project.  Thank you.
 */

package com.shawckz.mypractice.base.profile.cache;


import com.shawckz.mypractice.base.database.mongo.AutoMongo;

/**
 * Created by Jonah on 6/11/2015.
 */
public abstract class CachePlayer extends AutoMongo {

    public abstract String getName();

    public abstract String getUniqueId();

}

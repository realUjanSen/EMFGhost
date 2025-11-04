package com.example.emfghost;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReadingDao {
    @Insert
    void insert(EMFReadingEntity reading);

    @Query("SELECT * FROM readings WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    List<EMFReadingEntity> getReadingsForSession(String sessionId);
}


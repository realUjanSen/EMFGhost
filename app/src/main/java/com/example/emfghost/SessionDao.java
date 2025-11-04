package com.example.emfghost;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SessionDao {
    @Insert
    void insert(EMFSessionEntity session);

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    List<EMFSessionEntity> getAllSessions();

    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId LIMIT 1")
    EMFSessionEntity getSessionById(String sessionId);

    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    void deleteSessionById(String sessionId);
}


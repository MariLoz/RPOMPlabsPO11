package com.example.rpomp_20_8;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insert(LocationPoint point);

    @Query("SELECT * FROM LocationPoint")
    List<LocationPoint> getAllPoints();
}

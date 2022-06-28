package com.example.sp20_bse_009_room_api;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE email LIKE :email")
    User findByEmail(String email);

    @Insert
    long insertOne(User user);

    @Update
    void updateOne(User user);

    @Delete
    void delete(User user);
}


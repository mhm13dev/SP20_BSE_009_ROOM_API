package com.example.sp20_bse_009_room_api;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1 )
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}

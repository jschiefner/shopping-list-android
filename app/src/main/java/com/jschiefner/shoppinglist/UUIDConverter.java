package com.jschiefner.shoppinglist;

import androidx.room.TypeConverter;

import java.util.UUID;

public class UUIDConverter {

    @TypeConverter
    public static UUID toUUID(String uuidString) {
        return uuidString == null ? null : UUID.fromString(uuidString);
    }

    @TypeConverter
    public static String fromUUID(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }
}

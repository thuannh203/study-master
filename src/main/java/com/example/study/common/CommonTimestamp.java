package com.example.study.common;

import java.sql.Timestamp;

public class CommonTimestamp {
    public static Long currentTimestamp(){
        return new Timestamp(System.currentTimeMillis())
                .toInstant().toEpochMilli();
    }
}

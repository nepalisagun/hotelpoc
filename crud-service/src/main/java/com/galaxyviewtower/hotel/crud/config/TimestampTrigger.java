package com.galaxyviewtower.hotel.crud.config;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampTrigger implements Trigger {
    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
        // No initialization needed
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        // Update the updated_at timestamp
        newRow[16] = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public void close() throws SQLException {
        // No cleanup needed
    }

    @Override
    public void remove() throws SQLException {
        // No cleanup needed
    }
} 
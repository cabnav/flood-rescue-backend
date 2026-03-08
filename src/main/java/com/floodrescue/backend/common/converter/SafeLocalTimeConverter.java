package com.floodrescue.backend.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * JPA converter for LocalTime that truncates to milliseconds when reading from DB.
 * Fixes PostgreSQL JDBC driver issue where time conversion can produce invalid
 * NanoOfSecond values causing DateTimeException.
 */
@Converter(autoApply = true)
public class SafeLocalTimeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime attribute) {
        return attribute == null ? null : Time.valueOf(attribute);
    }

    @Override
    public LocalTime convertToEntityAttribute(Time dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return dbData.toLocalTime().truncatedTo(ChronoUnit.MILLIS);
        } catch (Exception e) {
            return Instant.ofEpochMilli(dbData.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .truncatedTo(ChronoUnit.SECONDS);
        }
    }
}

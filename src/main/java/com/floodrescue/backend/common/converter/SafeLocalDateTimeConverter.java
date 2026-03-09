package com.floodrescue.backend.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * JPA converter for LocalDateTime that truncates to milliseconds when reading from DB.
 * Fixes PostgreSQL JDBC driver issue where timestamp conversion can produce invalid
 * NanoOfSecond values (e.g. -960000000) causing DateTimeException.
 * Uses getTime() (epoch millis) instead of toInstant() to avoid corrupt nanos.
 */
@Converter(autoApply = true)
public class SafeLocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute == null ? null : Timestamp.valueOf(attribute);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        if (dbData == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dbData.getTime()),
                ZoneId.systemDefault()
        ).truncatedTo(ChronoUnit.MILLIS);
    }
}

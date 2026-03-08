package com.floodrescue.backend.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

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
        // Build time from epoch millis to avoid corrupt nanos coming from some JDBC drivers
        return Instant.ofEpochMilli(dbData.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .truncatedTo(ChronoUnit.MILLIS);
    }
}

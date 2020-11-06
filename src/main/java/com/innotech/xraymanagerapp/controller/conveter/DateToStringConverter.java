/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.conveter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author Alexander Escobar L.
 */
@Converter(autoApply = true)
public class DateToStringConverter implements AttributeConverter<Date, String> {

    private final String format = "yyyy-MM-dd hh:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(OffsetDateTime.now().getOffset()));

    @Override
    public String convertToDatabaseColumn(Date attribute) {
        try {
            return formatter.format(attribute.toInstant());
        } catch (NullPointerException ex) {
            Logger.getLogger(DateToStringConverter.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    @Override
    public Date convertToEntityAttribute(String dbData) {
        try {
            if (Objects.nonNull(dbData)) {
                /* Used for dates stored on the database that have the same format as 
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME */
                if (dbData.contains("T")) {
                    LocalDateTime dateTime = LocalDateTime.parse(dbData, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    Date date = java.sql.Timestamp.valueOf(dateTime);
                    return date;
                }
                
                /* This is used by the old dates (before sqlite) */
                SimpleDateFormat fmt = new SimpleDateFormat(format, Locale.ENGLISH);
                return fmt.parse(dbData);
            }
        } catch (NullPointerException | ParseException ex) {
            Logger.getLogger(DateToStringConverter.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

}

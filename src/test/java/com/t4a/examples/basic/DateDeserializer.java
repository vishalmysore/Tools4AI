package com.t4a.examples.basic;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateDeserializer implements JsonDeserializer<Date> {
    private final DateFormat dateFormat;

    public DateDeserializer(String format) {
        this.dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return dateFormat.parse(json.getAsString().replaceAll("(st|nd|rd|th),", ","));
        } catch (ParseException e) {
            return new Date();
        }
    }
}

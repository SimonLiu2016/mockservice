package com.mockservice.template;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public enum TemplateConstants {

    SEQUENCE_INT("sequence:int", IntSequenceSupplier::new),
    RANDOM_INT("random:int", () -> TemplateConstants::randomInt),
    RANDOM_UUID("random:uuid", () -> TemplateConstants::randomUuid),
    RANDOM_STRING("random:string", () -> TemplateConstants::randomString),
    RANDOM_DATE("random:date", () -> TemplateConstants::randomDate),
    CURRENT_DATE("current:date", () -> TemplateConstants::currentDate),
    CURRENT_TIMESTAMP("current:timestamp", () -> TemplateConstants::currentTimestamp);

    private String name;
    private Supplier<Supplier<String>> supplier;

    TemplateConstants(String name, Supplier<Supplier<String>> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    public String getName() {
        return name;
    }

    public Supplier<String> getSupplier() {
        return supplier.get();
    }

    private static String randomInt() {
        return "" + ThreadLocalRandom.current().nextInt(1, 1000000);
    }

    private static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    private static String randomString() {
        return new Random().ints(97, 123)
                .limit(20)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomDate() {
        long startEpochDay = LocalDate.of(1983, 5, 20).toEpochDay();
        long endEpochDay = LocalDate.of(2083, 5, 20).toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay).toString();
    }

    public static String currentDate() {
        return LocalDate.now().toString();
    }

    public static String currentTimestamp() {
        return ZonedDateTime
                .now(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH-mm-ss"));
    }
}
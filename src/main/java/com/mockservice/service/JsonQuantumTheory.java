package com.mockservice.service;

import com.mockservice.util.JsonGenerator;
import com.mockservice.util.RandomUtils;
import com.mockservice.util.ValueGenerator;
import org.springframework.stereotype.Service;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JsonQuantumTheory implements QuantumTheory {

    private static final String REGEX_JSON_STRING_VALUE = "\"(\\w+)\"\\s*:\\s*\"((\\\\\"|[^\"])*)\"";
    private static final String REGEX_JSON_NUMERIC_VALUE = "\"(\\w+)\"\\s*:\\s*(-?[\\d\\.e]+)";
    private static final String REGEX_JSON_BOOLEAN_VALUE = "\"(\\w+)\"\\s*:\\s*(false|true)";

    public JsonQuantumTheory() {
        // default
    }

    @Override
    public String apply(String input) {
        if (RandomUtils.withChance(20)) {
            return JsonGenerator.generate();
        }
        if (RandomUtils.withChance(10)) {
            return "";
        }
        if (RandomUtils.withChance(40)) {
            return input;
        }

        return randomizeJsonValues(input); // 43,2% chance
    }

    private String randomizeJsonValues(String data) {
        Pattern pattern = Pattern.compile(REGEX_JSON_STRING_VALUE);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            data = matcher.replaceAll(this::stringReplacer);
        }

        pattern = Pattern.compile(REGEX_JSON_NUMERIC_VALUE);
        matcher = pattern.matcher(data);
        if (matcher.find()) {
            data = matcher.replaceAll(this::numberReplacer);
        }

        pattern = Pattern.compile(REGEX_JSON_BOOLEAN_VALUE);
        matcher = pattern.matcher(data);
        if (matcher.find()) {
            data = matcher.replaceAll(this::booleanReplacer);
        }

        return data;
    }

    private String stringReplacer(MatchResult matchResult) {
        String name = matchResult.groupCount() > 0 ? matchResult.group(1) : ValueGenerator.randomWords(1);
        if (RandomUtils.withChance(10)) {
            return "\"" + name + "\": null";
        }
        return "\"" + name + "\": \"" + ValueGenerator.randomString() + "\"";
    }

    private String numberReplacer(MatchResult matchResult) {
        String name = matchResult.groupCount() > 0 ? matchResult.group(1) : ValueGenerator.randomWords(1);
        if (RandomUtils.withChance(10)) {
            return "\"" + name + "\": null";
        }
        return "\"" + name + "\": " + ValueGenerator.randomNumberString();
    }

    private String booleanReplacer(MatchResult matchResult) {
        String name = matchResult.groupCount() > 0 ? matchResult.group(1) : ValueGenerator.randomWords(1);
        return "\"" + name + "\": " + ValueGenerator.randomBooleanString();
    }
}

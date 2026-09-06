package ru.rgrabelnikov.fbbackend.util;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ValidatorConstants {

    public static final String UUID_PATTERN = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b";

    public static final String MAX_NUMBER_VALUE = "9007199254740991";
    public static final String MIN_NUMBER_VALUE = "-9007199254740991";
    public static final String MAX_INTEGER_VALUE = "2147483647";
    public static final String MIN_INTEGER_VALUE = "-2147483648";

    public static final int MAX_LENGTH_UUID = 36;
    public static final int MAX_LENGTH_ARRAY = 1000;
    public static final int MAX_LENGTH_DATE = 10;
    public static final int MAX_LENGTH_255 = 255;
    public static final int MAX_LENGTH_50 = 50;
}

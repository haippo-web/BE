package com.attendance.util;

public class EnumConverter {

    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
        if (value == null) return null;
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown enum value: " + value);
        }
    }

    public static <E extends Enum<E>> String toString(E enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }
}

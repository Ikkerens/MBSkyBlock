package com.ikkerens.skyblock.util;

public class MathUtils {
    public static int modulo( final int number, final int mod ) {
        final int m = number % mod;
        return m >= 0 ? m : ( mod + m );
    }
}

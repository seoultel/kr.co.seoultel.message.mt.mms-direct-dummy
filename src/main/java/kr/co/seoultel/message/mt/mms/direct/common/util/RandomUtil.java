package kr.co.seoultel.message.mt.mms.direct.common.util;

import java.util.Random;

public class RandomUtil {
    private static final Random random = new Random();

    public static String getRandomNum(int start, int end) {
        return String.valueOf(random.nextInt(end) + start);
    }

    public static String getRandomStr(String... strings) {
        return strings[Integer.parseInt(getRandomNum(0, strings.length))];
    }

    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        } else {
            Random random = new Random();
            return random.nextInt(max - min + 1) + min;
        }
    }

    public static long getRandomNumberInRange(long min, long max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        } else {
            Random random = new Random();
            return random.nextLong() % (max - min) + min;
        }
    }

    public static String getRandomNumberString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for(int i = 0; i < length; ++i) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }

    public static int sum(int... numbers) {
        int sum = 0;

        for(int number : numbers) {
            sum += number;
        }

        return sum;
    }
}

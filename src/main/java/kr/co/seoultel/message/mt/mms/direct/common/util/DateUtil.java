package kr.co.seoultel.message.mt.mms.direct.common.util;

import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DateUtil {

    private static final LocalTime startTime = LocalTime.of(7,59,59);
    private static final LocalTime endTime = LocalTime.of(21,00,00);

    // 오전 8시부터 오후 9시까지 전송 가능
    public static boolean checkNowIsTransmissiveTime(LocalTime current) {
        return current.isAfter(startTime) && current.isBefore(endTime);
    }

    private static final SimpleDateFormat dttmSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final DateTimeFormatter mcmpLocalDateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter fullLocalDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String getDate() {
        LocalDateTime future = LocalDateTime.now();
        return future.format(mcmpLocalDateFormatter);
    }


    public static String getDate(int plusSecond) {
        LocalDateTime future = LocalDateTime.now().plusSeconds(plusSecond);
        return future.format(mcmpLocalDateFormatter);
    }

    public static String getDate(String format) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return now.format(formatter);
    }

    public static String getDate(int plusSeconds, String format) {
        LocalDateTime future = LocalDateTime.now().plusSeconds((long) plusSeconds);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return future.format(formatter);
    }


    /*
    * String getDate(int plusSeconds, String format) 메서드 호출 시
    * java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: OffsetSeconds 예외 발생하는 경우
    * format 에 "Z" 가 포함된 경우 사용
    * */
    public static String getZoneDateTime(int plusSeconds, String format) {
        ZonedDateTime future = ZonedDateTime.now().plusSeconds((long) plusSeconds);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
        return future.format(formatter);
    }

    public static String parseFullLocalDateToMcmpLocalDateFormat(String localDateTimeString) {
        LocalDateTime fullLocalDateTime = LocalDateTime.parse(localDateTimeString, fullLocalDateFormatter);
        return fullLocalDateTime.format(mcmpLocalDateFormatter);
    }

    public static String parseLocalDateToString(LocalDateTime localDateTime) {
        return localDateTime.format(mcmpLocalDateFormatter);
    }

    public static Long parseLocalDateToLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getCurrentTimeInMillis() {
        return Instant.now().toEpochMilli();
    }

    public static long getTimeGapUntilNextSecond() {

        long currentTime = getCurrentTimeInMillis();

        long nextSecondTime = (currentTime / 1000 + 1) * 1000;

        return nextSecondTime - currentTime;
    }

    public static boolean checkNowIsTransmissiveTime() {
        LocalTime current = LocalTime.now();
        return current.isAfter(startTime) && current.isBefore(endTime);
    }

    public static boolean isOverDue(long compareTime, long dayDiff) {
        Date lastModifiedDate = new Date(compareTime);

        // 현재 날짜와 비교
        Date currentDate = new Date();

        // 밀리초 단위로 차이 계산
        long diffInMillies = currentDate.getTime() - lastModifiedDate.getTime();

        // 밀리초를 일 단위로 변환
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        return diffInDays >= dayDiff;
    }

    public static long getTimeStampByDttm(String dttm) throws ParseException {
        Date date = dttmSimpleDateFormat.parse(dttm);
        return date.getTime();
    }
}

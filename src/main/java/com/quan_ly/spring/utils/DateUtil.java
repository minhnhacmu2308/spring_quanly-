package com.quan_ly.spring.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String getDateNow(){
        LocalDate dateNow = LocalDate.now();

        // Định dạng ngày
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Chuyển đổi ngày thành chuỗi
        String dateNowString = dateNow.format(formatter);

        return dateNowString;

    }
}

package com.quan_ly.spring.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringUtil {
    public static String extractPublicIdFromUrl(String url) {
        try {
            // Cắt theo "/upload/"
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String afterUpload = parts[1];
                // Cắt bỏ đuôi .pdf, .jpg, .png,...
                int dotIndex = afterUpload.lastIndexOf('.');
                if (dotIndex > 0) {
                    return afterUpload.substring(0, dotIndex);
                } else {
                    return afterUpload; // Không có phần mở rộng
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

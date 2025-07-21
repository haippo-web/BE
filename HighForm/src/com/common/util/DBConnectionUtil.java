package com.common.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DBConnectionUtil {
   private static final Properties messages = new Properties();

   static {
      try (InputStream in = ClassLoader.getSystemResourceAsStream("com/common/util/db.properties");
              InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
             messages.load(reader);
      } catch (Exception e) {
         System.err.println("Error loading error messages: " + e.getMessage());
      }
   }

   public static String get(String key) {
      return messages.getProperty(key, key + "를 찾을 수 없습니다.");
   }
}
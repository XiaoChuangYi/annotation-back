package com.malgo.utils;

import java.util.regex.Pattern;

/**
 * Created by cjl on 2018/6/2.
 */
public class RegexUtil {

  private static final String REGEX = "^.*[(/) | (\\\\) | (:) | (\\*) | (\\?) | (\") | (<) | (>)].*$";

  public static boolean haveSpecialCharacter(String str) {
    return Pattern.matches(str, REGEX);
  }
}

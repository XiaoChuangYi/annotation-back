package cn.malgo.annotation.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class FileUtil {

  public static List<Pair<String, String>> getDirectoryContent(
      String directory, boolean isClassPath) {
    File file = null;
    if (isClassPath) {
      final Resource resource = new ClassPathResource(directory);
      try {
        file = resource.getFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      file = new File(directory);
    }
    File[] files = file.listFiles();
    if (files.length > 0) {
      final List<Pair<String, String>> pairs = new LinkedList<>();
      for (File current : files) {
        try {
          pairs.add(Pair.of(current.getName(), FileUtils.readFileToString(current)));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return pairs;
    }
    return Collections.emptyList();
  }

  public static String readClassPathFile(String path) {
    final Resource resource = new ClassPathResource(path);
    String input = "";
    try {
      final File file = resource.getFile();
      input = FileUtils.readFileToString(file);
      return input;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return input;
  }

  public static void writeFile(String path, String output) {
    final File file = new File(path);
    if (!file.exists()) {
      try {
        file.createNewFile();
        FileUtils.write(file, output);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

package cn.malgo.annotation.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
public class FileUtil {

  public static List<Pair<String, String>> getContent(String directory, boolean isClassPath)
      throws IOException {
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
        if (current.isDirectory()) {
          File deepFile = current.listFiles()[0];
          final String content = FileUtils.readFileToString(deepFile);
          pairs.add(
              Pair.of(
                  deepFile.getName(), FileUtils.readFileToString(deepFile, getEncoding(content))));
        }
        if (current.isFile()) {
          pairs.add(Pair.of(current.getName(), FileUtils.readFileToString(current)));
        }
      }
      return pairs;
    }
    return Collections.emptyList();
  }

  private static String getEncoding(String str) {
    String gEncode = "GB2312";
    String uEncode = "UTF-8";
    return Charset.forName(gEncode).newEncoder().canEncode(str) ? uEncode : gEncode;
  }

  //  public static List<Pair<String, String>> getDirectoryContent(
  //      String directory, boolean isClassPath) {
  //    File file = null;
  //    if (isClassPath) {
  //      final Resource resource = new ClassPathResource(directory);
  //      try {
  //        file = resource.getFile();
  //      } catch (IOException e) {
  //        e.printStackTrace();
  //      }
  //    } else {
  //      file = new File(directory);
  //    }
  //    File[] files = file.listFiles();
  //    if (files.length > 0) {
  //      final List<Pair<String, String>> pairs = new LinkedList<>();
  //      for (File current : files) {
  //        try {
  //          pairs.add(Pair.of(current.getName(), FileUtils.readFileToString(current)));
  //        } catch (IOException e) {
  //          e.printStackTrace();
  //        }
  //      }
  //      return pairs;
  //    }
  //    return Collections.emptyList();
  //  }

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

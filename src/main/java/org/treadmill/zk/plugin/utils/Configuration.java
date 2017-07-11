package org.treadmill.zk.plugin.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;

import static java.lang.System.getProperty;
import static java.text.MessageFormat.format;

public class Configuration {
  static PropertyResourceBundle bundle;


  static void loadBundle() throws IOException {
    String propFile = getProperty("org.treadmill.zk.plugin.configuration");
    if (propFile == null) {
      throw new IllegalArgumentException("missing system property org.treadmill.zk.plugin.configuration");
    }
    bundle = new PropertyResourceBundle(new FileInputStream(propFile));
  }

  public static String get(String key, Object... args) throws IOException {
    if (bundle == null) loadBundle();

    return format(bundle.getString(key), args);
  }
}

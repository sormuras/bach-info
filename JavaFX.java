/*
 * Copyright (c) 2024 Christian Stein
 * Licensed under the Universal Permissive License v 1.0 -> https://opensource.org/license/upl
 */

package run.info.bach;

import java.util.Set;
import run.bach.ModuleLocator;
import run.bach.info.MavenCoordinate;
import run.bach.info.OperatingSystem;

/**
 * JavaFX is a client application platform.
 *
 * @see <a href="https://openjfx.io">Homepage</a>
 */
public record JavaFX(String version, String classifier) implements ModuleLocator {
  public static JavaFX version(String version) {
    return version(version, OperatingSystem.CURRENT);
  }

  public static JavaFX version(String version, OperatingSystem os) {
    return new JavaFX(version, computeClassifier(os));
  }

  /**
   * @see <a href="https://repo.maven.apache.org/maven2/org/openjfx/">org.openjfx</a>
   */
  @Override
  public Set<String> names() {
    return Set.of(
        "javafx.base",
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.media",
        "javafx.swing",
        "javafx.web");
  }

  @Override
  public Location locate(String module) {
    if (!names().contains(module)) return Location.unknown(module);
    var group = "org.openjfx";
    var artifact = module.replace('.', '-');
    var coordinate = MavenCoordinate.ofCentral(group, artifact, version, classifier);
    return Location.of(module, coordinate.toUri().toString());
  }

  public static String computeClassifier(OperatingSystem os) {
    var name = os.name();
    var architecture = os.architecture();
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    var classifier =
        switch (name) {
          case ANY -> throw new UnsupportedOperationException();
          case LINUX ->
              switch (architecture) {
                case ARM_64 -> "linux-aarch64";
                default -> "linux";
              };
          case MAC ->
              switch (architecture) {
                case ARM_64 -> "mac-aarch64";
                default -> "mac";
              };
          case WINDOWS ->
              switch (architecture) {
                case X86_32 -> "win-x86";
                default -> "win";
              };
        };
    return classifier;
  }
}

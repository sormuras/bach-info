package generator.javafx;

import generator.Artifact;
import generator.Classifier;
import generator.Generator;
import generator.Maven;
import java.util.List;

public class GenerateJavaFX {
  private static final String MAVEN_GROUP = "org.openjfx";

  public static void main(String... args) throws Exception {
    var version = args.length == 0 ? "20.0.2" : args[0];
    var generator = Generator.of("javafx", "javafx@" + version);

    var lines =
        generator.lines(
            "@description JavaFX is a client application platform",
            "@home https://openjfx.io",
            "@version " + version,
            "");

    var artifacts =
        List.of(
            new Artifact("javafx-base"),
            new Artifact("javafx-controls"),
            new Artifact("javafx-fxml"),
            new Artifact("javafx-graphics"),
            new Artifact("javafx-media"),
            new Artifact("javafx-swing"),
            new Artifact("javafx-web"));

    var classifiers =
        List.of(
            // linux
            new Classifier("linux-x86_64", "linux"),
            new Classifier("linux-arm_64", "linux-aarch64"),
            // macos
            new Classifier("mac-x86_64", "mac"),
            new Classifier("mac-arm_64", "mac-aarch64"),
            // windows
            new Classifier("windows-x86_32", "win-x86"),
            new Classifier("windows-x86_64", "win"));

    for (var artifact : artifacts) {
      lines.add("");
      lines.add("# " + artifact.identifier());
      for (var classifier : classifiers) {
        var suffix = '|' + classifier.normalized();
        var uri =
            Maven.central(MAVEN_GROUP, artifact.identifier(), version, classifier.identifier());
        generator.generate(suffix, uri).ifPresent(lines::add);
      }
    }

    generator.write(lines);
  }
}

package generator.gluon;

import generator.Generator;
import generator.Maven;
import java.util.List;

// TODO Include classifiers, such as: android, desktop, ios
//  https://repo.maven.apache.org/maven2/com/gluonhq/attach/
public class GenerateGluonAttach {
  public static final String MAVEN_GROUP = "com.gluonhq.attach";

  public static void main(String... args) throws Exception {
    var version = args.length == 0 ? "4.0.14" : args[0];
    var generator = Generator.of("gluon", "gluon-attach@" + version);

    var lines =
        generator.lines(
            "@description Device & Platform Agnostic Native Hardware Services",
            "@home https://gluonhq.com/products/mobile/attach",
            "@version " + version,
            "");

    var uris =
        List.of(
            Maven.central(MAVEN_GROUP, "accelerometer", version),
            Maven.central(MAVEN_GROUP, "audio", version),
            Maven.central(MAVEN_GROUP, "audio-recording", version),
            Maven.central(MAVEN_GROUP, "augmented-reality", version),
            Maven.central(MAVEN_GROUP, "barcode-scan", version),
            Maven.central(MAVEN_GROUP, "battery", version),
            Maven.central(MAVEN_GROUP, "ble", version),
            Maven.central(MAVEN_GROUP, "browser", version),
            Maven.central(MAVEN_GROUP, "cache", version),
            Maven.central(MAVEN_GROUP, "compass", version),
            Maven.central(MAVEN_GROUP, "device", version),
            Maven.central(MAVEN_GROUP, "dialer", version),
            Maven.central(MAVEN_GROUP, "in-app-billing", version),
            Maven.central(MAVEN_GROUP, "keyboard", version),
            Maven.central(MAVEN_GROUP, "lifecycle", version),
            Maven.central(MAVEN_GROUP, "local-notifications", version),
            Maven.central(MAVEN_GROUP, "magnetometer", version),
            Maven.central(MAVEN_GROUP, "orientation", version),
            Maven.central(MAVEN_GROUP, "pictures", version),
            Maven.central(MAVEN_GROUP, "position", version),
            Maven.central(MAVEN_GROUP, "push-notifications", version),
            Maven.central(MAVEN_GROUP, "runtime-args", version),
            Maven.central(MAVEN_GROUP, "settings", version),
            Maven.central(MAVEN_GROUP, "share", version),
            Maven.central(MAVEN_GROUP, "statusbar", version),
            Maven.central(MAVEN_GROUP, "storage", version),
            Maven.central(MAVEN_GROUP, "util", version),
            Maven.central(MAVEN_GROUP, "version", version),
            Maven.central(MAVEN_GROUP, "vibration", version),
            Maven.central(MAVEN_GROUP, "video", version));

    for (var uri : uris) generator.generate("", uri).ifPresent(lines::add);

    generator.write(lines);
  }
}

package generator.fxgl;

import generator.Generator;
import generator.Maven;
import java.util.List;

public class GenerateFXGL {
  public static final String MAVEN_GROUP = "com.github.almasb";

  public static void main(String... args) throws Exception {
    var version = args.length == 0 ? "17" : args[0];
    var generator = Generator.of("fxgl", "fxgl@" + version);

    var lines =
        generator.lines(
            "@description Java / JavaFX / Kotlin Game Library (Engine)",
            "@home https://almasb.github.io/FXGL",
            "@version " + version,
            "");

    var uris =
        List.of(
            Maven.central(MAVEN_GROUP, "fxgl", version),
            Maven.central(MAVEN_GROUP, "fxgl-core", version),
            Maven.central(MAVEN_GROUP, "fxgl-controllerinput", version),
            Maven.central(MAVEN_GROUP, "fxgl-entity", version),
            Maven.central(MAVEN_GROUP, "fxgl-gameplay", version),
            Maven.central(MAVEN_GROUP, "fxgl-io", version),
            // Maven.central(group, "fxgl-samples", version),
            Maven.central(MAVEN_GROUP, "fxgl-scene", version)
            // Maven.central(group, "fxgl-test", version),
            // Maven.central(group, "fxgl-tools", version),
            // Maven.central(group, "fxgl-zdeploy", version)
            );

    for (var uri : uris) generator.generate("", uri).ifPresent(lines::add);

    generator.write(lines);
  }
}

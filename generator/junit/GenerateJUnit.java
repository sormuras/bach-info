package generator.junit;

import generator.Generator;
import generator.Maven;
import java.util.List;

public class GenerateJUnit {
  public static void main(String... args) throws Exception {
    if (args.length == 0) throw new Error("Usage: GenerateJUnit VERSION");
    var version = args[0];
    var generator = Generator.of("junit", "junit@" + version);

    var lines =
        generator.lines(
            "@description The testing framework for Java and the JVM",
            "@home https://junit.org",
            "@version " + version,
            "");

    @SuppressWarnings("UnnecessaryLocalVariable")
    var jupiterVersion = version;
    @SuppressWarnings("UnnecessaryLocalVariable")
    var vintageVersion = version;
    var platformVersion = "1" + jupiterVersion.substring(1); // "5.y.z" -> "1.y.z"

    var uris =
        List.of(
            // API Guardian
            Maven.central("org.apiguardian", "apiguardian-api", "1.1.2"),
            // Jupiter
            Maven.central("org.junit.jupiter", "junit-jupiter", jupiterVersion),
            Maven.central("org.junit.jupiter", "junit-jupiter-api", jupiterVersion),
            Maven.central("org.junit.jupiter", "junit-jupiter-engine", jupiterVersion),
            Maven.central("org.junit.jupiter", "junit-jupiter-migrationsupport", jupiterVersion),
            Maven.central("org.junit.jupiter", "junit-jupiter-params", jupiterVersion),
            // Platform
            Maven.central("org.junit.platform", "junit-platform-commons", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-console", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-engine", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-jfr", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-launcher", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-reporting", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-suite", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-suite-api", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-suite-commons", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-suite-engine", platformVersion),
            Maven.central("org.junit.platform", "junit-platform-testkit", platformVersion),
            // Vintage
            Maven.central("org.junit.vintage", "junit-vintage-engine", vintageVersion),
            // OpenTest4J
            Maven.central("org.opentest4j", "opentest4j", "1.2.0"));

    for (var uri : uris) generator.generate("", uri).ifPresent(lines::add);

    generator.write(lines);
  }
}

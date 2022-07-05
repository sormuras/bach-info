package generator.lwjgl;

import generator.Artifact;
import generator.Classifier;
import generator.Generator;
import generator.Maven;
import java.util.List;

public class GenerateLWJGL {
  private static final String MAVEN_GROUP = "org.lwjgl";

  public static void main(String... args) throws Exception {
    var version = args.length == 0 ? "3.3.1" : args[0];
    var generator = Generator.of("lwjgl", "lwjgl@" + version);

    var lines =
        generator.lines(
            "@description Lightweight Java Game Library",
            "@home https://lwjgl.org",
            "@version " + version,
            "");

    var artifacts =
        List.of(
            new Artifact("lwjgl"),
            new Artifact("lwjgl-assimp"),
            new Artifact("lwjgl-bgfx"),
            new Artifact("lwjgl-cuda"),
            new Artifact("lwjgl-egl"),
            new Artifact("lwjgl-glfw"),
            new Artifact("lwjgl-jawt"),
            new Artifact("lwjgl-jemalloc"),
            new Artifact("lwjgl-libdivide"),
            new Artifact("lwjgl-llvm"),
            new Artifact("lwjgl-lmdb"),
            new Artifact("lwjgl-lz4"),
            new Artifact("lwjgl-meow"),
            new Artifact("lwjgl-meshoptimizer"),
            new Artifact("lwjgl-nanovg"),
            new Artifact("lwjgl-nfd"),
            new Artifact("lwjgl-nuklear"),
            new Artifact("lwjgl-odbc"),
            new Artifact("lwjgl-openal"),
            new Artifact("lwjgl-opencl"),
            new Artifact("lwjgl-opengl"),
            new Artifact("lwjgl-opengles"),
            new Artifact("lwjgl-openvr"),
            new Artifact("lwjgl-openxr"),
            new Artifact("lwjgl-opus"),
            new Artifact("lwjgl-ovr"),
            new Artifact("lwjgl-par"),
            new Artifact("lwjgl-remotery"),
            new Artifact("lwjgl-rpmalloc"),
            new Artifact("lwjgl-shaderc"),
            new Artifact("lwjgl-spvc"),
            new Artifact("lwjgl-sse"),
            new Artifact("lwjgl-stb"),
            new Artifact("lwjgl-tinyexr"),
            new Artifact("lwjgl-tinyfd"),
            new Artifact("lwjgl-tootle"),
            new Artifact("lwjgl-vma"),
            new Artifact("lwjgl-vulkan"),
            new Artifact("lwjgl-xxhash"),
            new Artifact("lwjgl-yoga"),
            new Artifact("lwjgl-zstd"));

    var classifiers =
        List.of(
            // linux
            new Classifier("linux-x86_64", "natives-linux"),
            new Classifier("linux-arm_32", "natives-linux-arm32"),
            new Classifier("linux-arm_64", "natives-linux-arm64"),
            // macos
            new Classifier("mac-x86_64", "natives-macos"),
            new Classifier("mac-arm_64", "natives-macos-arm64"),
            // windows
            new Classifier("windows-x86_32", "natives-windows-x86"),
            new Classifier("windows-x86_64", "natives-windows"),
            new Classifier("windows-arm_64", "natives-windows-arm64"));

    for (var artifact : artifacts) {
      lines.add("");
      lines.add("#");
      lines.add("# " + "org." + artifact.identifier().replace('-', '.'));
      lines.add("#");
      generator
          .generate("", Maven.central(MAVEN_GROUP, artifact.identifier(), version))
          .map(lines::add)
          .orElseThrow();
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

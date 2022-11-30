import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Static helpers setting up new and managing existing installations of Bach. */
@SuppressWarnings("unused")
public interface bach {

  String DEFAULT_HOME = ".bach";
  String DEFAULT_VERSION = "main";

  static void main(String... args) {
    util.say("main");
  }

  static void info() {
    util.say("info");
  }

  interface install {
    static void bach(String version) {
      util.say("install bach " + version);
    }

    interface external {
      static void tool(String name) {
        System.out.println("install external tool");
      }

      static void modules(String... names) {
        util.say("install external modules " + String.join(" ", names));
      }
    }
  }

  interface util {

    interface files {
      static Path copy(String source, String target) throws Exception {
        return copy(source, Path.of(target), StandardCopyOption.REPLACE_EXISTING);
      }

      static Path copy(String source, Path target, CopyOption... options) throws Exception {
        say("<< %s".formatted(source));
        Files.createDirectories(target.getParent());
        try (var stream =
                     source.startsWith("http")
                             ? URI.create(source).toURL().openStream()
                             : Files.newInputStream(Path.of(source))) {
          var size = Files.copy(stream, target, options);
          say(">> %,7d %s".formatted(size, target.getFileName()));
        }
        return target;
      }

      static void delete(Path start) throws Exception {
        if (Files.notExists(start)) return;
        say("delete directory tree " + start);
        var roots =
                StreamSupport.stream(start.getFileSystem().getRootDirectories().spliterator(), false)
                        .toList();
        if (roots.contains(start.normalize())) {
          System.err.println("won't remove root directory: " + start);
          return;
        }
        try (var stream = Files.walk(start)) {
          var files = stream.sorted((p, q) -> -p.compareTo(q));
          for (var file : files.toArray(Path[]::new)) Files.deleteIfExists(file);
        }
      }
    }

    interface tool {
      static void run(String name, Object... args) {
        var tool = ToolProvider.findFirst(name).orElseThrow();
        var strings = Stream.of(args).map(Object::toString).toList();
        say(name + " " + String.join(" ", strings));
        var code = tool.run(System.out, System.err, strings.toArray(String[]::new));
        if (code != 0) throw new RuntimeException(name + " returned error code " + code);
      }
    }

    static void say(String line) {
      System.out.println(line);
    }
  }
}

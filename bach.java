import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Static helpers setting up new and managing existing installations of Bach. */
@SuppressWarnings("unused")
public interface bach {

  static void main(String... args) throws Exception {
    init.bach(args.length == 0 ? init.DEFAULT_VERSION : args[0]);
  }

  interface init {
    String DEFAULT_HOME = ".bach";
    String DEFAULT_VERSION = "main";

    static void bach() throws Exception {
      bach(DEFAULT_VERSION);
    }

    static void bach(String version) throws Exception {
      var home = Path.of(System.getProperty("bach.home", DEFAULT_HOME));
      bach(version, home);
    }

    static void bach(String version, Path home) throws Exception {
      util.say("Initialize Bach version %s in %s...".formatted(version, home.toUri()));
      // download sources to temporary directory
      var src = "https://github.com/sormuras/bach/archive/" + version + ".zip";
      var tmp = Files.createTempDirectory("bach-" + version + "-");
      var zip = tmp.resolve("bach-archive-" + version + ".zip");
      util.files.copy(src, zip, StandardCopyOption.REPLACE_EXISTING);
      // unzip and mark bash script as executable
      var from = tmp.resolve("bach-archive-" + version); // unzipped
      util.files.unzip(zip, from);
      //noinspection ResultOfMethodCallIgnored
      from.resolve("bin/bach").toFile().setExecutable(true, true);
      // build bach
      util.shell.bach(from, "build");
      // refresh binary directory
      util.files.delete(home.resolve("bin"));
      Files.createDirectories(home.resolve("bin"));
      Files.copy(from.resolve("bin/bach"), home.resolve("bin/bach"));
      Files.copy(from.resolve("bin/bach.bat"), home.resolve("bin/bach.bat"));
      Files.copy(from.resolve(".bach/out/modules/run.bach.jar"), home.resolve("bin/run.bach.jar"));
      util.files.delete(tmp);
      util.say("Initialized Bach %s".formatted(version));
    }
  }

  interface util {

    interface files {
      static void copy(String source, Path target, CopyOption... options) throws Exception {
        say("<< %s".formatted(source));
        Files.createDirectories(target.getParent());
        try (var stream =
            source.startsWith("http")
                ? URI.create(source).toURL().openStream()
                : Files.newInputStream(Path.of(source))) {
          var size = Files.copy(stream, target, options);
          say(">> %,7d %s".formatted(size, target.getFileName()));
        }
      }

      static void delete(Path path) throws Exception {
        var start = path.normalize().toAbsolutePath();
        if (Files.notExists(start)) return;
        for (var root : start.getFileSystem().getRootDirectories()) {
          if (start.equals(root)) {
            System.err.println("deletion of root directory?! " + path);
            return;
          }
        }
        say("delete directory tree " + start);
        try (var stream = Files.walk(start)) {
          var files = stream.sorted((p, q) -> -p.compareTo(q));
          for (var file : files.toArray(Path[]::new)) Files.deleteIfExists(file);
        }
      }

      static void unzip(Path zip, Path dir) throws Exception {
        say("<< %s".formatted(zip.toUri()));
        var files = new ArrayList<Path>();
        try (var fs = FileSystems.newFileSystem(zip)) {
          for (var root : fs.getRootDirectories()) {
            try (var stream = Files.walk(root)) {
              var list = stream.filter(Files::isRegularFile).toList();
              for (var file : list) {
                  var target = dir.resolve(file.subpath(1, file.getNameCount()).toString());
                  // verbose(target.toUri().toString());
                  Files.createDirectories(target.getParent());
                  Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                  files.add(target);

              }
            }
          }
        }
        say(">> %d files copied".formatted(files.size()));
      }
    }

    interface shell {
      static void bach(Path directory, String... arguments) throws Exception {
        var builder = new ProcessBuilder();
        builder.environment().put("JAVA_HOME", System.getProperty("java.home"));
        builder.directory(directory.toFile());
        var command = builder.command();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
          command.add("cmd.exe");
          command.add("/c");
          command.add("bin\\bach.bat");
        } else {
          command.add("bin/bach");
        }
        command.addAll(List.of(arguments));
        say("%s -> %s".formatted(builder.directory(), builder.command()));
        record LinePrinter(InputStream stream, Consumer<String> consumer) implements Runnable {
          @Override
          public void run() {
            new BufferedReader(new InputStreamReader(stream)).lines().forEach(consumer);
          }
        }
        var process = builder.start();
        new Thread(new LinePrinter(process.getInputStream(), System.out::println)).start();
        new Thread(new LinePrinter(process.getErrorStream(), System.err::println)).start();
        int code = process.waitFor();
        if (code != 0) throw new RuntimeException("Non-zero exit code " + code);
      }
    }

    static void say(String line) {
      System.out.println(line);
    }
  }
}

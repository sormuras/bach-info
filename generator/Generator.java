package generator;

import generator.fxgl.GenerateFXGL;
import generator.gluon.GenerateGluonAttach;
import generator.javafx.GenerateJavaFX;
import generator.junit.GenerateJUnit;
import generator.lwjgl.GenerateLWJGL;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

public record Generator(Path cache, HttpClient http) {

  public static void main(String... args) throws Exception {
    GenerateFXGL.main("17");
    GenerateFXGL.main("17.1");
    GenerateFXGL.main("17.2");
    GenerateGluonAttach.main("4.0.14");
    GenerateJavaFX.main("18");
    GenerateJavaFX.main("18.0.1");
    GenerateJUnit.main("5.8.0");
    GenerateJUnit.main("5.8.1");
    GenerateJUnit.main("5.8.2");
    GenerateJUnit.main("5.9.0-M1");
    GenerateJUnit.main("5.9.0-RC1");
    GenerateJUnit.main("5.9.0");
    GenerateJUnit.main("5.9.1");
    GenerateLWJGL.main("3.3.1");
  }

  public static Generator of(String... cache) {
    return new Generator(Path.of("generator-cache", cache), HttpClient.newHttpClient());
  }

  public Generator {
    System.out.println(cache + " -> " + cache.toUri());
  }

  public List<String> lines(String... initial) {
    return new ArrayList<>(List.of(initial));
  }

  public Optional<String> generate(String suffix, URI uri) throws Exception {
    var jar = HexFormat.of().toHexDigits(uri.hashCode()) + ".jar";
    var directory = Files.createDirectories(cache);
    var file = directory.resolve(jar);
    if (Files.notExists(file)) {
      var request = HttpRequest.newBuilder(uri).GET().build();
      System.out.println(request);
      var response = http.send(request, HttpResponse.BodyHandlers.ofFile(file));
      if (response.statusCode() != 200) {
        Files.deleteIfExists(file);
        Files.createFile(file);
        return Optional.empty();
      }
    }
    var size = Files.size(file);
    if (size == 0) return Optional.empty();
    var finder = ModuleFinder.of(file);
    var names =
        finder.findAll().stream()
            .map(ModuleReference::descriptor)
            .map(ModuleDescriptor::name)
            .toList();
    var key = "%s%s".formatted(String.join(",", names), suffix);
    var value = "%s#SIZE=%d".formatted(uri, size);
    return Optional.of(key + '=' + value);
  }

  public void write(List<String> lines) throws Exception {
    var directory = Path.of(".bach", "external-modules");
    var filename = cache.getFileName().toString();
    Files.createDirectories(directory);
    var file = directory.resolve(filename + ".properties");
    Files.write(file, lines);
    var modules =
        lines.stream()
            .filter(line -> !line.isBlank())
            .filter(line -> !line.startsWith("#"))
            .filter(line -> !line.startsWith("@"))
            .count();
    System.out.printf("%4d modules in %s%n", modules, file.toUri());
  }
}

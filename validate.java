import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class validate {

  public static void main(String... args) throws Exception {
    var validate = new validate();
    var paths = args.length == 0 ? findProperties() : Stream.of(args).map(Path::of).toList();
    for (var path : paths) validate.validateProperties(path);
  }

  static List<Path> findProperties() throws Exception {
    var syntaxAndPattern = "glob:.bach/**.properties";
    System.out.println(syntaxAndPattern);
    var directory = Path.of("");
    var matcher = directory.getFileSystem().getPathMatcher(syntaxAndPattern);
    try (var stream = Files.find(directory, 9, (path, attributes) -> matcher.matches(path))) {
      return stream.toList();
    }
  }

  final HttpClient http = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();

  void validateProperties(Path path) throws Exception {
    if (Files.notExists(path)) throw new IllegalArgumentException("no such file: " + path);
    System.out.println();
    System.out.println(path);
    var properties = new Properties();
    properties.load(Files.newBufferedReader(path));
    for (var key : new TreeSet<>(properties.stringPropertyNames())) {
      var value = properties.getProperty(key);
      validateProperty(key, value);
    }
  }

  private void validateProperty(String key, String value) throws Exception {
    if (key.startsWith("@") || value.startsWith("string:")) {
      var line = value.replace('\n', '\\').replace('\r', '\\');
      var snippet = line.length() < 80 ? line : line.substring(0, 80) + "[...]";
      System.out.printf("%9d %s %s%n", value.length(), key, snippet);
      return;
    }
    if (value.startsWith("http")) {
      var uri = URI.create(value);
      var request = HttpRequest.newBuilder(uri).method("HEAD", BodyPublishers.noBody()).build();
      var response = http.send(request, BodyHandlers.discarding());
      if (response.statusCode() == 200) {
        var size = response.headers().firstValueAsLong("Content-Length").orElse(-1);
        System.out.printf("%,9d %s %s%n", size, key, uri);
      } else {
        response
            .headers()
            .map()
            .forEach((header, entry) -> System.out.printf("%s -> %s%n", header, entry));
      }
      return;
    }
    var maven = Pattern.compile("(.+):(.+):(.+)");
    var matcher = maven.matcher(value);
    if (matcher.matches()) {
      var group = matcher.group(1);
      var artifact = matcher.group(2);
      var version = matcher.group(3);
      var uri = Maven.central(group, artifact, version);
      var request = HttpRequest.newBuilder(uri).method("HEAD", BodyPublishers.noBody()).build();
      var response = http.send(request, BodyHandlers.discarding());
      if (response.statusCode() == 200) {
        var size = response.headers().firstValueAsLong("Content-Length").orElse(-1);
        System.out.printf("%s=%s#SIZE=%d%n", key, uri, size);
      } else {
        response
            .headers()
            .map()
            .forEach((header, entry) -> System.out.printf("%s -> %s%n", header, entry));
      }
      return;
    }
    System.out.printf("Unknown property protocol %s=%s%n", key, value);
  }

  record Maven(
      String repository,
      String group,
      String artifact,
      String version,
      String classifier,
      String type) {

    static final String CENTRAL_REPOSITORY = "https://repo.maven.apache.org/maven2";
    static final String DEFAULT_CLASSIFIER = "", DEFAULT_TYPE = "jar";

    static URI central(String group, String artifact, String version) {
      return new Maven(
              CENTRAL_REPOSITORY, group, artifact, version, DEFAULT_CLASSIFIER, DEFAULT_TYPE)
          .toUri();
    }

    URI toUri() {
      var joiner = new StringJoiner("/").add(repository);
      joiner.add(group.replace('.', '/')).add(artifact).add(version);
      var file = artifact + '-' + (classifier.isBlank() ? version : version + '-' + classifier);
      return URI.create(joiner.add(file + '.' + type).toString());
    }
  }
}

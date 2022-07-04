import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

class scan {
  public static void main(String... args) throws Exception {
    // var uri = "https://github.com/sormuras/bach-info/archive/a4a310ca.zip"; // SHA
    // var uri = "https://github.com/sormuras/bach-info/archive/refs/tags/r4711.zip"; // TAG
    // var uri = "https://github.com/sormuras/bach-info/archive/refs/heads/main.zip"; // BRANCH
    var uri = "https://github.com/sormuras/bach-info/archive/HEAD.zip"; // "HEAD" -> "refs/heads/main" -> "a4a310ca"
    var http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    var request = HttpRequest.newBuilder(URI.create(uri)).build();
    var response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());

    var libraries = new ArrayList<String>();
    var tools = new ArrayList<String>();

    try (var zip = new ZipInputStream(new ByteArrayInputStream(response.body()))) {
      while (true) {
        var entry = zip.getNextEntry();
        if (entry == null) break;
        var name = entry.getName();
        if (!name.endsWith(".properties")) continue;
        System.out.println(name);
        var last = name.lastIndexOf('/');
        var item = name.substring(last + 1, name.length() - 11);
        if (name.contains("/external-modules/")) libraries.add(item);
        else if (name.contains("/external-tools/")) tools.add(item);
        else System.out.println("Unsupported entry name: " + name);
      }
    }
    System.out.printf("%d external modular library(ies)%n", libraries.size());
    for (var library : libraries) System.out.printf("  bach import %s%n", library);
    System.out.printf("%d external tool(s)%n", tools.size());
    for (var tool : tools) System.out.printf("  bach install %s%n", tool);
  }
}

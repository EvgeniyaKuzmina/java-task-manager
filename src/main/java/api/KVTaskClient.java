package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String API_KEY;
    KVServer kvServer;
    URI url;
    HttpClient client;

    public KVTaskClient(URI url) throws IOException, InterruptedException {
        this.url = url;
        kvServer = new KVServer();
        kvServer.start();
        API_KEY = getAPI_KEY();
    }

    protected String getAPI_KEY() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        URI urlKey = URI.create(url.toString() + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(urlKey)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        if (response.statusCode() == 200) {
            return response.body();
        }
        return "Неверный адрес запроса";
    }

    // сохраняет состояние менеджера задач через запрос POST /save/<ключ>?API_KEY=.
    protected void put(String key, String json) throws IOException,
                                                       InterruptedException {
        client = HttpClient.newHttpClient();
        URI urlKey = URI.create(url.toString() + "/save/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request =  HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(urlKey)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        if (response.statusCode() != 200) {
            System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
        }
    }

    // возвращает состояние менеджера задач через запрос GET /load/<ключ>?API_KEY=
    protected String load(String key) throws IOException,
                                             InterruptedException {
        client = HttpClient.newHttpClient();
        URI urlKey = URI.create(url.toString() + "/load/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request =  HttpRequest.newBuilder()
                .GET()
                .uri(urlKey)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        if (response.body().isEmpty()) {
            return "Задач с ключом" + key + "нет";
        }
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            return "Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode();
        }
    }
}

package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String API_KEY;
    KVServer kvServer;
    URI url;
    HttpRequest.Builder requestBuilder;
    HttpClient client;
    //Ещё на вебинаре обсуждали, что здесь должна быть хешмапа, которая будет хранить полученные через метод save
    // задачи, эпики, подзадачи и историю в формате json. Но я так и не поняла зачем эта мапа и сделала без неё


    public KVTaskClient(URI url) throws IOException, InterruptedException {
        this.url = url;
        kvServer = new KVServer();
        kvServer.start();
        API_KEY = kvServer.getAPI_KEY();
        requestBuilder = HttpRequest.newBuilder();
        client = HttpClient.newHttpClient();

    }


    protected void put(String key, String json) throws IOException,
                                                       InterruptedException {//должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_KEY=.
        URI urlKey = URI.create(url.toString() + "/save/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = requestBuilder
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


    protected String load(String key) throws IOException,
                                             InterruptedException { //должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_KEY=
        URI urlKey = URI.create(url.toString() + "/load/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = requestBuilder
                .GET()
                .uri(urlKey)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        if (response.statusCode() == 200) {
            JsonElement jsonElement = JsonParser.parseString(response.body());
            if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                return "Ответ от сервера не соответствует ожидаемому.";
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return jsonObject.get(key).getAsString();
        } else {
            return "Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode();
        }
    }
}

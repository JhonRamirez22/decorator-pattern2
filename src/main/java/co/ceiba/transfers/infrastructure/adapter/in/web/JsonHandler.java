package co.ceiba.transfers.infrastructure.adapter.in.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/** Base class for the REST adapters: handles errors and JSON responses. */
public abstract class JsonHandler implements HttpHandler {

    private final JsonWriter jsonWriter = new JsonWriter();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            sendJson(exchange, 200, handleRequest(exchange));
        } catch (RuntimeException error) {
            sendJson(exchange, 400, Map.of("error", error.getClass().getSimpleName(),
                    "detail", String.valueOf(error.getMessage())));
        }
    }

    protected abstract Object handleRequest(HttpExchange exchange) throws IOException;

    private void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = jsonWriter.write(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}

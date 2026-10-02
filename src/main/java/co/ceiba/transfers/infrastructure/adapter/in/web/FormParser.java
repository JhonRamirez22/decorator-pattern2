package co.ceiba.transfers.infrastructure.adapter.in.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Parses application/x-www-form-urlencoded bodies. */
public class FormParser {

    public Map<String, String> parse(String body) {
        Map<String, String> fields = new HashMap<>();
        if (body == null || body.isBlank()) {
            return fields;
        }
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            fields.put(key, value);
        }
        return fields;
    }
}

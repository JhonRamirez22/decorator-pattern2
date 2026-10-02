package co.ceiba.transfers.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Minimal JSON serializer for maps, lists, strings, numbers and booleans. */
public class JsonWriter {

    public String write(Object value) {
        StringBuilder out = new StringBuilder();
        append(out, value);
        return out.toString();
    }

    private void append(StringBuilder out, Object value) {
        if (value == null) {
            out.append("null");
        } else if (value instanceof String text) {
            out.append('"').append(escape(text)).append('"');
        } else if (value instanceof BigDecimal number) {
            out.append(number.toPlainString());
        } else if (value instanceof Number || value instanceof Boolean) {
            out.append(value);
        } else if (value instanceof Map<?, ?> map) {
            out.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) out.append(',');
                append(out, String.valueOf(entry.getKey()));
                out.append(':');
                append(out, entry.getValue());
                first = false;
            }
            out.append('}');
        } else if (value instanceof List<?> list) {
            out.append('[');
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) out.append(',');
                append(out, list.get(i));
            }
            out.append(']');
        } else {
            append(out, value.toString());
        }
    }

    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}

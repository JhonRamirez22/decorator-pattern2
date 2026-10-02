package co.ceiba.transfers.infrastructure.adapter.in.web;

import co.ceiba.transfers.application.service.TransferTemplateService;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.model.TransferRequestBuilder;
import co.ceiba.transfers.domain.model.TransferTemplate;
import co.ceiba.transfers.infrastructure.config.DecoratorType;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Lists preset templates and saves independently customized copies. */
public final class TemplatesHandler extends JsonHandler {

    private final TransferTemplateService service;
    private final FormParser formParser = new FormParser();

    public TemplatesHandler(TransferTemplateService service) {
        this.service = service;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            return service.findAll().stream().map(this::toJson).toList();
        }
        if (!"POST".equals(exchange.getRequestMethod())) {
            throw new IllegalArgumentException("Only GET and POST are supported");
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = formParser.parse(body);
        TransferRequest request = new TransferRequestBuilder()
                .source(form.get("source")).target(form.get("target"))
                .amount(new BigDecimal(form.getOrDefault("amount", "0"))).build();
        List<String> decorators = Arrays.stream(form.getOrDefault("decorators", "").split(","))
                .map(String::trim).filter(value -> !value.isEmpty())
                .map(DecoratorType::valueOf).map(Enum::name).toList();
        TransferTemplate saved = service.saveCopy(form.get("prototypeId"), form.get("name"),
                request, decorators);
        return toJson(saved);
    }

    private Map<String, Object> toJson(TransferTemplate template) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", template.getId());
        result.put("name", template.getName());
        result.put("profile", template.getProfile());
        result.put("builtIn", template.isBuiltIn());
        result.put("source", template.getRequest().sourceAccountId());
        result.put("target", template.getRequest().targetAccountId());
        result.put("amount", template.getRequest().amount());
        result.put("decorators", template.getDecorators());
        return result;
    }
}

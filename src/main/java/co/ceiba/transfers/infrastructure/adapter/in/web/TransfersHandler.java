package co.ceiba.transfers.infrastructure.adapter.in.web;

import co.ceiba.transfers.domain.model.ReceiptLine;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.infrastructure.config.DecoratorType;
import co.ceiba.transfers.infrastructure.config.TransferChainFactory;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * POST /api/transfers with form fields: source, target, amount and
 * decorators (comma separated, outermost first).
 */
public class TransfersHandler extends JsonHandler {

    private final TransferChainFactory chainFactory;
    private final FormParser formParser = new FormParser();

    public TransfersHandler(TransferChainFactory chainFactory) {
        this.chainFactory = chainFactory;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            throw new IllegalArgumentException("Only POST is supported");
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = formParser.parse(body);

        TransferRequest request = new TransferRequest(form.get("source"), form.get("target"),
                new BigDecimal(form.getOrDefault("amount", "0")));
        List<DecoratorType> decorators = parseDecorators(form.getOrDefault("decorators", ""));

        TransferReceipt receipt = chainFactory.build(decorators).transfer(request);
        return toJson(receipt, decorators);
    }

    private List<DecoratorType> parseDecorators(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(DecoratorType::valueOf)
                .toList();
    }

    private Map<String, Object> toJson(TransferReceipt receipt, List<DecoratorType> decorators) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("status", receipt.getStatus().name());
        json.put("message", receipt.getMessage());
        json.put("amount", receipt.getRequest().amount());
        json.put("totalDebited", receipt.totalDebited());
        json.put("chain", decorators.stream().map(Enum::name).toList());
        json.put("lines", receipt.getLines().stream().map(this::lineToJson).toList());
        return json;
    }

    private Map<String, Object> lineToJson(ReceiptLine line) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("layer", line.layer());
        json.put("concept", line.concept());
        json.put("charge", line.charge());
        return json;
    }
}

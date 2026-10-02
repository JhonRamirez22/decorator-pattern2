package co.ceiba.transfers.domain.model;

import java.util.List;

/** Prototype: an immutable transfer configuration that can be copied independently. */
public final class TransferTemplate {

    private final String id;
    private final String name;
    private final String profile;
    private final TransferRequest request;
    private final List<String> decorators;
    private final boolean builtIn;

    public TransferTemplate(String id, String name, String profile, TransferRequest request,
                            List<String> decorators, boolean builtIn) {
        this.id = id;
        this.name = name;
        this.profile = profile;
        this.request = request;
        this.decorators = List.copyOf(decorators);
        this.builtIn = builtIn;
    }

    public TransferTemplate copy(String newId, String newName) {
        TransferRequest copiedRequest = new TransferRequestBuilder()
                .source(request.sourceAccountId())
                .target(request.targetAccountId())
                .amount(request.amount())
                .build();
        return new TransferTemplate(newId, newName, profile, copiedRequest, decorators, false);
    }

    public TransferTemplate withConfiguration(TransferRequest configuredRequest,
                                              List<String> configuredDecorators,
                                              String configuredProfile) {
        return new TransferTemplate(id, name, configuredProfile, configuredRequest,
                configuredDecorators, builtIn);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProfile() { return profile; }
    public TransferRequest getRequest() { return request; }
    public List<String> getDecorators() { return decorators; }
    public boolean isBuiltIn() { return builtIn; }
}

package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestIdGeneratorResolver {
    private final List<RequestIdGenerator> requestIdGenerators;

    public RequestIdGenerator get(RequestType type) {
        return requestIdGenerators.stream()
            .filter(generator -> generator.getTypes().contains(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Could not resolve request id generator for request type: " + type.name()));
    }

}

package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;

@Service
@RequiredArgsConstructor
public class ALRCloseService {

    private final RequestService requestService;
    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);

    @Transactional
    public void addClosedRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        ALRApplicationClosedRequestActionPayload actionPayload = ALR_MAPPER
                .toALRApplicationClosedRequestActionPayload(requestPayload);

        // Add to request
        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.ALR_APPLICATION_CLOSED,
                requestPayload.getRegulatorAssignee());
    }
}

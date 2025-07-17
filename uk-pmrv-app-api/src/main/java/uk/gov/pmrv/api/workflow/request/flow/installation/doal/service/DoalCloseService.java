package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper.DoalMapper;

@Service
@RequiredArgsConstructor
public class DoalCloseService {

    private final RequestService requestService;
    private static final DoalMapper DOAL_MAPPER = Mappers.getMapper(DoalMapper.class);

    @Transactional
    public void addClosedRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        DoalApplicationClosedRequestActionPayload actionPayload = DOAL_MAPPER
                .toDoalApplicationClosedRequestActionPayload(requestPayload);

        // Add to request
        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.DOAL_APPLICATION_CLOSED,
                requestPayload.getRegulatorAssignee());
    }
}

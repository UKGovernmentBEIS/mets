package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.mapper.WasteQDRMapper;

@Service
@RequiredArgsConstructor
public class WasteQDRCompleteService {

    private final RequestService requestService;
    private static final WasteQDRMapper WASTE_QDR_MAPPER = Mappers.getMapper(WasteQDRMapper.class);
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    public void addRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final WasteQDRRequestPayload requestPayload = (WasteQDRRequestPayload) request.getPayload();

        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

        final WasteQDRApplicationCompletedRequestActionPayload actionPayload =
                WASTE_QDR_MAPPER.toWasteQDRApplicationCompletedRequestActionPayload(requestPayload, installationOperatorDetails);

        actionPayload.setWasteQDRAttachments(requestPayload.getWasteQDRAttachments());
        actionPayload.setRegulatorReviewAttachments(requestPayload.getRegulatorReviewAttachments());

        RequestActionType actionType  = RequestActionType.WASTE_QDR_APPLICATION_COMPLETED;

        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorReviewer());
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;

@Service
@RequiredArgsConstructor
public class BDRCompleteService {

    private final RequestService requestService;
    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService requestVerificationService;

    public void addRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();


        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

 		requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        final BDRApplicationCompletedRequestActionPayload actionPayload = 
                BDR_MAPPER.toBDRApplicationCompletedRequestActionPayload(requestPayload, installationOperatorDetails, requestPayload.getVerificationReport());

        actionPayload.setBdrAttachments(requestPayload.getBdrAttachments());
        actionPayload.setRegulatorReviewAttachments(requestPayload.getRegulatorReviewAttachments());

        RequestActionType actionType  = RequestActionType.BDR_APPLICATION_COMPLETED;

        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorReviewer());
    }
}

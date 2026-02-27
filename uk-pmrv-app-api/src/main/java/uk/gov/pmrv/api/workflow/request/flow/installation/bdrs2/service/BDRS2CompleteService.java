package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper.BDRS2Mapper;

@Service
@RequiredArgsConstructor
public class BDRS2CompleteService {

    private final RequestService requestService;
    private static final BDRS2Mapper BDRS2_MAPPER = Mappers.getMapper(BDRS2Mapper.class);
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService requestVerificationService;

    @Transactional
    public void complete(final String requestId) {
        //add insertion to account file attachment table method
        //bulk download
    }

    public void addRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();


        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

        requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
                request.getVerificationBodyId());

        final BDRS2ApplicationCompletedRequestActionPayload actionPayload =
                BDRS2_MAPPER.toBDRS2ApplicationCompletedRequestActionPayload(requestPayload, installationOperatorDetails, requestPayload.getVerificationReport());

        actionPayload.setBdrs2Attachments(requestPayload.getBdrs2Attachments());
        actionPayload.setRegulatorReviewAttachments(requestPayload.getRegulatorReviewAttachments());

        RequestActionType actionType  = RequestActionType.BDRS2_APPLICATION_COMPLETED;

        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorReviewer());
    }
}

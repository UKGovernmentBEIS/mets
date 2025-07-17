package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;

import java.util.Map;
import java.util.stream.Collectors;


public abstract class InstallationInspectionOperatorRespondInitializer
        implements InitializeRequestTaskHandler {


    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final InstallationInspectionRequestPayload requestPayload =
               (InstallationInspectionRequestPayload) request.getPayload();

        return InstallationInspectionOperatorRespondRequestTaskPayload.builder()
           .payloadType(RequestTaskPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS_PAYLOAD)
           .installationInspection(InstallationInspection
                   .builder()
                   .responseDeadline(requestPayload.getInstallationInspection().getResponseDeadline())
                   .followUpActions(requestPayload.getInstallationInspection().getFollowUpActions())
                   .details(InstallationInspectionDetails
                           .builder()
                           .date(requestPayload.getInstallationInspection().getDetails().getDate())
                           .officerNames(requestPayload.getInstallationInspection().getDetails().getOfficerNames())
                           .files(requestPayload.getInstallationInspection().getDetails().getFiles())
                           .build())
                   .build())
           .inspectionAttachments(requestPayload.getInspectionAttachments().entrySet().stream().filter(entry ->
                    !requestPayload
                            .getInstallationInspection()
                            .getDetails()
                            .getRegulatorExtraFiles()
                            .contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
           .build();
    }

}

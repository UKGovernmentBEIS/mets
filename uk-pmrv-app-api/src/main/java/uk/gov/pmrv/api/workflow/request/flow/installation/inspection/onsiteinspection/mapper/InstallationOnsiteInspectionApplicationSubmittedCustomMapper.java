package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class InstallationOnsiteInspectionApplicationSubmittedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final InstallationInspectionApplicationSubmittedRequestActionPayload entityPayload =
                (InstallationInspectionApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        entityPayload.setInspectionAttachments(
                entityPayload.getInspectionAttachments().entrySet().stream().filter(entry ->
                !entityPayload
                        .getInstallationInspection()
                        .getDetails()
                        .getRegulatorExtraFiles()
                        .contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        InstallationInspectionDetails installationInspectionDetails = InstallationInspectionDetails
                .builder()
                .date(entityPayload.getInstallationInspection().getDetails().getDate())
                .files(entityPayload.getInstallationInspection().getDetails().getFiles())
                .regulatorExtraFiles(new HashSet<>())
                .officerNames(entityPayload.getInstallationInspection().getDetails().getOfficerNames())
                .build();

        entityPayload.getInstallationInspection().setDetails(installationInspectionDetails);

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}

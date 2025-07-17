package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface InstallationInspectionMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPONDED_PAYLOAD)")
    InstallationInspectionOperatorRespondedRequestActionPayload toInstallationInspectionOperatorRespondedRequestActionPayload(
            InstallationInspectionOperatorRespondRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.INSTALLATION_INSPECTION_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "inspectionAttachments", ignore = true)
    InstallationInspectionApplicationSubmittedRequestActionPayload toSubmittedActionPayload(InstallationInspectionRequestPayload requestPayload);

    @AfterMapping
    default void setInstallationInspectionAttachments(@MappingTarget InstallationInspectionApplicationSubmittedRequestActionPayload actionPayload, InstallationInspectionRequestPayload requestPayload) {
        actionPayload.setInspectionAttachments(requestPayload.getInspectionAttachments());
    }


    @Mapping(target = "payloadType", source = "payloadType")
    InstallationInspectionApplicationSubmitRequestTaskPayload toInstallationInspectionApplicationSubmitRequestTaskPayload(
            InstallationInspectionRequestPayload requestPayload, RequestTaskPayloadType payloadType);

}

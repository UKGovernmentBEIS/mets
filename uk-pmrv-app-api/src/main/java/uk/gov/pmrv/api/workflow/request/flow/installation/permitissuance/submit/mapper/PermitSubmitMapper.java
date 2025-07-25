package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitSubmitMapper {
    
    PermitContainer toPermitContainer(
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssuanceApplicationSubmitRequestTaskPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "permitAttachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    PermitIssuanceApplicationSubmittedRequestActionPayload toPermitIssuanceApplicationSubmittedRequestActionPayload(
        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssuanceApplicationSubmitRequestTaskPayload);
}

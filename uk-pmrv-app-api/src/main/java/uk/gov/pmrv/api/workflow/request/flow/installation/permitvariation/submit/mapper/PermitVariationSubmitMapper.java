package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.mapper.PermitDeterminableMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitVariationSubmitMapper extends PermitDeterminableMapper {

	@Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
	PermitContainer toPermitContainer(PermitVariationApplicationSubmitRequestTaskPayload payload, InstallationOperatorDetails installationOperatorDetails);
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
	@Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    PermitVariationApplicationSubmittedRequestActionPayload toPermitVariationApplicationSubmittedRequestActionPayload(
        PermitVariationApplicationSubmitRequestTaskPayload permitVariationApplicationSubmitRequestTaskPayload, InstallationOperatorDetails installationOperatorDetails);
	
}

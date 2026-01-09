package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.mapper.PermitDeterminableMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitMapper extends PermitDeterminableMapper {

    @Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    PermitContainer toPermitContainer(PermitIssuanceRequestPayload permitIssuanceRequestPayload, InstallationOperatorDetails installationOperatorDetails);

    @AfterMapping
    default void setActivationDate(@MappingTarget PermitContainer permitContainer, PermitIssuanceRequestPayload permitIssuanceRequestPayload) {
    	PermitDeterminableMapper.super.setActivationDate(permitContainer, permitIssuanceRequestPayload);
    }

    @AfterMapping
    default void setAnnualEmissionsTargets(@MappingTarget PermitContainer permitContainer, PermitIssuanceRequestPayload permitIssuanceRequestPayload) {
    	PermitDeterminableMapper.super.setAnnualEmissionsTargets(permitContainer, permitIssuanceRequestPayload);
    }

    PermitContainer toPermitContainer(PermitIssuanceApplicationRequestTaskPayload payload);

}

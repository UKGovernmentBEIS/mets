package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.mapper.PermitDeterminableMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitVariationMapper extends PermitDeterminableMapper {

	@Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    PermitContainer toPermitContainer(PermitVariationRequestPayload permitVariationRequestPayload, InstallationOperatorDetails installationOperatorDetails);

    @AfterMapping
    default void setActivationDate(@MappingTarget PermitContainer permitContainer, PermitVariationRequestPayload permitVariationRequestPayload) {
    	if(permitVariationRequestPayload.isRegulatorLed()) {
    		permitContainer.setActivationDate(permitVariationRequestPayload.getDeterminationRegulatorLed().getActivationDate());
    	} else {
    		PermitDeterminableMapper.super.setActivationDate(permitContainer, permitVariationRequestPayload);
    	}
    }

    @AfterMapping
    default void setAnnualEmissionsTargets(@MappingTarget PermitContainer permitContainer, PermitVariationRequestPayload permitVariationRequestPayload) {
    	if(permitVariationRequestPayload.isRegulatorLed()) {
    		permitContainer.setAnnualEmissionsTargets(permitVariationRequestPayload.getDeterminationRegulatorLed().getAnnualEmissionsTargets());
    	} else {
    		PermitDeterminableMapper.super.setAnnualEmissionsTargets(permitContainer, permitVariationRequestPayload);
    	}
    }
    
    PermitVariationRequestInfo toPermitVariationRequestInfo(Request request);
    
    @Mapping(target = "endDate", source = "endDate")
    PermitVariationRequestInfo toPermitVariationRequestInfo(Request request, LocalDateTime endDate);
    
    @AfterMapping
    default void setMetadata(@MappingTarget PermitVariationRequestInfo permitVariationRequestInfo, Request request) {
    	if(request.getType() == RequestType.PERMIT_VARIATION) {
    		permitVariationRequestInfo.setMetadata((PermitVariationRequestMetadata) request.getMetadata());
    	}
    }
	
}

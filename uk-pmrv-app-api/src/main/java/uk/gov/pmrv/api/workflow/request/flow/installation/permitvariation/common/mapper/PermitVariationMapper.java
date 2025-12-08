package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.mapper;

import org.apache.commons.lang3.ObjectUtils;
import java.util.Set;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.GrantDetermination;
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
	default void removeNonDigitizedMmpFiles(@MappingTarget PermitContainer permitContainer) {
		MonitoringMethodologyPlans mmp = permitContainer.getPermit().getMonitoringMethodologyPlans();
		if (mmp == null || !mmp.isExist()) {
			return;
		}
		Set<UUID> plans = mmp.getPlans();
		DigitizedPlan digitizedPlan = mmp.getDigitizedPlan();
		if (ObjectUtils.isEmpty(plans) || ObjectUtils.isEmpty(digitizedPlan)) {
			return;
		}
		plans.forEach(plan -> permitContainer.getPermitAttachments().remove(plan));
		mmp.setPlans(null);
	}

    @AfterMapping
    default void setAnnualEmissionsTargets(@MappingTarget PermitContainer permitContainer, PermitVariationRequestPayload permitVariationRequestPayload) {
    	if(permitVariationRequestPayload.isRegulatorLed()) {
    		permitContainer.setAnnualEmissionsTargets(permitVariationRequestPayload.getDeterminationRegulatorLed().getAnnualEmissionsTargets());
    	} else {
    		PermitDeterminableMapper.super.setAnnualEmissionsTargets(permitContainer, permitVariationRequestPayload);
    	}
    }

	@AfterMapping
	default void setRegistryReportingFirstYear(@MappingTarget PermitContainer permitContainer, PermitVariationRequestPayload permitVariationRequestPayload) {
		if(permitVariationRequestPayload.isRegulatorLed()) {
			Integer firstYearOfReportingObligation = null;
			if(DeterminationType.GRANTED.equals(permitVariationRequestPayload.getDeterminationRegulatorLed().getType())) {
				firstYearOfReportingObligation = permitVariationRequestPayload.getDeterminationRegulatorLed().getFirstYearOfReportingObligation();
			}
			permitContainer.setFirstYearOfReportingObligation(ObjectUtils.isEmpty(firstYearOfReportingObligation) ?
					permitVariationRequestPayload.getOriginalPermitContainer().getFirstYearOfReportingObligation() : firstYearOfReportingObligation);
		}
		else {
			Integer firstYearOfReportingObligation = null;
			if(DeterminationType.GRANTED.equals(permitVariationRequestPayload.getDetermination().getType())) {
				firstYearOfReportingObligation = ((GrantDetermination) permitVariationRequestPayload.getDetermination()).getFirstYearOfReportingObligation();
			}
			permitContainer.setFirstYearOfReportingObligation(ObjectUtils.isEmpty(firstYearOfReportingObligation) ?
					permitVariationRequestPayload.getOriginalPermitContainer().getFirstYearOfReportingObligation() : firstYearOfReportingObligation);
		}

	}

	@Mapping(target = "changeType", constant = "AEM Variation")
    PermitVariationRequestInfo toPermitVariationRequestInfo(Request request);
    
    @Mapping(target = "endDate", source = "endDate")
	@Mapping(target = "changeType", constant = "AEM Variation")
    PermitVariationRequestInfo toPermitVariationRequestInfo(Request request, LocalDateTime endDate);
    
    @AfterMapping
    default void setMetadata(@MappingTarget PermitVariationRequestInfo permitVariationRequestInfo, Request request) {
    	if(request.getType() == RequestType.PERMIT_VARIATION) {
    		permitVariationRequestInfo.setMetadata((PermitVariationRequestMetadata) request.getMetadata());
    	}
    }
	
}

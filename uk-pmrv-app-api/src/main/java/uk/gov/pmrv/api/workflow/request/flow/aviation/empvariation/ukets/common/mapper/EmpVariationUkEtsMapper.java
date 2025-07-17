package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationUkEtsMapper extends EmpVariationUkEtsOperatorDetailsMapper {
	
	@Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.UK_ETS_AVIATION)")
    EmissionsMonitoringPlanUkEtsContainer toEmissionsMonitoringPlanUkEtsContainer(
    		EmpVariationUkEtsApplicationRequestTaskPayload taskPayload);
	
	@Mapping(target = "serviceContactDetails", source = "requestAviationAccountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
	EmissionsMonitoringPlanUkEtsContainer toEmissionsMonitoringPlanUkEtsContainer(
			EmpVariationUkEtsRequestPayload requestPayload, RequestAviationAccountInfo requestAviationAccountInfo,
			EmissionTradingScheme scheme);
	
	@Mapping(target = "operatorDetails.operatorName", ignore = true)
	@Mapping(target = "operatorDetails.crcoCode", ignore = true)
	@Mapping(target = "operatorDetails.attachmentIds", ignore = true)
	@Mapping(target = "empSectionAttachmentIds", ignore = true)
	@Mapping(target = "notEmptyDynamicSections", ignore = true)
	EmissionsMonitoringPlanUkEts cloneEmissionsMonitoringPlanUkEts(EmissionsMonitoringPlanUkEts source, String operatorName, String crcoCode);
	
    EmpVariationRequestInfo toEmpVariationRequestInfo(Request request);
    
    @AfterMapping
    default void setMetadata(@MappingTarget EmpVariationRequestInfo empVariationRequestInfo, Request request) {
         empVariationRequestInfo.setMetadata((EmpVariationRequestMetadata) request.getMetadata());
    }
    
}

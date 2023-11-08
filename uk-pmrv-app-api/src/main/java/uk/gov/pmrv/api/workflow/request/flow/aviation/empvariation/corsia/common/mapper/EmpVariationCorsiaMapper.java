package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationRequestTaskPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationCorsiaMapper extends EmpVariationCorsiaOperatorDetailsMapper {
	
	@Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.CORSIA)")
    EmissionsMonitoringPlanCorsiaContainer toEmissionsMonitoringPlanCorsiaContainer(
    		EmpVariationCorsiaApplicationRequestTaskPayload taskPayload);

    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    EmissionsMonitoringPlanCorsiaContainer toEmissionsMonitoringPlanCorsiaContainer(
        EmpVariationCorsiaRequestPayload requestPayload,
        EmissionTradingScheme scheme
    );
    
    @Mapping(target = "serviceContactDetails", source = "requestAviationAccountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
	EmissionsMonitoringPlanCorsiaContainer toEmissionsMonitoringPlanCorsiaContainer(
			EmpVariationCorsiaRequestPayload requestPayload, RequestAviationAccountInfo requestAviationAccountInfo,
			EmissionTradingScheme scheme);

    @Mapping(target = "operatorDetails.operatorName", ignore = true)
	@Mapping(target = "operatorDetails.attachmentIds", ignore = true)
	@Mapping(target = "empSectionAttachmentIds", ignore = true)
	@Mapping(target = "notEmptyDynamicSections", ignore = true)
	EmissionsMonitoringPlanCorsia cloneEmissionsMonitoringPlanCorsia(
			EmissionsMonitoringPlanCorsia source, String operatorName);
    
    EmpVariationRequestInfo toEmpVariationRequestInfo(Request request);

    @AfterMapping
    default void setMetadata(@MappingTarget EmpVariationRequestInfo empVariationRequestInfo, Request request) {
        if(RequestType.EMP_VARIATION_CORSIA.equals(request.getType())) {
            empVariationRequestInfo.setMetadata((EmpVariationRequestMetadata) request.getMetadata());
        }
    }
}

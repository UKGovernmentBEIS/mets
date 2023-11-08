package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper;

import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsOperatorDetailsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationUkEtsReviewMapper extends EmpVariationUkEtsOperatorDetailsMapper {
	
	@Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.UK_ETS_AVIATION)")
    EmissionsMonitoringPlanUkEtsContainer toEmissionsMonitoringPlanUkEtsContainer(
        EmpVariationUkEtsApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload);
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationUkEtsApplicationReviewRequestTaskPayload toEmpVariationUkEtsApplicationReviewRequestTaskPayload(
    		EmpVariationUkEtsRequestPayload empVariationUkEtsRequestPayload, RequestAviationAccountInfo accountInfo, 
    		RequestTaskPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationUkEtsApplicationApprovedRequestActionPayload toEmpVariationUkEtsApplicationApprovedRequestActionPayload(
        EmpVariationUkEtsRequestPayload requestPayload, RequestAviationAccountInfo accountInfo, 
        Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload toEmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload(
    		EmpVariationUkEtsRequestPayload requestPayload, Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    EmpVariationUkEtsApplicationRejectedRequestActionPayload toEmpVariationUkEtsApplicationRejectedRequestActionPayload(
    		EmpVariationUkEtsRequestPayload requestPayload, Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.operatorName", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload toEmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload(
    		EmpVariationUkEtsRequestPayload requestPayload, RequestAviationAccountInfo accountInfo);
    
    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
                                   EmpVariationUkEtsRequestPayload requestPayload) {
        requestActionPayload.setEmpAttachments(requestPayload.getEmpAttachments());
    }
}

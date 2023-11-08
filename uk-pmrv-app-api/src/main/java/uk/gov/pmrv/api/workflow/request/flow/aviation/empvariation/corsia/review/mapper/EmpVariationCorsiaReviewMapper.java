package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper;

import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaOperatorDetailsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationCorsiaReviewMapper extends EmpVariationCorsiaOperatorDetailsMapper {

    @Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.CORSIA)")
    EmissionsMonitoringPlanCorsiaContainer toEmissionsMonitoringPlanCorsiaContainer(
        EmpVariationCorsiaApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload);

	@Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationCorsiaApplicationReviewRequestTaskPayload toEmpVariationCorsiaApplicationReviewRequestTaskPayload(
    		EmpVariationCorsiaRequestPayload empVariationCorsiaRequestPayload, RequestAviationAccountInfo accountInfo, 
    		RequestTaskPayloadType payloadType);
	
	@Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationCorsiaApplicationApprovedRequestActionPayload toEmpVariationCorsiaApplicationApprovedRequestActionPayload(
        EmpVariationCorsiaRequestPayload requestPayload, RequestAviationAccountInfo accountInfo, 
        Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);
	
	@Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload toEmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload(
    		EmpVariationCorsiaRequestPayload requestPayload, Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    EmpVariationCorsiaApplicationRejectedRequestActionPayload toEmpVariationCorsiaApplicationRejectedRequestActionPayload(
    		EmpVariationCorsiaRequestPayload requestPayload, Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.operatorName", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload toEmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload(
        EmpVariationCorsiaRequestPayload requestPayload, RequestAviationAccountInfo accountInfo);

    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
                                   EmpVariationCorsiaRequestPayload requestPayload) {
        requestActionPayload.setEmpAttachments(requestPayload.getEmpAttachments());
    }
}

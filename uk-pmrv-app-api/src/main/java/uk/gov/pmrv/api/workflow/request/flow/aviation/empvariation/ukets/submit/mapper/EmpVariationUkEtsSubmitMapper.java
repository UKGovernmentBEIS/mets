package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsOperatorDetailsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationUkEtsSubmitMapper extends EmpVariationUkEtsOperatorDetailsMapper {
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationUkEtsApplicationSubmittedRequestActionPayload toEmpVariationUkEtsApplicationSubmittedRequestActionPayload(
        EmpVariationUkEtsApplicationSubmitRequestTaskPayload taskPayload, RequestAviationAccountInfo accountInfo);
    
    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpVariationUkEtsApplicationSubmittedRequestActionPayload requestActionPayload,
                                   EmpVariationUkEtsApplicationSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setEmpAttachments(taskPayload.getEmpAttachments());
    }
    
}

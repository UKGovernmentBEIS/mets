package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaOperatorDetailsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationCorsiaSubmitMapper extends EmpVariationCorsiaOperatorDetailsMapper {

	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpVariationCorsiaApplicationSubmittedRequestActionPayload toEmpVariationCorsiaApplicationSubmittedRequestActionPayload(
        EmpVariationCorsiaApplicationSubmitRequestTaskPayload taskPayload, RequestAviationAccountInfo accountInfo);
    
    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpVariationCorsiaApplicationSubmittedRequestActionPayload requestActionPayload,
                                   EmpVariationCorsiaApplicationSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setEmpAttachments(taskPayload.getEmpAttachments());
    }
}

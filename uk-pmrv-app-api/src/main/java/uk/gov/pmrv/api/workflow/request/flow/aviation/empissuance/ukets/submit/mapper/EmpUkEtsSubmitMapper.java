package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpUkEtsSubmitMapper {

    @Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.UK_ETS_AVIATION)")
    EmissionsMonitoringPlanUkEtsContainer toEmissionsMonitoringPlanUkEtsContainer(
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload empIssuanceUkEtsApplicationSubmitRequestTaskPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.operatorName", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload toEmpIssuanceUkEtsApplicationSubmittedRequestActionPayload(
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload empIssuanceUkEtsApplicationSubmitRequestTaskPayload, RequestAviationAccountInfo accountInfo);


    @AfterMapping
    default void setOperatorDetailsNameAndCrcoCode(@MappingTarget EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload requestActionPayload,
                                                   RequestAviationAccountInfo accountInfo) {
        EmpOperatorDetails operatorDetails = requestActionPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setOperatorName(accountInfo.getOperatorName());
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }

    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload requestActionPayload,
                                   EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setEmpAttachments(taskPayload.getEmpAttachments());
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpUkEtsMapper {

    @Mapping(target = "serviceContactDetails", source = "requestAviationAccountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    EmissionsMonitoringPlanUkEtsContainer toEmissionsMonitoringPlanUkEtsContainer(EmpIssuanceUkEtsRequestPayload requestPayload,
                                                                                  RequestAviationAccountInfo requestAviationAccountInfo,
                                                                                  EmissionTradingScheme scheme);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget EmissionsMonitoringPlanUkEtsContainer container,
                                            RequestAviationAccountInfo accountInfo) {
        EmpOperatorDetails operatorDetails = container.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }
}

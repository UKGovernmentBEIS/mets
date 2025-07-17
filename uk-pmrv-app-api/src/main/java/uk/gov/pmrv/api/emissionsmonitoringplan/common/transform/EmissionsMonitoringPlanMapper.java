package uk.gov.pmrv.api.emissionsmonitoringplan.common.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmissionsMonitoringPlanDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmissionsMonitoringPlanMapper {
    @Mapping(target = "empAttachments", source = "emissionsMonitoringPlanEntity.empContainer.empAttachments")
    EmpDetailsDTO toEmpDetailsDTO(EmissionsMonitoringPlanEntity emissionsMonitoringPlanEntity, FileInfoDTO fileDocument);

    EmissionsMonitoringPlanUkEtsDTO toEmissionsMonitoringPlanUkEtsDTO(EmissionsMonitoringPlanEntity emissionsMonitoringPlanEntity);

    @AfterMapping
    default void setEmissionsMonitoringPlanUkEtsContainer(@MappingTarget EmissionsMonitoringPlanUkEtsContainer empContainerTarget,
                                                          EmissionsMonitoringPlanContainer empContainer) {
        if (empContainer instanceof EmissionsMonitoringPlanUkEtsContainer empContainerSource) {
            empContainerTarget.setEmissionsMonitoringPlan(empContainerSource.getEmissionsMonitoringPlan());
            empContainerTarget.setServiceContactDetails(empContainerSource.getServiceContactDetails());
            empContainerTarget.setEmpAttachments(empContainerSource.getEmpAttachments());
        }
    }

    EmissionsMonitoringPlanCorsiaDTO toEmissionsMonitoringPlanCorsiaDTO(EmissionsMonitoringPlanEntity emissionsMonitoringPlanEntity);

    @AfterMapping
    default void setEmissionsMonitoringPlanCorsiaContainer(@MappingTarget
                                                           EmissionsMonitoringPlanCorsiaContainer empContainerTarget,
                                                           EmissionsMonitoringPlanContainer empContainer) {

        if (empContainer instanceof EmissionsMonitoringPlanCorsiaContainer empContainerSource) {
            empContainerTarget.setEmissionsMonitoringPlan(empContainerSource.getEmissionsMonitoringPlan());
            empContainerTarget.setServiceContactDetails(empContainerSource.getServiceContactDetails());
            empContainerTarget.setEmpAttachments(empContainerSource.getEmpAttachments());
        }
    }

    default EmissionsMonitoringPlanDTO toEmissionsMonitoringPlanDTO(EmissionsMonitoringPlanEntity emissionsMonitoringPlanEntity, EmissionTradingScheme emissionTradingScheme) {
        if (EmissionTradingScheme.CORSIA.equals(emissionTradingScheme)) {
            return toEmissionsMonitoringPlanCorsiaDTO(emissionsMonitoringPlanEntity);
        } else if (EmissionTradingScheme.UK_ETS_AVIATION.equals(emissionTradingScheme)) {
            return toEmissionsMonitoringPlanUkEtsDTO(emissionsMonitoringPlanEntity);
        }
        throw new UnsupportedOperationException(String.format("Unsupported emissionTradingScheme: %s", emissionTradingScheme));
    }
}

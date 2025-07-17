package uk.gov.pmrv.api.permit.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PermitEntityMapperTest {

    private PermitEntityMapper cut = Mappers.getMapper(PermitEntityMapper.class);

    @Test
    void toPermitDetailsDTO() {
        Map<UUID, String> permitAttachments = Map.of(
                UUID.randomUUID(), "att1"
        );

        Long accountId = 1L;

        PermitContainer permitContainer = PermitContainer.builder()
                .permitType(PermitType.GHGE)
                .activationDate(LocalDate.of(2000, 1, 1))
                .permitAttachments(permitAttachments)
                .build();

        String fileDocumentUuid = UUID.randomUUID().toString();
        FileInfoDTO fileInfoDTO = FileInfoDTO.builder().name("name").uuid(fileDocumentUuid).build();
        PermitEntity permitEntity = PermitEntity.createPermit("permitId",
                permitContainer,
                accountId,
                fileDocumentUuid);

        PermitDetailsDTO result = cut.toPermitDetailsDTO(permitEntity, fileInfoDTO);

        assertThat(result).isEqualTo(PermitDetailsDTO.builder()
                .id("permitId")
                .activationDate(LocalDate.of(2000, 1, 1))
                .permitAttachments(permitAttachments)
                .fileDocument(fileInfoDTO)
                .build());
    }

    @Test
    void mapMonitoringApproachTypes() {

        final Map<MonitoringApproachType, PermitMonitoringApproachSection> approachSectionMap = Map.of(
                MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().approachDescription(
                        "CALCULATION_CO2 approachDescription").build(),
                MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder().approachDescription(
                        "MEASUREMENT_CO2 approachDescription").build(),
                MonitoringApproachType.FALLBACK, FallbackMonitoringApproach.builder().approachDescription("FALLBACK " +
                        "approachDescription").build(),
                MonitoringApproachType.MEASUREMENT_N2O, MeasurementOfN2OMonitoringApproach.builder().approachDescription(
                        "MEASUREMENT_N2O approachDescription").build(),
                MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCMonitoringApproach.builder().approachDescription(
                        "CALCULATION_PFC approachDescription").build(),
                MonitoringApproachType.INHERENT_CO2,
                InherentCO2MonitoringApproach.builder().inherentReceivingTransferringInstallations(Collections.emptyList()).build(),
                MonitoringApproachType.TRANSFERRED_CO2_N2O,
                TransferredCO2AndN2OMonitoringApproach.builder().build()
        );

        final Map<MonitoringApproachType, PermitMonitoringApproachSection> result = cut.cloneApproaches(approachSectionMap);

        assertEquals(approachSectionMap, result);
    }
}

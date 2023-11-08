package uk.gov.pmrv.api.permit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.DeepClone;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Transfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallationDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class, mappingControl = DeepClone.class)
public interface PermitEntityMapper {

    @Mapping(target = "permitContainer.permit.monitoringApproaches.attachmentIds", ignore = true)
    @Mapping(target = "permitContainer.permit.managementProcedures.attachmentIds", ignore = true)
    @Mapping(target = "permitContainer.permit.permitSectionAttachmentIds", ignore = true)
	PermitEntityDto toPermitEntityDto(PermitEntity permitEntity);

	@AfterMapping
	default void setInstallationLocation(@MappingTarget PermitEntityDto target, PermitEntity source) {
		target.getPermitContainer().getInstallationOperatorDetails().setInstallationLocation(source.getPermitContainer().getInstallationOperatorDetails().getInstallationLocation());
	}

    @Mapping(target = "activationDate", source = "permitEntity.permitContainer.activationDate")
    @Mapping(target = "permitAttachments", source = "permitEntity.permitContainer.permitAttachments")
    PermitDetailsDTO toPermitDetailsDTO(PermitEntity permitEntity, FileInfoDTO fileDocument);

    default Map<MonitoringApproachType, PermitMonitoringApproachSection> cloneApproaches(Map<MonitoringApproachType,
        PermitMonitoringApproachSection> value) {

		return value.entrySet().stream()
			.map(entry -> {
					final PermitMonitoringApproachSection section;
					switch (entry.getKey()) {
						case CALCULATION_CO2:
							section = cloneApproach((CalculationOfCO2MonitoringApproach) entry.getValue());
							break;
						case MEASUREMENT_CO2:
							section = cloneApproach((MeasurementOfCO2MonitoringApproach) entry.getValue());
							break;
						case FALLBACK:
							section = cloneApproach((FallbackMonitoringApproach) entry.getValue());
							break;
						case MEASUREMENT_N2O:
							section = cloneApproach((MeasurementOfN2OMonitoringApproach) entry.getValue());
							break;
						case CALCULATION_PFC:
							section = cloneApproach((CalculationOfPFCMonitoringApproach) entry.getValue());
							break;
						case INHERENT_CO2:
							section = cloneApproach((InherentCO2MonitoringApproach) entry.getValue());
							break;
						case TRANSFERRED_CO2_N2O:
							section = cloneApproach((TransferredCO2AndN2OMonitoringApproach) entry.getValue());
							break;
						default:
							section = null;

					}
					return Map.entry(entry.getKey(), section);
				}
			).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	default Transfer clone(Transfer transfer) {
		if (transfer.getInstallationDetailsType() == InstallationDetailsType.INSTALLATION_DETAILS) {
			var installationDetails = clone(transfer.getInstallationDetails());
			return cloneTransferBuilder(transfer).installationDetails(installationDetails).build();
		}
		return cloneTransferBuilder(transfer)
			.installationEmitter(clone(transfer.getInstallationEmitter()))
			.build();
	}

	@Mapping(target = "attachmentIds", ignore = true)
	CalculationOfCO2MonitoringApproach cloneApproach(CalculationOfCO2MonitoringApproach value);

	@Mapping(target = "emissionPoint", ignore = true)
	@Mapping(target = "attachmentIds", ignore = true)
	MeasurementOfCO2MonitoringApproach cloneApproach(MeasurementOfCO2MonitoringApproach value);

    @Mapping(target = "attachmentIds", ignore = true)
    FallbackMonitoringApproach cloneApproach(FallbackMonitoringApproach value);

	@Mapping(target = "attachmentIds", ignore = true)
	@Mapping(target = "emissionPoint", ignore = true)
	MeasurementOfN2OMonitoringApproach cloneApproach(MeasurementOfN2OMonitoringApproach value);

	@Mapping(target = "attachmentIds", ignore = true)
	CalculationOfPFCMonitoringApproach cloneApproach(CalculationOfPFCMonitoringApproach value);

    @Mapping(target = "attachmentIds", ignore = true)
    InherentCO2MonitoringApproach cloneApproach(InherentCO2MonitoringApproach value);

    @Mapping(target = "attachmentIds", ignore = true)
    TransferredCO2AndN2OMonitoringApproach cloneApproach(TransferredCO2AndN2OMonitoringApproach value);

	default InherentReceivingTransferringInstallation cloneApproach(InherentReceivingTransferringInstallation inherentReceivingTransferringInstallation) {
		var builder = InherentReceivingTransferringInstallation.builder()
			.inherentCO2Direction(inherentReceivingTransferringInstallation.getInherentCO2Direction())
			.totalEmissions(inherentReceivingTransferringInstallation.getTotalEmissions())
			.measurementInstrumentOwnerTypes(Set.copyOf(inherentReceivingTransferringInstallation.getMeasurementInstrumentOwnerTypes()))
			.installationDetailsType(inherentReceivingTransferringInstallation.getInstallationDetailsType());

		var inherentReceivingTransferringInstallationDetails =
			switch (inherentReceivingTransferringInstallation.getInstallationDetailsType()) {
				case INSTALLATION_EMITTER ->
					clone((InherentReceivingTransferringInstallationEmitter) inherentReceivingTransferringInstallation.getInherentReceivingTransferringInstallationDetailsType());
				case INSTALLATION_DETAILS ->
					clone((InherentReceivingTransferringInstallationDetails) inherentReceivingTransferringInstallation.getInherentReceivingTransferringInstallationDetailsType());
			};
		builder.inherentReceivingTransferringInstallationDetailsType(inherentReceivingTransferringInstallationDetails);
		return builder.build();
	}

    private static InherentReceivingTransferringInstallationEmitter clone(InherentReceivingTransferringInstallationEmitter receivingTransferringInstallation) {
        return InherentReceivingTransferringInstallationEmitter.builder()
            .installationEmitter(InstallationEmitter.builder()
                .email(receivingTransferringInstallation.getInstallationEmitter().getEmail())
                .emitterId(receivingTransferringInstallation.getInstallationEmitter().getEmitterId())
                .build())
            .build();
    }

    private static InherentReceivingTransferringInstallationDetails clone(InherentReceivingTransferringInstallationDetails receivingTransferringInstallation) {
        return InherentReceivingTransferringInstallationDetails.builder()
            .installationDetails(InstallationDetails.builder()
	            .installationName(receivingTransferringInstallation.getInstallationDetails().getInstallationName())
                .line1(receivingTransferringInstallation.getInstallationDetails().getLine1())
                .line2(receivingTransferringInstallation.getInstallationDetails().getLine2())
                .city(receivingTransferringInstallation.getInstallationDetails().getCity())
                .email(receivingTransferringInstallation.getInstallationDetails().getEmail())
                .postcode(receivingTransferringInstallation.getInstallationDetails().getPostcode())
                .build()
            )
            .build();
    }

    private static InstallationEmitter clone(InstallationEmitter transferInstallationEmitter) {
        return InstallationEmitter.builder()
            .emitterId(transferInstallationEmitter.getEmitterId())
            .email(transferInstallationEmitter.getEmail())
            .build();
    }

    private static InstallationDetails clone(InstallationDetails transferInstallationDetails) {
        return InstallationDetails.builder()
            .line1(transferInstallationDetails.getLine1())
            .line2(transferInstallationDetails.getLine2())
            .installationName(transferInstallationDetails.getInstallationName())
            .postcode(transferInstallationDetails.getPostcode())
            .city(transferInstallationDetails.getCity())
            .email(transferInstallationDetails.getEmail())
            .build();
    }

    private static TransferCO2.TransferCO2Builder<?, ?> cloneTransferBuilder(Transfer transfer) {
        return TransferCO2.builder()
            .transferDirection(((TransferCO2) transfer).getTransferDirection())
            .entryAccountingForTransfer(transfer.getEntryAccountingForTransfer())
            .installationDetailsType(transfer.getInstallationDetailsType());

    }
}

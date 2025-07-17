package uk.gov.pmrv.api.permit.mapper;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.DeepClone;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
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
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityFuelLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityHeatLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityProcessLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualProductionLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorHeatFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProduct;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.dolime.DolimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.lime.LimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.steamcracking.SteamCrackingSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.vinylchloridemonomer.VinylChlorideMonomerSP;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class, mappingControl = DeepClone.class)
public interface PermitEntityMapper {

    @Mapping(target = "permitContainer.permit.monitoringApproaches.attachmentIds", ignore = true)
    @Mapping(target = "permitContainer.permit.managementProcedures.attachmentIds", ignore = true)
    @Mapping(target = "permitContainer.permit.permitSectionAttachmentIds", ignore = true)
	@Mapping(target = "permitContainer.permit.monitoringMethodologyPlans.digitizedPlan.subInstallations[].specialProduct",
			qualifiedByName = "mapSpecialProduct")
	@Mapping(target = "permitContainer.permit.monitoringMethodologyPlans.digitizedPlan.subInstallations[].annualLevel",
			qualifiedByName = "mapAnnualLevel")
	@Mapping(target = "permitContainer.permit.monitoringMethodologyPlans.digitizedPlan.subInstallations[].directlyAttributableEmissions",
			qualifiedByName = "mapDirectlyAttributableEmissions")
	@Mapping(target = "permitContainer.permit.monitoringMethodologyPlans.digitizedPlan.subInstallations[].fuelInputAndRelevantEmissionFactor",
			qualifiedByName = "mapFuelInputAndRelevantEmissionFactor")
	@Mapping(target = "permitContainer.permit.monitoringMethodologyPlans.digitizedPlan.attachmentIds", ignore = true)
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

	default SpecialProduct mapSpecialProduct(SpecialProduct source) {
		if (source == null)
			return null;
		//populate with more special product types
		switch (source.getSpecialProductType()) {
			case REFINERY_PRODUCTS -> {
				if (source instanceof RefineryProductsSP refineryProduct) {
					return RefineryProductsSP.builder()
							.specialProductType(refineryProduct.getSpecialProductType())
							.methodologyAppliedDescription(refineryProduct.getMethodologyAppliedDescription())
							.hierarchicalOrder(refineryProduct.getHierarchicalOrder())
							.supportingFiles(refineryProduct.getSupportingFiles())
							.refineryProductsDataSources(refineryProduct.getRefineryProductsDataSources())
							.refineryProductsRelevantCWTFunctions(refineryProduct.getRefineryProductsRelevantCWTFunctions())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case DOLIME -> {
				if (source instanceof DolimeSP dolime) {
					return DolimeSP.builder()
							.specialProductType(dolime.getSpecialProductType())
							.methodologyAppliedDescription(dolime.getMethodologyAppliedDescription())
							.hierarchicalOrder(dolime.getHierarchicalOrder())
							.supportingFiles(dolime.getSupportingFiles())
							.dataSources(dolime.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case LIME -> {
				if (source instanceof LimeSP lime) {
					return LimeSP.builder()
							.specialProductType(lime.getSpecialProductType())
							.methodologyAppliedDescription(lime.getMethodologyAppliedDescription())
							.hierarchicalOrder(lime.getHierarchicalOrder())
							.supportingFiles(lime.getSupportingFiles())
							.dataSources(lime.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case STEAM_CRACKING -> {
				if (source instanceof SteamCrackingSP steamCracking) {
					return SteamCrackingSP.builder()
							.specialProductType(steamCracking.getSpecialProductType())
							.methodologyAppliedDescription(steamCracking.getMethodologyAppliedDescription())
							.hierarchicalOrder(steamCracking.getHierarchicalOrder())
							.supportingFiles(steamCracking.getSupportingFiles())
							.dataSources(steamCracking.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case AROMATICS ->  {
				if (source instanceof AromaticsSP aromatics) {
					return AromaticsSP.builder()
							.specialProductType(aromatics.getSpecialProductType())
							.methodologyAppliedDescription(aromatics.getMethodologyAppliedDescription())
							.hierarchicalOrder(aromatics.getHierarchicalOrder())
							.supportingFiles(aromatics.getSupportingFiles())
							.relevantCWTFunctions(aromatics.getRelevantCWTFunctions())
							.dataSources(aromatics.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case HYDROGEN ->  {
				if (source instanceof HydrogenSP hydrogen) {
					return HydrogenSP.builder()
							.specialProductType(hydrogen.getSpecialProductType())
							.methodologyAppliedDescription(hydrogen.getMethodologyAppliedDescription())
							.hierarchicalOrder(hydrogen.getHierarchicalOrder())
							.supportingFiles(hydrogen.getSupportingFiles())
							.dataSources(hydrogen.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case SYNTHESIS_GAS ->  {
				if (source instanceof SynthesisGasSP synthesisGas) {
					return SynthesisGasSP.builder()
							.specialProductType(synthesisGas.getSpecialProductType())
							.methodologyAppliedDescription(synthesisGas.getMethodologyAppliedDescription())
							.hierarchicalOrder(synthesisGas.getHierarchicalOrder())
							.supportingFiles(synthesisGas.getSupportingFiles())
							.dataSources(synthesisGas.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case ETHYLENE_OXIDE_ETHYLENE_GLYCOLS -> {
				if (source instanceof EthyleneOxideEthyleneGlycolsSP ethyleneOxideEthyleneGlycols) {
					return EthyleneOxideEthyleneGlycolsSP.builder()
							.specialProductType(ethyleneOxideEthyleneGlycols.getSpecialProductType())
							.methodologyAppliedDescription(ethyleneOxideEthyleneGlycols.getMethodologyAppliedDescription())
							.hierarchicalOrder(ethyleneOxideEthyleneGlycols.getHierarchicalOrder())
							.supportingFiles(ethyleneOxideEthyleneGlycols.getSupportingFiles())
							.dataSources(ethyleneOxideEthyleneGlycols.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			case VINYL_CHLORIDE_MONOMER -> {
				if (source instanceof VinylChlorideMonomerSP vinylChlorideMonomer) {
					return VinylChlorideMonomerSP.builder()
							.specialProductType(vinylChlorideMonomer.getSpecialProductType())
							.methodologyAppliedDescription(vinylChlorideMonomer.getMethodologyAppliedDescription())
							.hierarchicalOrder(vinylChlorideMonomer.getHierarchicalOrder())
							.supportingFiles(vinylChlorideMonomer.getSupportingFiles())
							.dataSources(vinylChlorideMonomer.getDataSources())
							.build();
				}
				throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
			}
			default -> throw new UnsupportedOperationException("Unknown SpecialProduct subtype: " + source.getClass().getName());
		}
	}

	default AnnualLevel mapAnnualLevel(AnnualLevel annualLevel) {
		switch (annualLevel.getAnnualLevelType()) {
			case PRODUCTION -> {
				if (annualLevel instanceof AnnualProductionLevel annualProductionLevel) {
					return AnnualProductionLevel.builder().
							annualLevelType(annualProductionLevel.getAnnualLevelType()).
							methodologyAppliedDescription(annualProductionLevel.getMethodologyAppliedDescription()).
							hierarchicalOrder(annualProductionLevel.getHierarchicalOrder()).
							trackingMethodologyDescription(annualProductionLevel.getTrackingMethodologyDescription()).
							supportingFiles(annualProductionLevel.getSupportingFiles()).
							quantityProductDataSources(annualProductionLevel.getQuantityProductDataSources()).
							annualQuantityDeterminationMethod(annualProductionLevel.getAnnualQuantityDeterminationMethod()).
							build();
				}
				throw new UnsupportedOperationException("Unknown Annual Level subtype: " + annualLevel.getClass().getName());
			}
			case ACTIVITY_HEAT -> {
				if (annualLevel instanceof AnnualActivityHeatLevel annualActivityHeatLevel) {
					return AnnualActivityHeatLevel.builder().
							annualLevelType(annualActivityHeatLevel.getAnnualLevelType()).
							methodologyAppliedDescription(annualActivityHeatLevel.getMethodologyAppliedDescription()).
							hierarchicalOrder(annualActivityHeatLevel.getHierarchicalOrder()).
							trackingMethodologyDescription(annualActivityHeatLevel.getTrackingMethodologyDescription()).
							supportingFiles(annualActivityHeatLevel.getSupportingFiles()).
							measurableHeatFlowList(annualActivityHeatLevel.getMeasurableHeatFlowList()).
							build();
				}
				throw new UnsupportedOperationException("Unknown Annual Level subtype: " + annualLevel.getClass().getName());
			}
			case ACTIVITY_FUEL -> {
				if (annualLevel instanceof AnnualActivityFuelLevel annualActivityFuelLevel) {
					return AnnualActivityFuelLevel.builder().
							annualLevelType(annualActivityFuelLevel.getAnnualLevelType()).
							methodologyAppliedDescription(annualActivityFuelLevel.getMethodologyAppliedDescription()).
							hierarchicalOrder(annualActivityFuelLevel.getHierarchicalOrder()).
							trackingMethodologyDescription(annualActivityFuelLevel.getTrackingMethodologyDescription()).
							supportingFiles(annualActivityFuelLevel.getSupportingFiles()).
							fuelDataSources(annualActivityFuelLevel.getFuelDataSources()).
							build();
				}
				throw new UnsupportedOperationException("Unknown Annual Level subtype: " + annualLevel.getClass().getName());
			}
			case ACTIVITY_PROCESS -> {
				if (annualLevel instanceof AnnualActivityProcessLevel annualActivityProcessLevel) {
					return AnnualActivityProcessLevel.builder().
							annualLevelType(annualActivityProcessLevel.getAnnualLevelType()).
							methodologyAppliedDescription(annualActivityProcessLevel.getMethodologyAppliedDescription()).
							hierarchicalOrder(annualActivityProcessLevel.getHierarchicalOrder()).
							trackingMethodologyDescription(annualActivityProcessLevel.getTrackingMethodologyDescription()).
							supportingFiles(annualActivityProcessLevel.getSupportingFiles()).
							build();
				}
				throw new UnsupportedOperationException("Unknown Annual Level subtype: " + annualLevel.getClass().getName());
			}
			default -> throw new UnsupportedOperationException("Unknown Annual Level subtype: " + annualLevel.getClass().getName());
		}
	}

	default DirectlyAttributableEmissions mapDirectlyAttributableEmissions(DirectlyAttributableEmissions source) {
		if (source == null)
			return null;
		switch (source.getDirectlyAttributableEmissionsType()) {
			case PRODUCT_BENCHMARK -> {
				if (source instanceof DirectlyAttributableEmissionsPB directlyAttributableEmissionsPB) {
					return DirectlyAttributableEmissionsPB.builder()
							.furtherInternalSourceStreamsRelevant(directlyAttributableEmissionsPB.isFurtherInternalSourceStreamsRelevant())
							.dataSources(directlyAttributableEmissionsPB.getDataSources())
							.methodologyAppliedDescription(directlyAttributableEmissionsPB.getMethodologyAppliedDescription())
							.transferredCO2ImportedOrExportedRelevant(directlyAttributableEmissionsPB.isTransferredCO2ImportedOrExportedRelevant())
							.amountsMonitoringDescription(directlyAttributableEmissionsPB.getAmountsMonitoringDescription())
							.directlyAttributableEmissionsType(directlyAttributableEmissionsPB.getDirectlyAttributableEmissionsType())
							.attribution(directlyAttributableEmissionsPB.getAttribution())
							.supportingFiles(directlyAttributableEmissionsPB.getSupportingFiles())
							.directlyAttributableEmissionsType(directlyAttributableEmissionsPB.getDirectlyAttributableEmissionsType())
							.build();
				}
				throw new UnsupportedOperationException("Unknown DirectlyAttributableEmissions subtype: " + source.getClass().getName());
			}
			case FALLBACK_APPROACH -> {
				if (source instanceof DirectlyAttributableEmissionsFA directlyAttributableEmissionsFA) {
					return DirectlyAttributableEmissionsFA.builder()
							.attribution(directlyAttributableEmissionsFA.getAttribution())
							.directlyAttributableEmissionsType(directlyAttributableEmissionsFA.getDirectlyAttributableEmissionsType())
							.supportingFiles(directlyAttributableEmissionsFA.getSupportingFiles())
							.directlyAttributableEmissionsType(directlyAttributableEmissionsFA.getDirectlyAttributableEmissionsType())
							.build();
				}
				throw new UnsupportedOperationException("Unknown DirectlyAttributableEmissions subtype: " + source.getClass().getName());
			}
			default -> throw new UnsupportedOperationException("Unknown DirectlyAttributableEmissions subtype: " + source.getClass().getName());
		}
	}

	default FuelInputAndRelevantEmissionFactor mapFuelInputAndRelevantEmissionFactor(FuelInputAndRelevantEmissionFactor source) {
		if (ObjectUtils.isEmpty(source)) return null;
		switch (source.getFuelInputAndRelevantEmissionFactorType()) {
			case PRODUCT_BENCHMARK -> {
				if (source instanceof FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor) {
					return FuelInputAndRelevantEmissionFactorPB.builder()
							.exist(fuelInputAndRelevantEmissionFactor.isExist())
							.dataSources(fuelInputAndRelevantEmissionFactor.getDataSources())
							.fuelInputAndRelevantEmissionFactorType(fuelInputAndRelevantEmissionFactor.getFuelInputAndRelevantEmissionFactorType())
							.hierarchicalOrder(fuelInputAndRelevantEmissionFactor.getHierarchicalOrder())
							.supportingFiles(fuelInputAndRelevantEmissionFactor.getSupportingFiles())
							.methodologyAppliedDescription(fuelInputAndRelevantEmissionFactor.getMethodologyAppliedDescription())
							.build();
				}
				throw new UnsupportedOperationException("Unknown FuelInputAndRelevantEmissionFactor subtype: " + source.getClass().getName());
			}
			case FALLBACK_APPROACH -> {
				if (source instanceof FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactor) {
					return FuelInputAndRelevantEmissionFactorFA.builder()
							.wasteGasesInput(fuelInputAndRelevantEmissionFactor.getWasteGasesInput())
							.dataSources(fuelInputAndRelevantEmissionFactor.getDataSources())
							.fuelInputAndRelevantEmissionFactorType(fuelInputAndRelevantEmissionFactor.getFuelInputAndRelevantEmissionFactorType())
							.hierarchicalOrder(fuelInputAndRelevantEmissionFactor.getHierarchicalOrder())
							.supportingFiles(fuelInputAndRelevantEmissionFactor.getSupportingFiles())
							.methodologyAppliedDescription(fuelInputAndRelevantEmissionFactor.getMethodologyAppliedDescription())
							.build();
				}
				throw new UnsupportedOperationException("Unknown FuelInputAndRelevantEmissionFactor subtype: " + source.getClass().getName());
			}
			case HEAT_FALLBACK_APPROACH -> {
				if (source instanceof FuelInputAndRelevantEmissionFactorHeatFA fuelInputAndRelevantEmissionFactor) {
					return FuelInputAndRelevantEmissionFactorHeatFA.builder()
							.exists(fuelInputAndRelevantEmissionFactor.isExists())
							.wasteGasesInput(fuelInputAndRelevantEmissionFactor.getWasteGasesInput())
							.dataSources(fuelInputAndRelevantEmissionFactor.getDataSources())
							.fuelInputAndRelevantEmissionFactorType(fuelInputAndRelevantEmissionFactor.getFuelInputAndRelevantEmissionFactorType())
							.hierarchicalOrder(fuelInputAndRelevantEmissionFactor.getHierarchicalOrder())
							.supportingFiles(fuelInputAndRelevantEmissionFactor.getSupportingFiles())
							.methodologyAppliedDescription(fuelInputAndRelevantEmissionFactor.getMethodologyAppliedDescription())
							.build();
				}
				throw new UnsupportedOperationException("Unknown FuelInputAndRelevantEmissionFactor subtype: " + source.getClass().getName());
			}
			default -> throw new UnsupportedOperationException("Unknown FuelInputAndRelevantEmissionFactor subtype: " + source.getClass().getName());
		}
	}
}

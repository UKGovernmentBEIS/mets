package uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationError;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationException;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.WorksheetNames;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.ImportedMeasurableHeatFlow;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationCategory;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevelType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualProductionLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelandelectricityexchangeability.FuelAndElectricityExchangeability;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WorksheetMigrationFProductBM implements WorksheetMigrationImplementor {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public WorksheetNames getWorksheetname() {
        return WorksheetNames.F_PRODUCT_BM;
    }

    @Override
    public List<String> migrateWorksheet(Workbook workbook, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString,
                                         String fileUuid, DigitizedPlan digitizedPlan) {
        Sheet sheet = workbook.getSheet(this.getWorksheetname().getName());
        return this.doMigrate_fProductBM(sheet, accountIdString, fileUuid, digitizedPlan);
    }

    private List<String> doMigrate_fProductBM(Sheet sheet, String accountIdString, String fileUuid,
                                              DigitizedPlan digitizedPlan) {
        List<String> results = new ArrayList<>();

        try {
            for (int i = 0; i < digitizedPlan.getSubInstallations().size(); i++) {
                SubInstallation subInstallation = digitizedPlan.getSubInstallations().get(i);

                if (ObjectUtils.isEmpty(subInstallation.getSubInstallationType()))
                    continue;

                if (!Objects.equals(subInstallation.getSubInstallationType().getCategory(), SubInstallationCategory.PRODUCT_BENCHMARK))
                    //Product BMs are migrated and appear first on the list.
                    break;

                //SubInstallation description
                int descriptionBaseRow = (i == 0) ? 43 : (i == 1) ? 316 : (316 + (i - 1) * 204);
                subInstallation.setDescription(DigitizedMmpMigrationUtils.concatenateSubInstallationDescription(sheet,"E" + descriptionBaseRow));

                //SubInstallation Annual Production Level
                int APL_dataSourcesBaseRow = (i == 0) ? 60 : (i == 1) ? 327 : (327 + (i - 1) * 204);
                int APL_determinationMethodBaseRow = APL_dataSourcesBaseRow + 2;
                int APL_descriptionBaseRow = (i == 0) ? 71 : (i == 1) ? 336 : (336 + (i - 1) * 204);
                int APL_HO_flagAndReason = APL_descriptionBaseRow + 4;
                int APL_HO_details = (i == 0) ? 82 : (i == 1) ? 343 : (343 + (i - 1) * 204);
                int APL_trackingDesc = APL_HO_details + 4;

                subInstallation.setAnnualLevel(AnnualProductionLevel.builder()
                        .annualLevelType(AnnualLevelType.PRODUCTION)
                        .quantityProductDataSources(DigitizedMmpMigrationUtils.subInstallation_APL_dataSources(sheet, "I" + APL_dataSourcesBaseRow))
                        .annualQuantityDeterminationMethod(DigitizedMmpMigrationUtils.subInstallation_APL_determinationMethod(sheet, "I" + APL_determinationMethodBaseRow))
                        .methodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + APL_descriptionBaseRow))
                        .hierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + APL_HO_flagAndReason, "K" + APL_HO_flagAndReason, "F" + APL_HO_details))
                        .trackingMethodologyDescription(DigitizedMmpMigrationUtils.getStringValueOfCellByPosition(sheet, "F" + APL_trackingDesc)).build());

                //SubInstallation Exchangeability of Fuel and Electricity
                int EoFaE_dataSourceBaseRow = (i == 0) ? 98 : (i == 1) ? 353 : (353 + (i - 1) * 204);
                int EoFaE_descriptionBaseRow = EoFaE_dataSourceBaseRow + 6;
                int EoFaE_flagAndReason = EoFaE_descriptionBaseRow + 4;
                int EoFaE_HO_details = (i == 0) ? 115 : (i == 1) ? 366 : (366 + (i - 1) * 204);


                FuelAndElectricityExchangeability fuelAndElectricityExchangeability = FuelAndElectricityExchangeability.builder()
                        .fuelAndElectricityExchangeabilityEnergyFlowDataSources(DigitizedMmpMigrationUtils.subInstallation_EoFaE_dataSources(sheet, "I" + EoFaE_dataSourceBaseRow))
                        .methodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + EoFaE_descriptionBaseRow))
                        .hierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet,"I" + EoFaE_flagAndReason, "K" + EoFaE_flagAndReason, "F" +EoFaE_HO_details))
                        .build();

                // Check if any field is populated to confirm the existence of FuelAndElectricityExchangeability data
                boolean fuelAndElectricityExchangeabilityExists = !ObjectUtils.isEmpty(fuelAndElectricityExchangeability.getFuelAndElectricityExchangeabilityEnergyFlowDataSources()) ||
                        !ObjectUtils.isEmpty(fuelAndElectricityExchangeability.getMethodologyAppliedDescription()) ||
                        !ObjectUtils.isEmpty(fuelAndElectricityExchangeability.getHierarchicalOrder());

                if (fuelAndElectricityExchangeabilityExists)
                    subInstallation.setFuelAndElectricityExchangeability(fuelAndElectricityExchangeability);

                //SubInstallation Imported Measurable Heat Flows
                int IMHF_flagBaseRow = (i == 0) ? 118 : (i == 1) ? 369 : (369 + (i - 1) * 204);
                int IMHF_descriptionBaseRow = (i == 0) ? 127 : (i == 1) ? 375 : (375 + (i - 1) * 204);

                ImportedMeasurableHeatFlow importedMeasurableHeatFlow = ImportedMeasurableHeatFlow.builder()
                        .exist(DigitizedMmpMigrationUtils.getBooleanValueOfCellByPosition(sheet, "M" + IMHF_flagBaseRow))
                        .methodologyAppliedDescription(DigitizedMmpMigrationUtils.getStringValueOfCellByPosition(sheet, "F" + IMHF_descriptionBaseRow))
                        .build();

                subInstallation.setImportedMeasurableHeatFlow(importedMeasurableHeatFlow);

                //SubInstallation Directly Attributable Emissions
                int DAE_attribution = (i == 0) ? 141 : (i == 1) ? 385 : (385 + (i - 1) * 204);
                int DAE_furtherInternalSourceStreamsRelevant = DAE_attribution + 4;
                int DAE_dataSources =(i == 0) ? 152 : (i == 1) ? 392 : (392 + (i - 1) * 204);
                int DAE_methodologyAppliedDescription = (i == 0) ? 161 : (i == 1) ? 401 : (401 + (i - 1) * 204);
                int DAE_transferredCO2ImportedOrExportedRelevant = DAE_methodologyAppliedDescription + 4;
                int DAE_amountsMonitoringDescription = (i == 0) ? 168 : (i == 1) ? 407 : (407 + (i - 1) * 204);



                DirectlyAttributableEmissionsPB directlyAttributableEmissionsPB = DirectlyAttributableEmissionsPB.builder()
                        .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                        .attribution(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + DAE_attribution))
                        .furtherInternalSourceStreamsRelevant(DigitizedMmpMigrationUtils.getBooleanValueOfCellByPosition(sheet, "M" + DAE_furtherInternalSourceStreamsRelevant))
                        .dataSources(DigitizedMmpMigrationUtils.subInstallation_DAE_dataSources(sheet, "I" + DAE_dataSources))
                        .methodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + DAE_methodologyAppliedDescription))
                        .transferredCO2ImportedOrExportedRelevant(DigitizedMmpMigrationUtils.getBooleanValueOfCellByPosition(sheet, "M" + DAE_transferredCO2ImportedOrExportedRelevant))
                        .amountsMonitoringDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + DAE_amountsMonitoringDescription))
                        .build();

                subInstallation.setDirectlyAttributableEmissions(directlyAttributableEmissionsPB);

                //SubInstallation Fuel Input and Relevant Emission Factor
                int FIaREF_dataSources = (i == 0) ? 183 : (i == 1) ? 415 : (415 + (i - 1) * 204);
                int FIaREF_description = FIaREF_dataSources + 7;
                int FIaREF_HO_flagAndReason = FIaREF_description + 4;
                int FIaREF_HO_details = (i == 0) ? 201 : (i == 1) ? 429 : (429 + (i - 1) * 204);


                FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder()
                        .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK)
                        .dataSources(DigitizedMmpMigrationUtils.subInstallation_FIaREF_dataSources(sheet, "I" + FIaREF_dataSources))
                        .methodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + FIaREF_description))
                        .hierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + FIaREF_HO_flagAndReason, "K" + FIaREF_HO_flagAndReason, "F" + FIaREF_HO_details))
                        .build();

                // Check if any field is populated to confirm the existence of FuelInputAndRelevantEmissionFactor data
                boolean fuelInputAndRelevantEmissionFactorExists = !ObjectUtils.isEmpty(fuelInputAndRelevantEmissionFactor.getDataSources()) ||
                        !ObjectUtils.isEmpty(fuelInputAndRelevantEmissionFactor.getMethodologyAppliedDescription()) ||
                        !ObjectUtils.isEmpty(fuelInputAndRelevantEmissionFactor.getHierarchicalOrder());

                fuelInputAndRelevantEmissionFactor.setExist(fuelInputAndRelevantEmissionFactorExists);

                subInstallation.setFuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor);

                //SubInstallation Imported and Exported Measurable Heat
                int IaEMH_isRelevant_baseRow = (i == 0) ? 207 : (i == 1) ? 433 : (433 + (i - 1) * 204);
                int IaEMH_dataSources_baseRow = (i == 0) ? 216 : (i == 1) ? 438 : (438 + (i - 1) * 204);
                int IaEMH_DS_methodologyDesc_baseRow = IaEMH_dataSources_baseRow + 10;
                int IaEMH_HO_flagAndReason_baseRow = IaEMH_DS_methodologyDesc_baseRow + 4;
                int IaEMH_HO_details_baseRow = (i == 0) ? 237 : (i == 1) ? 455 : (455 + (i - 1) * 204);
                int IaEMH_methodologyDeterminationDesc_baseRow = (i == 0) ? 245 : (i == 1) ? 461 : (461 + (i - 1) * 204);
                int IaEMH_pulpRelevant_baseRow = IaEMH_methodologyDeterminationDesc_baseRow + 4;
                int IaEMH_pulpDesc_baseRow = IaEMH_pulpRelevant_baseRow + 6;

                ImportedExportedMeasurableHeat.ImportedExportedMeasurableHeatBuilder importedExportedMeasurableHeatBuilder = ImportedExportedMeasurableHeat.builder();
                DigitizedMmpMigrationUtils.subInstallation_IaEMH_fuelBurnCalculationTypes_dataSources(sheet, importedExportedMeasurableHeatBuilder, "I" + IaEMH_dataSources_baseRow, "M" + IaEMH_isRelevant_baseRow);

                ImportedExportedMeasurableHeat importedExportedMeasurableHeat = importedExportedMeasurableHeatBuilder
                        .dataSourcesMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + IaEMH_DS_methodologyDesc_baseRow))
                        .hierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet,"I" + IaEMH_HO_flagAndReason_baseRow, "K" + IaEMH_HO_flagAndReason_baseRow, "F" + IaEMH_HO_details_baseRow))
                        .methodologyDeterminationDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + IaEMH_methodologyDeterminationDesc_baseRow))
                        .measurableHeatImportedFromPulp(DigitizedMmpMigrationUtils.getBooleanValueOfCellByPosition(sheet, "M" + IaEMH_pulpRelevant_baseRow))
                        .pulpMethodologyDeterminationDescription(DigitizedMmpMigrationUtils.getStringValueOfCellByPosition(sheet, "F" + IaEMH_pulpDesc_baseRow))
                        .build();

                subInstallation.setImportedExportedMeasurableHeat(importedExportedMeasurableHeat);

                //SubInstallation Waste Gas Balance
                int WGB_isRelevant_baseRow = (i == 0) ? 261 : (i == 1) ? 475 : (475 + (i - 1) * 204);
                int WGB_dataSources_baseRow = (i == 0) ? 270 : (i == 1) ? 480 : (480 + (i - 1) * 204);
                int WGB_methodologyAppliedDescription_baseRow = (i == 0) ? 292 : (i == 1) ? 500 : (500 + (i - 1) * 204);
                int WGB_HO_flagAndReason_baseRow = WGB_methodologyAppliedDescription_baseRow + 4;
                int WGB_HO_details_baseRow = (i == 0) ? 303 : (i == 1) ? 507 : (507 + (i - 1) * 204);

                WasteGasBalance.WasteGasBalanceBuilder wasteGasBalanceBuilder = WasteGasBalance.builder();
                DigitizedMmpMigrationUtils.subInstallation_WGB_wasteGasActivities_dataSources(sheet, wasteGasBalanceBuilder, "I" + WGB_dataSources_baseRow, "M" + WGB_isRelevant_baseRow);

                WasteGasBalance wasteGasBalance = wasteGasBalanceBuilder
                        .dataSourcesMethodologyAppliedDescription(DigitizedMmpMigrationUtils.subInstallation_generalDescription(sheet, "F" + WGB_methodologyAppliedDescription_baseRow))
                        .hierarchicalOrder(DigitizedMmpMigrationUtils.subInstallation_HierarchicalOrder(sheet, "I" + WGB_HO_flagAndReason_baseRow, "K" + WGB_HO_flagAndReason_baseRow, "F" + WGB_HO_details_baseRow))
                        .build();

                subInstallation.setWasteGasBalance(wasteGasBalance);
            }
        } catch (DigitizedMmpMigrationException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .fileUuid(fileUuid).worksheetName(sheet.getSheetName()).errorReport(e.getMessage()).build()));
        }

        return results;
    }
}

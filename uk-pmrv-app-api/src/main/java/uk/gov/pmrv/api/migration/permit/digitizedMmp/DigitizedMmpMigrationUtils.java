package uk.gov.pmrv.api.migration.permit.digitizedMmp;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellReference;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.*;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.*;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevelType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.*;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualQuantityDeterminationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.QuantityProductDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.ImportedExportedAmountsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelandelectricityexchangeability.FuelAndElectricityExchangeabilityEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.*;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExportedDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedDataSourceDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.produced.MeasurableHeatProduced;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.produced.MeasurableHeatProducedDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasActivity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalance;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalanceEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalanceEnergyFlowDataSourceDetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

public class DigitizedMmpMigrationUtils {

    public static String getStringValueOfCell(CellReference cr, Sheet worksheet, FormulaEvaluator evaluator) {
        Cell cell = getCellByCellRefAndWorksheet(cr, worksheet);

        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Format numbers with or without decimals
                    DecimalFormat df = new DecimalFormat("#.########");
                    return df.format(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluate the formula and get the result based on the formula type
                CellValue evaluatedValue = evaluator.evaluate(cell);
                return switch (evaluatedValue.getCellType()) {
                    case STRING -> evaluatedValue.getStringValue();
                    case NUMERIC -> {
                        // Format numeric results from formulas
                        DecimalFormat df = new DecimalFormat("#.########");
                        yield df.format(evaluatedValue.getNumberValue());
                    }
                    case BOOLEAN -> String.valueOf(evaluatedValue.getBooleanValue());
                    default -> "";
                };
            default:
                return "";
        }
    }

    public static boolean getCellValueBoolean(CellReference cr, Sheet worksheet) throws DigitizedMmpMigrationException {
        Cell cell = getCellByCellRefAndWorksheet(cr, worksheet);

        if (cell.getCellType() != CellType.BOOLEAN && cell.getCellType() != CellType.BLANK) {
            throw new DigitizedMmpMigrationException("Cell (" + cr.getRow() + "," + cr.getCol() + ") of worksheet '" + worksheet.getSheetName() + "' is not a boolean!");
        }
        return cell.getBooleanCellValue();
    }

    public static Boolean getNullableCellValueBoolean(CellReference cr, Sheet worksheet) throws DigitizedMmpMigrationException {
        Cell cell = getCellByCellRefAndWorksheet(cr, worksheet);

        if (cell==null || cell.getCellType()==CellType.BLANK) {
            return null;
        }

        if (cell.getCellType() != CellType.BOOLEAN) {
            throw new DigitizedMmpMigrationException("Cell (" + cr.getRow() + "," + cr.getCol() + ") of worksheet '" + worksheet.getSheetName() + "' is not a boolean!");
        }
        return cell.getBooleanCellValue();
    }


    public static Cell getCellByCellRefAndWorksheet(CellReference cr, Sheet worksheet) {
        return worksheet.getRow(cr.getRow()).getCell(cr.getCol());
    }

    public static CellReference getNextRow (CellReference cr) {
        return new CellReference(cr.getRow() + 1, cr.getCol());
    }

    public static CellReference getNextColumn (CellReference cr) {
        return new CellReference(cr.getRow(), cr.getCol() + 1);
    }

    public static String buildErrorMessage(final DigitizedMmpMigrationError migrationError) {
        return String.format("accountId: '%s' | fileUUID: '%s' | Worksheet '%s' | %s",
            migrationError.getAccountId(),
            migrationError.getFileUuid(),
            migrationError.getWorksheetName(),
            migrationError.getErrorReport()
        );
    }

    public static String getStringValueOfCellByPosition(Sheet sheet, String cell) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference cr = new CellReference(cell);
        return DigitizedMmpMigrationUtils.getStringValueOfCell(cr, sheet, evaluator);
    }

    public static boolean getBooleanValueOfCellByPosition(Sheet sheet, String cell) throws DigitizedMmpMigrationException {

        CellReference cr = new CellReference(cell);
        return DigitizedMmpMigrationUtils.getCellValueBoolean(cr, sheet);
    }

    public static Boolean getNullableBooleanValueOfCellByPosition(Sheet sheet, String cell) throws DigitizedMmpMigrationException {

        CellReference cr = new CellReference(cell);
        return DigitizedMmpMigrationUtils.getNullableCellValueBoolean(cr, sheet);
    }

    public static String concatenateSubInstallationDescription(Sheet sheet, String startCell) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crDesc = new CellReference(startCell);
        String descVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crDesc, sheet, evaluator);

        if (!hasText(descVal)) {
            return Strings.EMPTY;
        }

        StringBuilder description = new StringBuilder(descVal);

        CellReference crExtFilesReference = new CellReference(crDesc.getRow() + 2, crDesc.getCol() + 6);
        String extFilesReferenceVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crExtFilesReference, sheet, evaluator);

        CellReference crFDReference = new CellReference(crDesc.getRow() + 4, crDesc.getCol() + 6);
        String FDReferenceVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crFDReference, sheet, evaluator);

        if (hasText(extFilesReferenceVal)) {
            description.append("\r\r\n")
                    .append("Reference to external files, if relevant: ")
                    .append(extFilesReferenceVal);
        }

        if (hasText(FDReferenceVal)) {
            description.append("\r\r\n")
                    .append("Reference to a separate detailed flow diagram, if relevant: ")
                    .append(FDReferenceVal);
        }

        return description.toString();
    }

    public static List<QuantityProductDataSource> subInstallation_APL_dataSources(Sheet sheet, String startCell) {
        List<QuantityProductDataSource> dataSources = new ArrayList<>();

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        CellReference crDS1 = new CellReference(startCell);

        for (int i = 0; i < 3; i++) {
            CellReference crDS = i == 0 ? crDS1 : new CellReference(crDS1.getRow(), crDS1.getCol() + (i*2) );

            String DSVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crDS, sheet, evaluator);

            if (!hasText(DSVal)) {
                continue;
            }
            dataSources.add(QuantityProductDataSource.builder()
                    .quantityProductDataSourceNo(String.valueOf(i))
                    .quantityProduct(AnnexVIISection44.getByValue(DSVal))
                    .build());
        }
        return dataSources;
    }

    public static AnnualQuantityDeterminationMethod subInstallation_APL_determinationMethod(Sheet sheet, String startCell) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crDM = new CellReference(startCell);
        String DMVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crDM, sheet, evaluator);

        return AnnualQuantityDeterminationMethod.getByValue(DMVal);
    }

    public static String subInstallation_generalDescription(Sheet sheet, String startCell) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crMAD = new CellReference(startCell);
        String MADVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crMAD, sheet, evaluator);

        StringBuilder description = new StringBuilder(MADVal);

        CellReference crExtFilesReference = new CellReference(crMAD.getRow() + 2, crMAD.getCol() + 5);
        String extFilesReferenceVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crExtFilesReference, sheet, evaluator);

        if (hasText(extFilesReferenceVal)) {
            description.append("\r\r\n")
                    .append("Reference to external files, if relevant: ")
                    .append(extFilesReferenceVal);
        }

        return description.toString();
    }

    public static SubInstallationHierarchicalOrder subInstallation_HierarchicalOrder(Sheet sheet, String flagStartCell, String reasonStartCell, String detailsStartCell) throws DigitizedMmpMigrationException {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crFlag = new CellReference(flagStartCell);
        boolean flagValue = DigitizedMmpMigrationUtils.getCellValueBoolean(crFlag, sheet);

        CellReference crReason = new CellReference(reasonStartCell);
        String reasonValue = DigitizedMmpMigrationUtils.getStringValueOfCell(crReason, sheet, evaluator);

        CellReference crDetails = new CellReference(detailsStartCell);
        String detailsValue = DigitizedMmpMigrationUtils.getStringValueOfCell(crDetails, sheet, evaluator);

        if (!flagValue && !hasText(reasonValue) && !hasText(detailsValue))
            return null;

        return SubInstallationHierarchicalOrder.builder()
                .followed(flagValue)
                .notFollowingHierarchicalOrderReason(NotFollowingHierarchicalOrderReason.getByValue(reasonValue))
                .notFollowingHierarchicalOrderDescription(detailsValue)
                .build();
    }

    public static List<FuelAndElectricityExchangeabilityEnergyFlowDataSource> subInstallation_EoFaE_dataSources(Sheet sheet, String startCell) {
        List<FuelAndElectricityExchangeabilityEnergyFlowDataSource> dataSources = new ArrayList<>();

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crDS1 = new CellReference(startCell);

        for (int i = 0; i < 3; i++) {
            CellReference crDS = i == 0 ? crDS1 : new CellReference(crDS1.getRow(), crDS1.getCol() + (i*2) );

            String DSVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crDS, sheet, evaluator);

            if (!hasText(DSVal)) {
                continue;
            }
            dataSources.add(FuelAndElectricityExchangeabilityEnergyFlowDataSource.builder()
                    .energyFlowDataSourceNo(String.valueOf(i))
                    .relevantElectricityConsumption(AnnexVIISection45.getByValue(DSVal))
                    .build());
        }
        return dataSources;
    }

    public static List<ImportedExportedAmountsDataSource> subInstallation_DAE_dataSources(Sheet sheet, String startCell) {
        List<ImportedExportedAmountsDataSource> dataSources = new ArrayList<>();

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crAmounts1 = new CellReference(startCell);

        for (int i = 0; i < 3; i++) {
            CellReference crAmounts = i == 0 ? crAmounts1 : new CellReference(crAmounts1.getRow(), crAmounts1.getCol() + (i*2) );

            String amountsVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crAmounts, sheet, evaluator);

            CellReference crEnergyContent = new CellReference(crAmounts.getRow() + 1, crAmounts.getCol());
            String energyContentVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crEnergyContent, sheet, evaluator);

            CellReference crEmissionFactorOrCarbonContent = new CellReference(crEnergyContent.getRow() + 1, crAmounts.getCol());
            String emissionFactorOrCarbonContentVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crEmissionFactorOrCarbonContent, sheet, evaluator);

            CellReference crBiomassContent = new CellReference(crEmissionFactorOrCarbonContent.getRow() + 1, crAmounts.getCol());
            String biomassContentVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crBiomassContent, sheet, evaluator);

            ImportedExportedAmountsDataSource importedExportedAmountsDataSource = ImportedExportedAmountsDataSource.builder()
                    .importedExportedAmountsDataSourceNo(String.valueOf(i))
                    .amounts(AnnexVIISection44.getByValue(amountsVal))
                    .energyContent(AnnexVIISection46.getByValue(energyContentVal))
                    .emissionFactorOrCarbonContent(AnnexVIISection46.getByValue(emissionFactorOrCarbonContentVal))
                    .biomassContent(AnnexVIISection46.getByValue(biomassContentVal))
                    .build();

            // Check if any field is populated to confirm the existence of ImportedExportedAmountsDataSource data
            boolean dataSourceExists = !ObjectUtils.isEmpty(importedExportedAmountsDataSource.getAmounts()) ||
                    !ObjectUtils.isEmpty(importedExportedAmountsDataSource.getEnergyContent()) ||
                    !ObjectUtils.isEmpty(importedExportedAmountsDataSource.getEmissionFactorOrCarbonContent()) ||
                    !ObjectUtils.isEmpty(importedExportedAmountsDataSource.getBiomassContent());

            if (dataSourceExists)
                dataSources.add(importedExportedAmountsDataSource);
        }
        return dataSources;

    }

    public static List<FuelInputDataSourcePB> subInstallation_FIaREF_dataSources(Sheet sheet, String startCell) {
        List<FuelInputDataSourcePB> dataSources = new ArrayList<>();

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crFuelInput1 = new CellReference(startCell);

        for (int i = 0; i < 3; i++) {
            CellReference crFuelInput = i == 0 ? crFuelInput1 : new CellReference(crFuelInput1.getRow(), crFuelInput1.getCol() + (i*2) );

            String fuelInputVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crFuelInput, sheet, evaluator);

            CellReference crWEF = new CellReference(crFuelInput.getRow() + 1, crFuelInput.getCol());
            String WEFVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crWEF, sheet, evaluator);

            FuelInputDataSourcePB fuelInputDataSourcePB = FuelInputDataSourcePB.builder()
                    .fuelInputDataSourceNo(String.valueOf(i))
                    .fuelInput(AnnexVIISection44.getByValue(fuelInputVal))
                    .weightedEmissionFactor(AnnexVIISection46.getByValue(WEFVal))
                    .build();

            // Check if any field is populated to confirm the existence of FuelInputDataSource data
            boolean dataSourceExists = !ObjectUtils.isEmpty(fuelInputDataSourcePB.getFuelInput()) ||
                    !ObjectUtils.isEmpty(fuelInputDataSourcePB.getWeightedEmissionFactor());

            if (dataSourceExists) {
                dataSources.add(fuelInputDataSourcePB);
            }
        }
        return dataSources;

    }

    public static void subInstallation_IaEMH_fuelBurnCalculationTypes_dataSources(Sheet sheet,ImportedExportedMeasurableHeat.ImportedExportedMeasurableHeatBuilder importedExportedMeasurableHeatBuilder, String DSstartCell, String isRelevantPosition) throws DigitizedMmpMigrationException {
        List<ImportedExportedMeasurableHeatEnergyFlowDataSource> dataSources = new ArrayList<>();
        Set<ImportedExportedMeasurableHeatType> heatTypes = new HashSet<>();

        boolean isRelevant = getBooleanValueOfCellByPosition(sheet, isRelevantPosition);
        if (!isRelevant)
            heatTypes.add(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT);

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crFuelInput1 = new CellReference(DSstartCell);

        for (int i = 0; i < 3; i++) {
            CellReference crFuelInput = i == 0 ? crFuelInput1 : new CellReference(crFuelInput1.getRow(), crFuelInput1.getCol() + (i*2) );

            String importedVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crFuelInput, sheet, evaluator);
            if (Strings.isNotBlank(importedVal))
                heatTypes.add(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_IMPORTED);

            CellReference crFromPulp = new CellReference(crFuelInput.getRow() + 1, crFuelInput.getCol());
            String fromPulpVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crFromPulp, sheet, evaluator);
            if (Strings.isNotBlank(fromPulpVal))
                heatTypes.add(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_FROM_PULP);

            CellReference crFromNitricAcid = new CellReference(crFuelInput.getRow() + 2, crFuelInput.getCol());
            String fromNitricAcidVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crFromNitricAcid, sheet, evaluator);
            if (Strings.isNotBlank(fromNitricAcidVal))
                heatTypes.add(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_FROM_NITRIC_ACID);

            CellReference crExported = new CellReference(crFuelInput.getRow() + 3, crFuelInput.getCol());
            String exportedVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crExported, sheet, evaluator);
            if (Strings.isNotBlank(exportedVal))
                heatTypes.add(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_EXPORTED);

            CellReference crNet = new CellReference(crFuelInput.getRow() + 4, crFuelInput.getCol());
            String netVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crNet, sheet, evaluator);

            ImportedExportedMeasurableHeatEnergyFlowDataSource importedExportedMeasurableHeatEnergyFlowDataSource = ImportedExportedMeasurableHeatEnergyFlowDataSource.builder()
                    .energyFlowDataSourceNo(String.valueOf(i))
                    .measurableHeatImported(AnnexVIISection45.getByValue(importedVal))
                    .measurableHeatPulp(AnnexVIISection45.getByValue(fromPulpVal))
                    .measurableHeatNitricAcid(AnnexVIISection45.getByValue(fromNitricAcidVal))
                    .measurableHeatExported(AnnexVIISection45.getByValue(exportedVal))
                    .netMeasurableHeatFlows(AnnexVIISection72.getByValue(netVal))
                    .build();

            // Check if any field is populated to confirm the existence of ImportedExportedMeasurableHeatEnergyFlowDataSource data
            boolean dataSourceExists = !ObjectUtils.isEmpty(importedExportedMeasurableHeatEnergyFlowDataSource.getMeasurableHeatImported()) ||
                    !ObjectUtils.isEmpty(importedExportedMeasurableHeatEnergyFlowDataSource.getMeasurableHeatPulp()) ||
                    !ObjectUtils.isEmpty(importedExportedMeasurableHeatEnergyFlowDataSource.getMeasurableHeatNitricAcid()) ||
                    !ObjectUtils.isEmpty(importedExportedMeasurableHeatEnergyFlowDataSource.getMeasurableHeatExported()) ||
                    !ObjectUtils.isEmpty(importedExportedMeasurableHeatEnergyFlowDataSource.getNetMeasurableHeatFlows());

            if (dataSourceExists) {
                dataSources.add(importedExportedMeasurableHeatEnergyFlowDataSource);
            }
        }

        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getEnergyFlowDataSourceNo(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("ImportedExportedMeasurableHeatEnergyFlowDataSource are not ordered!");

        importedExportedMeasurableHeatBuilder
                .fuelBurnCalculationTypes(heatTypes)
                .dataSources(dataSources);
    }


    public static String subInstallation_methodologyAppliedDescription_SP(Sheet sheet, String startCell) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crMAD = new CellReference(startCell);
        String MADVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crMAD, sheet, evaluator);

        StringBuilder description = new StringBuilder(MADVal);

        CellReference crExtFilesReference = new CellReference(crMAD.getRow() + 2, crMAD.getCol() + 6);
        String extFilesReferenceVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crExtFilesReference, sheet, evaluator);

        if (hasText(extFilesReferenceVal)) {
            description.append("\r\r\n")
                    .append("Reference to external files, if relevant: ")
                    .append(extFilesReferenceVal);
        }

        return description.toString();
    }

    public static void subInstallation_WGB_wasteGasActivities_dataSources(Sheet sheet, WasteGasBalance.WasteGasBalanceBuilder wasteGasBalanceBuilderBuilder, String DSstartCell, String isRelevantPosition) throws DigitizedMmpMigrationException {
        List<WasteGasBalanceEnergyFlowDataSource> dataSources = new ArrayList<>();
        Set<WasteGasActivity> wasteGasActivities = new HashSet<>();

        boolean isRelevant = getBooleanValueOfCellByPosition(sheet, isRelevantPosition);
        if (!isRelevant)
            wasteGasActivities.add(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES);

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        CellReference crWGActivity1 = new CellReference(DSstartCell);

        final int NUMBER_OF_ITERATIONS = 3;
        final int OFFSET_PRODUCED = 0;
        final int OFFSET_CONSUMED = 3;
        final int OFFSET_FLARED = 6;
        final int OFFSET_IMPORTED = 9;
        final int OFFSET_EXPORTED = 12;

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Map<WasteGasActivity, WasteGasBalanceEnergyFlowDataSourceDetails> wasteGasActivitiesMap = new HashMap<>();

            CellReference baseCellReference = (i == 0) ? crWGActivity1 : new CellReference(crWGActivity1.getRow(), crWGActivity1.getCol() + (i * 2));

            processWasteGasActivity(wasteGasActivitiesMap, wasteGasActivities, baseCellReference, OFFSET_PRODUCED,
                    WasteGasActivity.WASTE_GAS_PRODUCED, sheet, evaluator);
            processWasteGasActivity(wasteGasActivitiesMap, wasteGasActivities, baseCellReference, OFFSET_CONSUMED,
                    WasteGasActivity.WASTE_GAS_CONSUMED, sheet, evaluator);
            processWasteGasActivity(wasteGasActivitiesMap, wasteGasActivities, baseCellReference, OFFSET_FLARED,
                    WasteGasActivity.WASTE_GAS_FLARED, sheet, evaluator);
            processWasteGasActivity(wasteGasActivitiesMap, wasteGasActivities, baseCellReference, OFFSET_IMPORTED,
                    WasteGasActivity.WASTE_GAS_IMPORTED, sheet, evaluator);
            processWasteGasActivity(wasteGasActivitiesMap, wasteGasActivities, baseCellReference, OFFSET_EXPORTED,
                    WasteGasActivity.WASTE_GAS_EXPORTED, sheet, evaluator);

            WasteGasBalanceEnergyFlowDataSource dataSource = WasteGasBalanceEnergyFlowDataSource.builder()
                    .energyFlowDataSourceNo(String.valueOf(i))
                    .wasteGasActivityDetails(wasteGasActivitiesMap)
                    .build();

            //check the existence of data source by checking if getWasteGasActivityDetails map is empty
            if (!ObjectUtils.isEmpty(dataSource.getWasteGasActivityDetails())) {
                dataSources.add(dataSource);
            }
        }
        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getEnergyFlowDataSourceNo(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("WasteGasBalanceEnergyFlowDataSources are not ordered!");

        wasteGasBalanceBuilderBuilder
                .wasteGasActivities(wasteGasActivities)
                .dataSources(dataSources);
    }

    private static void processWasteGasActivity(Map<WasteGasActivity, WasteGasBalanceEnergyFlowDataSourceDetails> wasteGasActivitiesMap, Set<WasteGasActivity> wasteGasActivities, CellReference baseCellReference,
            int offset, WasteGasActivity activityType, Sheet sheet, FormulaEvaluator evaluator) {

        CellReference crEntity = new CellReference(baseCellReference.getRow() + offset, baseCellReference.getCol());
        CellReference crEnergyContent = new CellReference(baseCellReference.getRow() + offset + 1, baseCellReference.getCol());
        CellReference crEmissionFactor = new CellReference(baseCellReference.getRow() + offset + 2, baseCellReference.getCol());

        String entityVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crEntity, sheet, evaluator);
        String energyContentVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crEnergyContent, sheet, evaluator);
        String emissionFactorVal = DigitizedMmpMigrationUtils.getStringValueOfCell(crEmissionFactor, sheet, evaluator);

        if (Stream.of(entityVal, energyContentVal, emissionFactorVal).anyMatch(Strings::isNotBlank)) {
            WasteGasBalanceEnergyFlowDataSourceDetails details = WasteGasBalanceEnergyFlowDataSourceDetails.builder()
                    .entity(Strings.isNotBlank(entityVal) ? AnnexVIISection44.getByValue(entityVal) : null)
                    .energyContent(Strings.isNotBlank(energyContentVal) ? AnnexVIISection46.getByValue(energyContentVal) : null)
                    .emissionFactor(Strings.isNotBlank(emissionFactorVal) ? AnnexVIISection46.getByValue(emissionFactorVal) : null)
                    .build();

            wasteGasActivitiesMap.put(activityType, details);
            wasteGasActivities.add(activityType);
        }
    }

    public static Map.Entry<AnnexVIISection44, AnnexVIISection46> getHydrogenSynthesisGasPairAnnexValues(
            CellReference firstCell,
            int offset,
            Sheet sheet,
            FormulaEvaluator evaluator) {

        CellReference productionCell = offset == 0
                ? firstCell
                : new CellReference(firstCell.getRow(), firstCell.getCol() + (offset * 2));

        String productionValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(productionCell, sheet, evaluator);
        AnnexVIISection44 productionValue = AnnexVIISection44.getByValue(productionValueStr);

        CellReference fractionCell = new CellReference(productionCell.getRow() + 1, productionCell.getCol());
        String fractionValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(fractionCell, sheet, evaluator);
        AnnexVIISection46 fractionValue = AnnexVIISection46.getByValue(fractionValueStr);

        return new AbstractMap.SimpleEntry<>(productionValue, fractionValue);
    }

    public static AnnualActivityHeatLevel getFallbackApproachAnnualActivityHeatLevel(
            CellReference dcFirstCell, String methodologyAppliedDescriptionStartCell,
            String HO_flagAndReasonStartCell, String HO_detailsStartCell,
            String trackingMethodologyDescriptionStartCell, Sheet sheet, FormulaEvaluator evaluator)
            throws DigitizedMmpMigrationException {

        AnnualActivityHeatLevel annualActivityHeatLevel = new AnnualActivityHeatLevel();
        annualActivityHeatLevel.setAnnualLevelType(AnnualLevelType.ACTIVITY_HEAT);

        List<MeasurableHeatFlow> measurableHeatFlowList = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            CellReference quantificationCell = j == 0 ? dcFirstCell : new CellReference(dcFirstCell.getRow(), dcFirstCell.getCol() + (j * 2));
            String quantificationValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(quantificationCell, sheet, evaluator);
            AnnexVIISection45 quantification = AnnexVIISection45.getByValue(quantificationValueStr);

            CellReference netCell = new CellReference(quantificationCell.getRow() + 1, quantificationCell.getCol());
            String netValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(netCell, sheet, evaluator);
            AnnexVIISection72 net = AnnexVIISection72.getByValue(netValueStr);

            if (!ObjectUtils.isEmpty(quantification) || !ObjectUtils.isEmpty(net)) {
                measurableHeatFlowList.add(new MeasurableHeatFlow(String.valueOf(j), quantification, net));
            }
        }
        annualActivityHeatLevel.setMeasurableHeatFlowList(measurableHeatFlowList);

        setFallbackApproachAnnualLevelCommonProperties(annualActivityHeatLevel, sheet, methodologyAppliedDescriptionStartCell, HO_flagAndReasonStartCell, HO_detailsStartCell, trackingMethodologyDescriptionStartCell);

        return annualActivityHeatLevel;
    }

    public static AnnualActivityFuelLevel getFallbackApproachAnnualActivityFuelLevel(
            CellReference dcFirstCell, String methodologyAppliedDescriptionStartCell,
            String HO_flagAndReasonStartCell, String HO_detailsStartCell,
            String trackingMethodologyDescriptionStartCell, Sheet sheet, FormulaEvaluator evaluator)
            throws DigitizedMmpMigrationException {

        AnnualActivityFuelLevel annualActivityFuelLevel = new AnnualActivityFuelLevel();
        annualActivityFuelLevel.setAnnualLevelType(AnnualLevelType.ACTIVITY_FUEL);

        List<ActivityFuelDataSource> activityFuelDataSources = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            CellReference quantificationCell = j == 0 ? dcFirstCell : new CellReference(dcFirstCell.getRow(), dcFirstCell.getCol() + (j * 2));
            String fuelInputValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(quantificationCell, sheet, evaluator);
            AnnexVIISection44 fuelInput = AnnexVIISection44.getByValue(fuelInputValueStr);

            CellReference energyContentCell = new CellReference(quantificationCell.getRow() + 1, quantificationCell.getCol());
            String energyContentValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(energyContentCell, sheet, evaluator);
            AnnexVIISection46 energyContent = AnnexVIISection46.getByValue(energyContentValueStr);

            if (!ObjectUtils.isEmpty(fuelInput) || !ObjectUtils.isEmpty(energyContent)) {
                activityFuelDataSources.add(new ActivityFuelDataSource(String.valueOf(j), fuelInput, energyContent));
            }
        }
        annualActivityFuelLevel.setFuelDataSources(activityFuelDataSources);

        setFallbackApproachAnnualLevelCommonProperties(annualActivityFuelLevel, sheet, methodologyAppliedDescriptionStartCell, HO_flagAndReasonStartCell, HO_detailsStartCell, trackingMethodologyDescriptionStartCell);

        return annualActivityFuelLevel;
    }

    public static AnnualActivityProcessLevel getFallbackApproachAnnualActivityProcessLevel(
            String methodologyAppliedDescriptionStartCell,
            String trackingMethodologyDescriptionStartCell, Sheet sheet) {

        AnnualActivityProcessLevel annualActivityProcessLevel = new AnnualActivityProcessLevel();
        annualActivityProcessLevel.setAnnualLevelType(AnnualLevelType.ACTIVITY_PROCESS);

        annualActivityProcessLevel.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F" + methodologyAppliedDescriptionStartCell));
        annualActivityProcessLevel.setTrackingMethodologyDescription(getStringValueOfCellByPosition(sheet, "F" + trackingMethodologyDescriptionStartCell));

        return annualActivityProcessLevel;
    }

    private static void setFallbackApproachAnnualLevelCommonProperties(AnnualActivityLevel level, Sheet sheet,
                                                                       String methodologyAppliedDescriptionStartCell,
                                                                       String HO_flagAndReasonStartCell,
                                                                       String HO_detailsStartCell,
                                                                       String trackingMethodologyDescriptionStartCell) throws DigitizedMmpMigrationException {

        level.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F" + methodologyAppliedDescriptionStartCell));
        level.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I" + HO_flagAndReasonStartCell, "K" + HO_flagAndReasonStartCell, "F" + HO_detailsStartCell));
        level.setTrackingMethodologyDescription(getStringValueOfCellByPosition(sheet, "F" + trackingMethodologyDescriptionStartCell));
    }

    public static DirectlyAttributableEmissionsFA getFallbackApproachDirectlyAttributableEmissions(String attributionStartCell, Sheet sheet){
        DirectlyAttributableEmissionsFA directlyAttributableEmissionsFA = new DirectlyAttributableEmissionsFA();
        directlyAttributableEmissionsFA.setDirectlyAttributableEmissionsType(DirectlyAttributableEmissionsType.FALLBACK_APPROACH);

        directlyAttributableEmissionsFA.setAttribution(subInstallation_generalDescription(sheet, "F" + attributionStartCell));
        return directlyAttributableEmissionsFA;
    }

    public static FuelInputAndRelevantEmissionFactorFA getFuelInputAndRelevantEmissionFactor(CellReference dcFirstCell, String methodologyAppliedDescriptionStartCell,
                                                                                      String HO_flagAndReasonStartCell, String HO_detailsStartCell,
                                                                                      String wasteGasFlag, Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {

        FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA = new FuelInputAndRelevantEmissionFactorFA();
        fuelInputAndRelevantEmissionFactorFA.setFuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.FALLBACK_APPROACH);
        List<FuelInputDataSourceFA> dataSources = extractFuelInputAndRelevantEmissionFactorDataSources(dcFirstCell,sheet,evaluator);
        fuelInputAndRelevantEmissionFactorFA.setWasteGasesInput(getBooleanValueOfCellByPosition(sheet, "H" + wasteGasFlag));
        fuelInputAndRelevantEmissionFactorFA.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F" + methodologyAppliedDescriptionStartCell));
        fuelInputAndRelevantEmissionFactorFA.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I" + HO_flagAndReasonStartCell, "K" + HO_flagAndReasonStartCell, "F" + HO_detailsStartCell));
        fuelInputAndRelevantEmissionFactorFA.setDataSources(dataSources);

        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getFuelInputDataSourceNo(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("FuelInputDataSources are not ordered!");

        return fuelInputAndRelevantEmissionFactorFA;
    }

    public static FuelInputAndRelevantEmissionFactorHeatFA getHeatFuelInputAndRelevantEmissionFactor(CellReference dcFirstCell, String methodologyAppliedDescriptionStartCell,
                                                                                                        String HO_flagAndReasonStartCell, String HO_detailsStartCell,
                                                                                                        String wasteGasFlag, Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {

        FuelInputAndRelevantEmissionFactorHeatFA fuelInputAndRelevantEmissionFactorHeatFA = new FuelInputAndRelevantEmissionFactorHeatFA();
        fuelInputAndRelevantEmissionFactorHeatFA.setFuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.HEAT_FALLBACK_APPROACH);
        List<FuelInputDataSourceFA> dataSources = extractFuelInputAndRelevantEmissionFactorDataSources(dcFirstCell,sheet,evaluator);

        fuelInputAndRelevantEmissionFactorHeatFA.setExists(!dataSources.isEmpty());
        fuelInputAndRelevantEmissionFactorHeatFA.setWasteGasesInput(getNullableBooleanValueOfCellByPosition(sheet, "H" + wasteGasFlag));
        fuelInputAndRelevantEmissionFactorHeatFA.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F" + methodologyAppliedDescriptionStartCell));
        fuelInputAndRelevantEmissionFactorHeatFA.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I" + HO_flagAndReasonStartCell, "K" + HO_flagAndReasonStartCell, "F" + HO_detailsStartCell));
        fuelInputAndRelevantEmissionFactorHeatFA.setDataSources(dataSources);

        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getFuelInputDataSourceNo(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("FuelInputDataSources are not ordered!");

        return fuelInputAndRelevantEmissionFactorHeatFA;
    }

    private static List<FuelInputDataSourceFA> extractFuelInputAndRelevantEmissionFactorDataSources(CellReference dcFirstCell,Sheet sheet, FormulaEvaluator evaluator) {
        List<FuelInputDataSourceFA> dataSources = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            CellReference fuelInputCell = j == 0 ? dcFirstCell : new CellReference(dcFirstCell.getRow(), dcFirstCell.getCol() + (j * 2));
            String fuelInputValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(fuelInputCell, sheet, evaluator);
            AnnexVIISection44 fuelInput = AnnexVIISection44.getByValue(fuelInputValueStr);

            CellReference netCalorificValueCell = new CellReference(fuelInputCell.getRow() + 1, fuelInputCell.getCol());
            String netCalorificValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(netCalorificValueCell, sheet, evaluator);
            AnnexVIISection46 netCalorificValue = AnnexVIISection46.getByValue(netCalorificValueStr);

            CellReference weightedEmissionFactorCell = new CellReference(fuelInputCell.getRow() + 2, fuelInputCell.getCol());
            String weightedEmissionFactorStr = DigitizedMmpMigrationUtils.getStringValueOfCell(weightedEmissionFactorCell, sheet, evaluator);
            AnnexVIISection46 weightedEmissionFactor = AnnexVIISection46.getByValue(weightedEmissionFactorStr);

            CellReference wasteGasFuelInputCell = new CellReference(fuelInputCell.getRow() + 3, fuelInputCell.getCol());
            String wasteGasFuelInputStr = DigitizedMmpMigrationUtils.getStringValueOfCell(wasteGasFuelInputCell, sheet, evaluator);
            AnnexVIISection44 wasteGasFuelInput = AnnexVIISection44.getByValue(wasteGasFuelInputStr);

            CellReference wasteGasNetCalorificValueCell = new CellReference(fuelInputCell.getRow() + 4, fuelInputCell.getCol());
            String wasteGasNetCalorificValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(wasteGasNetCalorificValueCell, sheet, evaluator);
            AnnexVIISection46 wasteGasNetCalorificValue = AnnexVIISection46.getByValue(wasteGasNetCalorificValueStr);

            CellReference emissionFactorCell = new CellReference(fuelInputCell.getRow() + 5, fuelInputCell.getCol());
            String emissionFactorStr = DigitizedMmpMigrationUtils.getStringValueOfCell(emissionFactorCell, sheet, evaluator);
            AnnexVIISection46 emissionFactor = AnnexVIISection46.getByValue(emissionFactorStr);

            if (!ObjectUtils.isEmpty(fuelInput) || !ObjectUtils.isEmpty(netCalorificValue) ||
                    !ObjectUtils.isEmpty(weightedEmissionFactor) || !ObjectUtils.isEmpty(wasteGasFuelInput) || !ObjectUtils.isEmpty(wasteGasNetCalorificValue) ||
                    !ObjectUtils.isEmpty(emissionFactor)) {
                FuelInputDataSourceFA fuelInputDataSource = FuelInputDataSourceFA.builder()
                        .fuelInputDataSourceNo(String.valueOf(j))
                        .fuelInput(fuelInput)
                        .netCalorificValue(netCalorificValue)
                        .weightedEmissionFactor(weightedEmissionFactor)
                        .wasteGasFuelInput(wasteGasFuelInput)
                        .wasteGasNetCalorificValue(wasteGasNetCalorificValue)
                        .emissionFactor(emissionFactor)
                        .build();

                dataSources.add(fuelInputDataSource);
            }
        }
        return dataSources;
    }

    public static MeasurableHeatProduced getMeasurableHeatProduced(CellReference dcFirstCell, String methodologyAppliedDescriptionStartCell,
                                                                   String HO_flagAndReasonStartCell, String HO_detailsStartCell, Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {
        MeasurableHeatProduced measurableHeatProduced = new MeasurableHeatProduced();

        List<MeasurableHeatProducedDataSource> dataSources = new ArrayList<>();
        for (int j = 0; j < 3; j++) {

            CellReference heatProducedCell = j == 0 ? dcFirstCell : new CellReference(dcFirstCell.getRow(), dcFirstCell.getCol() + (j * 2));
            String heatProducedValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(heatProducedCell, sheet, evaluator);
            AnnexVIISection45 heatProduced = AnnexVIISection45.getByValue(heatProducedValueStr);

            if (!ObjectUtils.isEmpty(heatProduced)) {
               MeasurableHeatProducedDataSource measurableHeatProducedDataSource = MeasurableHeatProducedDataSource.builder()
                       .dataSourceNo(String.valueOf(j))
                       .heatProduced(heatProduced)
                       .build();

               dataSources.add(measurableHeatProducedDataSource);
            }
        }
        measurableHeatProduced.setProduced(!dataSources.isEmpty());
        measurableHeatProduced.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F" + methodologyAppliedDescriptionStartCell));
        measurableHeatProduced.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I" + HO_flagAndReasonStartCell, "K" + HO_flagAndReasonStartCell, "F" + HO_detailsStartCell));
        measurableHeatProduced.setDataSources(dataSources);


        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("MeasurableHeatProducedDataSources are not ordered!");

        return measurableHeatProduced;
    }

    public static MeasurableHeatImported getMeasurableHeatImported(String isRelevantCell, CellReference typeRelevantFirstCell, String methodologyAppliedDescriptionStartCell,
                                                                   String HO_flagAndReasonStartCell, String HO_detailsStartCell, String determinationEmissionDescriptionStartCell,
                                                                   Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {
    MeasurableHeatImported measurableHeatImported = new MeasurableHeatImported();

    List<MeasurableHeatImportedDataSource> dataSources = new ArrayList<>();

    boolean isRelevant = getBooleanValueOfCellByPosition(sheet, "M" + isRelevantCell);

    if (!isRelevant) {
        measurableHeatImported.getMeasurableHeatImportedActivities().add(MeasurableHeatImportedType.MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED);
        return measurableHeatImported;
    }

        MeasurableHeatImportedType[] types = {
                MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES,
                MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK,
                MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,
                MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_FUEL_BENCHMARK,
                MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_WASTE_GAS
        };

        for (int i = 0; i < types.length; i++) {
            CellReference relevantCell = new CellReference(typeRelevantFirstCell.getRow() + (i * 2), typeRelevantFirstCell.getCol());
            boolean isTypeRelevant = getCellValueBoolean(relevantCell, sheet);

            if (isTypeRelevant) {
                measurableHeatImported.getMeasurableHeatImportedActivities().add(types[i]);
            }
        }

    CellReference dcFirstCell = new CellReference(typeRelevantFirstCell.getRow(), typeRelevantFirstCell.getCol() + 1);

        for (int j = 0; j < 3; j++) {
            MeasurableHeatImportedDataSource measurableHeatImportedDataSource = MeasurableHeatImportedDataSource.builder()
                    .dataSourceNo(String.valueOf(j))
                    .build();

            CellReference baseCell = j == 0 ? dcFirstCell : new CellReference(dcFirstCell.getRow(), dcFirstCell.getCol() + (j * 2));

            // Define the corresponding types and their row offsets
            Map<MeasurableHeatImportedType, Integer> typeOffsets = Map.of(
                    MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES, 0,
                    MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK, 2,
                    MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP, 4,
                    MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_FUEL_BENCHMARK, 6,
                    MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_WASTE_GAS, 8
            );

            for (Map.Entry<MeasurableHeatImportedType, Integer> entry : typeOffsets.entrySet()) {
                MeasurableHeatImportedType type = entry.getKey();
                int rowOffset = entry.getValue();

                CellReference entityCell = new CellReference(baseCell.getRow() + rowOffset, baseCell.getCol());
                CellReference netContentCell = new CellReference(entityCell.getRow() + 1, entityCell.getCol());

                String entityValue = getStringValueOfCell(entityCell, sheet, evaluator);
                String netContentValue = getStringValueOfCell(netContentCell, sheet, evaluator);

                if (Strings.isNotBlank(entityValue) || Strings.isNotBlank(netContentValue)) {
                    measurableHeatImportedDataSource.getMeasurableHeatImportedActivityDetails()
                            .put(type, MeasurableHeatImportedDataSourceDetails.builder()
                                    .entity(AnnexVIISection45.getByValue(entityValue))
                                    .netContent(AnnexVIISection72.getByValue(netContentValue))
                                    .build());
                }
            }
            if (!ObjectUtils.isEmpty(measurableHeatImportedDataSource.getMeasurableHeatImportedActivityDetails())) {
                dataSources.add(measurableHeatImportedDataSource);
            }
        }
        measurableHeatImported.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F" + methodologyAppliedDescriptionStartCell));
        measurableHeatImported.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I" + HO_flagAndReasonStartCell, "K" + HO_flagAndReasonStartCell, "F" + HO_detailsStartCell));
        measurableHeatImported.setMethodologyDeterminationEmissionDescription(subInstallation_generalDescription(sheet, "F" + determinationEmissionDescriptionStartCell));
        measurableHeatImported.setDataSources(dataSources);


        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("MeasurableHeatProducedDataSources are not ordered!");


        return measurableHeatImported;
    }

    public static MeasurableHeatExported getMeasurableHeatExported(String isRelevantCell, CellReference dcFirstCell, String methodologyAppliedDescriptionStartCell,
                                                                   String HO_flagAndReasonStartCell, String HO_detailsStartCell, String determinationEmissionDescriptionStartCell, Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {
        MeasurableHeatExported measurableHeatExported = new MeasurableHeatExported();

        boolean isRelevant = getBooleanValueOfCellByPosition(sheet, "M" + isRelevantCell);

        if (!isRelevant) {
            return measurableHeatExported;
        }

        List<MeasurableHeatExportedDataSource> dataSources = new ArrayList<>();
        for (int j = 0; j < 3; j++) {

            CellReference heatExportedCell = j == 0 ? dcFirstCell : new CellReference(dcFirstCell.getRow(), dcFirstCell.getCol() + (j * 2));
            String heatExportedValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(heatExportedCell, sheet, evaluator);
            AnnexVIISection45 heatExported = AnnexVIISection45.getByValue(heatExportedValueStr);

            CellReference netMeasurableHeatFlowsCell =  new CellReference(heatExportedCell.getRow() + 1, heatExportedCell.getCol());
            String netMeasurableHeatFlowsValueStr = DigitizedMmpMigrationUtils.getStringValueOfCell(netMeasurableHeatFlowsCell, sheet, evaluator);
            AnnexVIISection72 netMeasurableHeatFlows = AnnexVIISection72.getByValue(netMeasurableHeatFlowsValueStr);

            if (!ObjectUtils.isEmpty(netMeasurableHeatFlows)) {
                MeasurableHeatExportedDataSource measurableHeatExportedDataSource = MeasurableHeatExportedDataSource.builder()
                        .dataSourceNo(String.valueOf(j))
                        .heatExported(heatExported)
                        .netMeasurableHeatFlows(netMeasurableHeatFlows)
                        .build();

                dataSources.add(measurableHeatExportedDataSource);
            }
        }

        measurableHeatExported.setMeasurableHeatExported(isRelevant);
        measurableHeatExported.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F" + methodologyAppliedDescriptionStartCell));
        measurableHeatExported.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I" + HO_flagAndReasonStartCell, "K" + HO_flagAndReasonStartCell, "F" + HO_detailsStartCell));
        measurableHeatExported.setDataSources(dataSources);
        measurableHeatExported.setMethodologyDeterminationEmissionDescription(subInstallation_generalDescription(sheet, "F" + determinationEmissionDescriptionStartCell));

        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNo(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("MeasurableHeatExportedDataSources are not ordered!");

        return measurableHeatExported;
    }

    public static FuelInputFlows migrateFuelInputFlows(Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {
        FuelInputFlows fuelInputFlows = new FuelInputFlows();
        List<FuelInputDataSource> dataSources = extractFuelInputDataSources(sheet, evaluator, new CellReference("I35"));

        fuelInputFlows.setFuelInputDataSources(dataSources);
        fuelInputFlows.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F42"));
        fuelInputFlows.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I46", "K46", "F53"));

        boolean isOrdered = IntStream.range(0, dataSources.size())
                .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNumber(), String.valueOf(k)));

        if (!isOrdered)
            throw new DigitizedMmpMigrationException("FuelInputDataSources are not ordered!");

        return fuelInputFlows;
    }

    public static MeasurableHeatFlows migrateMeasurableHeatFlows(Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {
        MeasurableHeatFlows measurableHeatFlows = new MeasurableHeatFlows();
        boolean isRelevant = getCellValueBoolean(new CellReference("M59"), sheet);
        measurableHeatFlows.setMeasurableHeatFlowsRelevant(isRelevant);

        if (isRelevant) {
            List<MeasurableHeatFlowsDataSource> dataSources = extractMeasurableHeatDataSources(sheet, evaluator, new CellReference("I69"));

            measurableHeatFlows.setMeasurableHeatFlowsDataSources(dataSources);
            measurableHeatFlows.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F76"));
            measurableHeatFlows.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I80", "K80", "F87"));

            boolean isOrdered1 = IntStream.range(0, dataSources.size())
                    .allMatch(k -> Objects.equals(dataSources.get(k).getDataSourceNumber(), String.valueOf(k)));

            if (!isOrdered1)
                throw new DigitizedMmpMigrationException("MeasurableHeatFlowsDataSources are not ordered!");
        }

        return measurableHeatFlows;
    }

    public static WasteGasFlows migrateWasteGasFlows(Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {

        boolean isRelevant = getCellValueBoolean(new CellReference("M93"), sheet);
        WasteGasFlows wasteGasFlows = WasteGasFlows.builder().wasteGasFlowsRelevant(isRelevant).build();
        if (isRelevant) {
            List<WasteGasFlowsDataSource> dataSources = extractWasteGasFlowsDataSources(sheet, evaluator, new CellReference("I102"));
            wasteGasFlows.setWasteGasFlowsDataSources(dataSources);
            wasteGasFlows.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F109"));
            wasteGasFlows.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I113", "K113", "F120"));
        }
        return wasteGasFlows;
    }

    public static ElectricityFlows migrateElectricityFlows(Sheet sheet, FormulaEvaluator evaluator) throws DigitizedMmpMigrationException {
        boolean electricityProduced = getCellValueBoolean(new CellReference("M126"), sheet);
        ElectricityFlows electricityFlows = ElectricityFlows.builder().electricityProduced(electricityProduced).build();
        if (electricityProduced) {
            List<ElectricityFlowsDataSource> dataSources = extractElectricityFlowsDataSource(sheet, evaluator, new CellReference("I133"));
            electricityFlows.setElectricityFlowsDataSources(dataSources);
            electricityFlows.setMethodologyAppliedDescription(subInstallation_generalDescription(sheet, "F140"));
            electricityFlows.setHierarchicalOrder(subInstallation_HierarchicalOrder(sheet, "I144", "K144", "F151"));
        }
        return electricityFlows;
    }

    private static List<FuelInputDataSource> extractFuelInputDataSources(Sheet sheet, FormulaEvaluator evaluator, CellReference startCell) {
        List<FuelInputDataSource> dataSources = new ArrayList<>();

        for (int j = 0; j < 3; j++) {
            CellReference quantificationCell = new CellReference(startCell.getRow(), startCell.getCol() + (j * 2));
            AnnexVIISection44 fuelInput = AnnexVIISection44.getByValue(getStringValueOfCell(quantificationCell, sheet, evaluator));
            AnnexVIISection46 energyContent = AnnexVIISection46.getByValue(getStringValueOfCell(new CellReference(quantificationCell.getRow() + 1, quantificationCell.getCol()), sheet, evaluator));

            if (!ObjectUtils.isEmpty(fuelInput) || !ObjectUtils.isEmpty(energyContent)) {
                dataSources.add(new FuelInputDataSource(String.valueOf(j), fuelInput, energyContent));
            }
        }

        return dataSources;
    }

    private static List<MeasurableHeatFlowsDataSource> extractMeasurableHeatDataSources(Sheet sheet, FormulaEvaluator evaluator, CellReference startCell) {
        List<MeasurableHeatFlowsDataSource> dataSources = new ArrayList<>();

        for (int j = 0; j < 3; j++) {
            CellReference quantificationCell = new CellReference(startCell.getRow(), startCell.getCol() + (j * 2));
            AnnexVIISection45 fuelInput = AnnexVIISection45.getByValue(getStringValueOfCell(quantificationCell, sheet, evaluator));
            AnnexVIISection72 net = AnnexVIISection72.getByValue(getStringValueOfCell(new CellReference(quantificationCell.getRow() + 1, quantificationCell.getCol()), sheet, evaluator));

            if (!ObjectUtils.isEmpty(fuelInput) || !ObjectUtils.isEmpty(net)) {
                dataSources.add(new MeasurableHeatFlowsDataSource(String.valueOf(j), fuelInput, net));
            }
        }

        return dataSources;
    }

    private static List<WasteGasFlowsDataSource> extractWasteGasFlowsDataSources(Sheet sheet, FormulaEvaluator evaluator, CellReference startCell) {
        List<WasteGasFlowsDataSource> dataSources = new ArrayList<>();
        for (int i=0; i < 3; i++) {
            CellReference quantificationCell = new CellReference(startCell.getRow(), startCell.getCol() + (i*2));
            AnnexVIISection44 quantification = AnnexVIISection44.getByValue(getStringValueOfCell(quantificationCell, sheet, evaluator));
            AnnexVIISection46 energy = AnnexVIISection46.getByValue(getStringValueOfCell(new CellReference(quantificationCell.getRow() + 1, quantificationCell.getCol()), sheet, evaluator));

            if (!ObjectUtils.isEmpty(quantification) || !ObjectUtils.isEmpty(energy)) {
                dataSources.add(WasteGasFlowsDataSource.builder().dataSourceNumber(String.valueOf(i)).quantification(quantification).energyContent(energy).build());
            }
        }
        return dataSources;
    }

    private static List<ElectricityFlowsDataSource> extractElectricityFlowsDataSource(Sheet sheet, FormulaEvaluator evaluator, CellReference startCell) {
        List<ElectricityFlowsDataSource> dataSources = new ArrayList<>();
        for (int i=0; i < 3; i++) {
            CellReference quantificationCell = new CellReference(startCell.getRow(), startCell.getCol() + (i*2));
            AnnexVIISection45 quantification = AnnexVIISection45.getByValue(getStringValueOfCell(quantificationCell, sheet, evaluator));

            if (!ObjectUtils.isEmpty(quantification)) {
                dataSources.add(ElectricityFlowsDataSource.builder().dataSourceNumber(String.valueOf(i)).quantification(quantification).build());
            }
        }
        return dataSources;
    }

}

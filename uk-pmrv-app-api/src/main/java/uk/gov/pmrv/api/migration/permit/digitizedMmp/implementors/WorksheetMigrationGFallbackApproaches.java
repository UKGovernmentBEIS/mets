package uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationError;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationException;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.WorksheetNames;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationCategory;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.MeasurableHeat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils.concatenateSubInstallationDescription;

@Service
public class WorksheetMigrationGFallbackApproaches implements WorksheetMigrationImplementor{

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public WorksheetNames getWorksheetname() {
        return WorksheetNames.G_FALLBACK;
    }

    @Override
    public List<String> migrateWorksheet(Workbook workbook, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString,
                                         String fileUuid, DigitizedPlan digitizedPlan) {
        Sheet sheet = workbook.getSheet(this.getWorksheetname().getName());
        return this.doMigrate_gFallbackApproaches(sheet, accountIdString, fileUuid, digitizedPlan);
    }

    private List<String> doMigrate_gFallbackApproaches(Sheet sheet, String accountIdString, String fileUuid,
                                                       DigitizedPlan digitizedPlan) {
        List<String> results = new ArrayList<>();
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        try {
            for (int i = 0; i < digitizedPlan.getSubInstallations().size(); i++) {
                SubInstallation subInstallation = digitizedPlan.getSubInstallations().get(i);

                if (ObjectUtils.isEmpty(subInstallation.getSubInstallationType()))
                    continue;

                if (!Objects.equals(subInstallation.getSubInstallationType().getCategory(), SubInstallationCategory.FALLBACK_APPROACH))
                    continue;

                switch (subInstallation.getSubInstallationType()) {
                    case HEAT_BENCHMARK_CL -> {
                        subInstallation.setDescription(concatenateSubInstallationDescription(sheet, "E41"));
                        subInstallation.setAnnualLevel(DigitizedMmpMigrationUtils.getFallbackApproachAnnualActivityHeatLevel(new CellReference("I58"), "67", "71",
                                "78", "87", sheet, evaluator));
                        subInstallation.setDirectlyAttributableEmissions(DigitizedMmpMigrationUtils.getFallbackApproachDirectlyAttributableEmissions("102", sheet));
                        subInstallation.setFuelInputAndRelevantEmissionFactor(DigitizedMmpMigrationUtils.getHeatFuelInputAndRelevantEmissionFactor(new CellReference("I117"), "128", "132", "135",
                                "120", sheet, evaluator));
                        subInstallation.setMeasurableHeat(MeasurableHeat.builder()
                                .measurableHeatProduced(DigitizedMmpMigrationUtils.getMeasurableHeatProduced(new CellReference("I144"), "150", "154", "157", sheet, evaluator))
                                .measurableHeatImported(DigitizedMmpMigrationUtils.getMeasurableHeatImported("162", new CellReference("H174"), "189", "193", "196", "204", sheet, evaluator))
                                .build());
                    }
                    case HEAT_BENCHMARK_NON_CL -> {
                        subInstallation.setDescription(concatenateSubInstallationDescription(sheet, "E218"));
                        subInstallation.setAnnualLevel(DigitizedMmpMigrationUtils.getFallbackApproachAnnualActivityHeatLevel(new CellReference("I230"), "237", "241",
                                "244", "250", sheet, evaluator));
                        subInstallation.setDirectlyAttributableEmissions(DigitizedMmpMigrationUtils.getFallbackApproachDirectlyAttributableEmissions("260", sheet));
                        subInstallation.setFuelInputAndRelevantEmissionFactor(DigitizedMmpMigrationUtils.getHeatFuelInputAndRelevantEmissionFactor(new CellReference("I269"), "280", "284", "287",
                                "272", sheet, evaluator));
                        subInstallation.setMeasurableHeat(MeasurableHeat.builder()
                                .measurableHeatProduced(DigitizedMmpMigrationUtils.getMeasurableHeatProduced(new CellReference("I294"), "300", "304", "307", sheet, evaluator))
                                .measurableHeatImported(DigitizedMmpMigrationUtils.getMeasurableHeatImported("312", new CellReference("H317"), "332", "336", "339", "345", sheet, evaluator))
                                .build());
                    }
                    case DISTRICT_HEATING_NON_CL -> {
                        subInstallation.setDescription(concatenateSubInstallationDescription(sheet, "E359"));
                        subInstallation.setAnnualLevel(DigitizedMmpMigrationUtils.getFallbackApproachAnnualActivityHeatLevel(new CellReference("I371"), "378", "382",
                                "385", "391", sheet, evaluator));
                        subInstallation.setDirectlyAttributableEmissions(DigitizedMmpMigrationUtils.getFallbackApproachDirectlyAttributableEmissions("401", sheet));
                        subInstallation.setFuelInputAndRelevantEmissionFactor(DigitizedMmpMigrationUtils.getHeatFuelInputAndRelevantEmissionFactor(new CellReference("I410"), "421", "425", "428",
                                "413", sheet, evaluator));
                        subInstallation.setMeasurableHeat(MeasurableHeat.builder()
                                .measurableHeatProduced(DigitizedMmpMigrationUtils.getMeasurableHeatProduced(new CellReference("I435"), "441", "445", "448", sheet, evaluator))
                                .measurableHeatImported(DigitizedMmpMigrationUtils.getMeasurableHeatImported("453", new CellReference("H458"), "473", "477", "480", "486", sheet, evaluator))
                                .build());
                    }
                    case FUEL_BENCHMARK_CL -> {
                        subInstallation.setDescription(concatenateSubInstallationDescription(sheet, "E506"));
                        subInstallation.setAnnualLevel(DigitizedMmpMigrationUtils.getFallbackApproachAnnualActivityFuelLevel(new CellReference("I523"), "532", "536",
                                "539", "545", sheet, evaluator));
                        subInstallation.setDirectlyAttributableEmissions(DigitizedMmpMigrationUtils.getFallbackApproachDirectlyAttributableEmissions("555", sheet));
                        subInstallation.setFuelInputAndRelevantEmissionFactor(DigitizedMmpMigrationUtils.getFuelInputAndRelevantEmissionFactor(new CellReference("I568"), "579", "583", "586",
                                "571", sheet, evaluator));
                        subInstallation.setMeasurableHeat(MeasurableHeat.builder()
                                .measurableHeatExported(DigitizedMmpMigrationUtils.getMeasurableHeatExported("591", new CellReference("I596"), "603", "607", "610", "616", sheet, evaluator))
                                .build());
                    }
                    case FUEL_BENCHMARK_NON_CL -> {
                        subInstallation.setDescription(concatenateSubInstallationDescription(sheet, "E631"));
                        subInstallation.setAnnualLevel(DigitizedMmpMigrationUtils.getFallbackApproachAnnualActivityFuelLevel(new CellReference("I643"), "652", "656",
                                "659", "665", sheet, evaluator));
                        subInstallation.setDirectlyAttributableEmissions(DigitizedMmpMigrationUtils.getFallbackApproachDirectlyAttributableEmissions("675", sheet));
                        subInstallation.setFuelInputAndRelevantEmissionFactor(DigitizedMmpMigrationUtils.getFuelInputAndRelevantEmissionFactor(new CellReference("I685"), "696", "700", "703",
                                "688", sheet, evaluator));
                        subInstallation.setMeasurableHeat(MeasurableHeat.builder()
                                .measurableHeatExported(DigitizedMmpMigrationUtils.getMeasurableHeatExported("708", new CellReference("I713"), "720", "724", "727", "733", sheet, evaluator))
                                .build());

                    }
                    case PROCESS_EMISSIONS_CL -> {
                        subInstallation.setDescription(concatenateSubInstallationDescription(sheet, "E748"));
                        subInstallation.setAnnualLevel(DigitizedMmpMigrationUtils.getFallbackApproachAnnualActivityProcessLevel("762", "770", sheet));
                    }
                    case PROCESS_EMISSIONS_NON_CL -> {
                        subInstallation.setDescription(concatenateSubInstallationDescription(sheet, "E784"));
                        subInstallation.setAnnualLevel(DigitizedMmpMigrationUtils.getFallbackApproachAnnualActivityProcessLevel("798", "806", sheet));
                    }
                    default -> {
                        subInstallation.setDescription(null);
                        subInstallation.setAnnualLevel(null);
                    }
                }
            }
        }
        catch (DigitizedMmpMigrationException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .fileUuid(fileUuid).worksheetName(sheet.getSheetName()).errorReport(e.getMessage()).build()));
        }


        return results;
    }

}

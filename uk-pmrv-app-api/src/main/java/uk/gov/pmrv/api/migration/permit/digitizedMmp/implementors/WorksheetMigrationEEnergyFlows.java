package uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationError;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationException;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.DigitizedMmpMigrationUtils;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.WorksheetNames;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.*;


import java.util.ArrayList;
import java.util.List;


@Service
public class WorksheetMigrationEEnergyFlows implements WorksheetMigrationImplementor {

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public WorksheetNames getWorksheetname() {
        return WorksheetNames.E_ENERGY_FLOWS;
    }

    @Override
    public List<String> migrateWorksheet(Workbook workbook, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString, String fileUuid, DigitizedPlan digitizedPlan) {
        Sheet sheet = workbook.getSheet(this.getWorksheetname().getName());
        List<String> results = new ArrayList<>();
        try {
            doMigrate_eEnergyFlows(sheet,digitizedPlan);
        } catch (DigitizedMmpMigrationException e) {
            results.add(DigitizedMmpMigrationUtils.buildErrorMessage(DigitizedMmpMigrationError.builder().accountId(accountIdString)
                    .fileUuid(fileUuid).worksheetName(sheet.getSheetName()).errorReport(e.getMessage()).build()));
        }
        return results;
    }

    private void doMigrate_eEnergyFlows(Sheet sheet, DigitizedPlan digitizedPlan) throws DigitizedMmpMigrationException {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        EnergyFlows energyFlows = new EnergyFlows();
        energyFlows.setFuelInputFlows(DigitizedMmpMigrationUtils.migrateFuelInputFlows(sheet, evaluator));
        energyFlows.setMeasurableHeatFlows(DigitizedMmpMigrationUtils.migrateMeasurableHeatFlows(sheet, evaluator));
        energyFlows.setWasteGasFlows(DigitizedMmpMigrationUtils.migrateWasteGasFlows(sheet, evaluator));
        energyFlows.setElectricityFlows(DigitizedMmpMigrationUtils.migrateElectricityFlows(sheet, evaluator));
        digitizedPlan.setEnergyFlows(energyFlows);
    }
}

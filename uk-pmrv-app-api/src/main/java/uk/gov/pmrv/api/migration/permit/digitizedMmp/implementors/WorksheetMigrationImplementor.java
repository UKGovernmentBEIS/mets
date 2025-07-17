package uk.gov.pmrv.api.migration.permit.digitizedMmp.implementors;

import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.WorksheetNames;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;

import java.util.List;

public interface WorksheetMigrationImplementor {

    WorksheetNames getWorksheetname();

    List<String> migrateWorksheet(Workbook workbook, List<MmpFilesMigrationEntity> accompanyingFiles, String accountIdString, String fileUuid, DigitizedPlan digitizedPlan);

    int getOrder();
}

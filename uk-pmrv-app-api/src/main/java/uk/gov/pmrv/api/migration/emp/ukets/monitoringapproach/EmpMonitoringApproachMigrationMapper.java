package uk.gov.pmrv.api.migration.emp.ukets.monitoringapproach;

import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.SmallEmittersMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.SupportFacilityMonitoringApproach;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

import java.util.UUID;

public class EmpMonitoringApproachMigrationMapper {

    public static EmpMigrationMonitoringApproach toEmissionsMonitoringApproach(EtsEmpMonitoringApproach etsMonitoringApproach) {

        if (!etsMonitoringApproach.isSimplifiedProcedure()) {
            return createFuelUseMonitoringApproach();
        } else if ("Small Emitters Tool populated by Eurocontrol’s ETS Support Facility".equalsIgnoreCase(etsMonitoringApproach.getMonitoringApproach())) {
            return createSupportFacilityApproach(etsMonitoringApproach);
        } else return createSmallEmittersApproach(etsMonitoringApproach);

    }

    private static EmpMigrationMonitoringApproach createFuelUseMonitoringApproach() {

        return EmpMigrationMonitoringApproach.builder()
                .monitoringApproach(FuelMonitoringApproach.builder()
                        .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                        .build())
                .build();
    }

    private static EmpMigrationMonitoringApproach createSupportFacilityApproach(EtsEmpMonitoringApproach etsMonitoringApproach) {

        return EmpMigrationMonitoringApproach.builder()
                .monitoringApproach(SupportFacilityMonitoringApproach.builder()
                        .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                        .explanation(etsMonitoringApproach.getSimplifiedReportingEligibility())
                        .build())
                .etsFileAttachment(createEtsFileAttachment(etsMonitoringApproach))
                .build();
    }

    private static EmpMigrationMonitoringApproach createSmallEmittersApproach(EtsEmpMonitoringApproach etsMonitoringApproach) {

        return EmpMigrationMonitoringApproach.builder()
                .monitoringApproach(SmallEmittersMonitoringApproach.builder()
                        .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                        .explanation(etsMonitoringApproach.getSimplifiedReportingEligibility())
                        .build())
                .etsFileAttachment(createEtsFileAttachment(etsMonitoringApproach))
                .build();
    }

    private static EtsFileAttachment createEtsFileAttachment(EtsEmpMonitoringApproach etsEmpMonitoringApproach) {

        return etsEmpMonitoringApproach.getStoredFileName() != null && etsEmpMonitoringApproach.getUploadedFileName() != null &&
                MigrationConstants.ALLOWED_FILE_TYPES.contains(etsEmpMonitoringApproach.getUploadedFileName().substring(etsEmpMonitoringApproach.getUploadedFileName().lastIndexOf(".")).toLowerCase()) ?
                EtsFileAttachment.builder()
                        .etsAccountId(etsEmpMonitoringApproach.getEtsAccountId())
                        .uploadedFileName(etsEmpMonitoringApproach.getUploadedFileName())
                        .storedFileName(etsEmpMonitoringApproach.getStoredFileName())
                        .uuid(UUID.randomUUID())
                        .build() : null;
    }
}

package uk.gov.pmrv.api.migration.emp.corsia.monitoringapproach;

import java.util.UUID;

import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertEmissionsType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

public class EmpMonitoringApproachCorsiaMigrationMapper {

	public static EmpMigrationMonitoringApproachCorsia toEmissionsMonitoringApproach(EtsMonitoringApproachCorsia corsiaMonitoringApproach) {

        if ("CERT".equalsIgnoreCase(corsiaMonitoringApproach.getCorsiaMonitoringMethod())) {
            return createCertMonitoringApproach(corsiaMonitoringApproach);
        } else return createFuelMonitoringApproach();

    }

    private static EmpMigrationMonitoringApproachCorsia createFuelMonitoringApproach() {

        return EmpMigrationMonitoringApproachCorsia.builder()
                .monitoringApproach(FuelMonitoringApproach.builder()
                        .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                        .build())
                .build();
    }

    private static EmpMigrationMonitoringApproachCorsia createCertMonitoringApproach(EtsMonitoringApproachCorsia corsiaMonitoringApproach) {

        return EmpMigrationMonitoringApproachCorsia.builder()
                .monitoringApproach(CertMonitoringApproach.builder()
                        .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
                        .certEmissionsType("Great Circle Distance".equalsIgnoreCase(corsiaMonitoringApproach.getCorsiaCertType()) 
                        		? CertEmissionsType.GREAT_CIRCLE_DISTANCE 
                        				: CertEmissionsType.BLOCK_TIME)
                        .explanation(corsiaMonitoringApproach.getIcaoCertUsedDesc())
                        .build())
                .etsFileAttachment(createEtsFileAttachment(corsiaMonitoringApproach))
                .build();
    }

    private static EtsFileAttachment createEtsFileAttachment(EtsMonitoringApproachCorsia corsiaMonitoringApproach) {

        return corsiaMonitoringApproach.getEvidenceStoredFileName() != null && corsiaMonitoringApproach.getEvidenceUploadedFileName() != null &&
                MigrationConstants.ALLOWED_FILE_TYPES.contains(
                		corsiaMonitoringApproach.getEvidenceUploadedFileName()
                		.substring(corsiaMonitoringApproach.getEvidenceUploadedFileName().lastIndexOf(".")).toLowerCase()) ?
                EtsFileAttachment.builder()
                        .etsAccountId(corsiaMonitoringApproach.getFldEmitterID())
                        .uploadedFileName(corsiaMonitoringApproach.getEvidenceUploadedFileName())
                        .storedFileName(corsiaMonitoringApproach.getEvidenceStoredFileName())
                        .uuid(UUID.randomUUID())
                        .build() : null;
    }
}

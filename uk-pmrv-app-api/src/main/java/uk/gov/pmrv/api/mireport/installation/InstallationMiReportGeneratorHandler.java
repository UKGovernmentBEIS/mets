package uk.gov.pmrv.api.mireport.installation;

import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;

import jakarta.persistence.EntityManager;

public interface InstallationMiReportGeneratorHandler<T extends MiReportParams> {

    MiReportResult generateMiReport(EntityManager entityManager, T reportParams);

    MiReportType getReportType();
}

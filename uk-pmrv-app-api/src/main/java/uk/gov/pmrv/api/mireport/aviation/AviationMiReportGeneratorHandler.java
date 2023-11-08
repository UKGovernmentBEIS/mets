package uk.gov.pmrv.api.mireport.aviation;

import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;

import jakarta.persistence.EntityManager;

public interface AviationMiReportGeneratorHandler<T extends MiReportParams> {

    MiReportResult generateMiReport(EntityManager entityManager, T reportParams);

    MiReportType getReportType();
}

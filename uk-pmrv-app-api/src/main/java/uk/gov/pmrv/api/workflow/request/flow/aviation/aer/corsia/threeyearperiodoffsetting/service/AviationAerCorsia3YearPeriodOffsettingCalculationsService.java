package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service;

import java.time.Year;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;


@Service
public class AviationAerCorsia3YearPeriodOffsettingCalculationsService {

    public Long calculatePeriodOffsettingRequirements(Long totalCalculatedAnnualOffsetting,
                                                         Long totalCefEmissionsReductions) {

        return Math.max(totalCalculatedAnnualOffsetting - totalCefEmissionsReductions, 0);
    }

    public Long calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(
            Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData) {

        return  yearlyOffsettingData
                .values()
                .stream()
                .map(e -> ObjectUtils.isEmpty(e.getCalculatedAnnualOffsetting()) ?
                        0 :
                        e.getCalculatedAnnualOffsetting())
                .reduce(0L, Long::sum);

    }

    public Long calculateTotalCefEmissionsReductionsFromYearlyOffsettingData(
            Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData) {

        return  yearlyOffsettingData
                .values()
                .stream()
                .map(e -> ObjectUtils.isEmpty(e.getCefEmissionsReductions()) ?
                        0 :
                        e.getCefEmissionsReductions())
                .reduce(0L, Long::sum);

    }
}

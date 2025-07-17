package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class AviationAer3YearPeriodOffsettingRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public AviationAer3YearPeriodOffsettingRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING);
    }

    @Override
    public String getPrefix() {
        return "AEM-3YPO-";
    }
}

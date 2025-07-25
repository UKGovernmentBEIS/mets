package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;


@Service
public class AviationAerAnnualOffsettingRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {


    public AviationAerAnnualOffsettingRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }


    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING);
    }

    @Override
    public String getPrefix() {
        return "AEM-AO-";
    }

}

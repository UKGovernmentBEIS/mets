package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmpIssuingAuthorityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpIssuingAuthorityQueryService {

    private final EmpIssuingAuthorityRepository issuingAuthorityRepository;

    public List<String> getAllIssuingAuthorityNames() {
        return issuingAuthorityRepository.findAllIssuingAuthorityNames();
    }

    public boolean existsByName(String name) {
        return issuingAuthorityRepository.existsByName(name);
    }
}

package uk.gov.pmrv.api.aviationreporting.common.service;

import java.time.Year;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationRptAirportsRepository;

@Service
@RequiredArgsConstructor
public class AviationRptAirportsService {

    private final AviationRptAirportsRepository repository;

    @Transactional(readOnly = true)
    public List<AviationRptAirportsDTO> getAirportsByIcaoCodesAndYear(Set<String> icaoCodes, Year year) {
        return repository.findAllByIcaoInAndYear(icaoCodes, year);
    }

    @Transactional(readOnly = true)
    public List<String> findChapter3Icaos(Set<String> icaoCodes, Year year) {
        return repository.findChapter3Icaos(icaoCodes, year);
    }
}


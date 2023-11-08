package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftDataDetails;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerAircraftDataSectionValidator;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAircraftDataSectionValidator implements AviationAerCorsiaContextValidator {


    private final AviationAerAircraftDataSectionValidator aircraftDataSectionValidator;

    @Override
    public AviationAerValidationResult validate(AviationAerCorsiaContainer aerContainer) {

        final Set<AviationAerAircraftDataDetails> aviationAerAircraftData =
                aerContainer.getAer().getAviationAerAircraftData().getAviationAerAircraftDataDetails();

        return aircraftDataSectionValidator.validateAircaftData(aviationAerAircraftData, aerContainer.getReportingYear());
    }
}

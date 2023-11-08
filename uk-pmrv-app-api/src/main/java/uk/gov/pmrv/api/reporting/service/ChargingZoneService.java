package uk.gov.pmrv.api.reporting.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.reporting.domain.dto.ChargingZoneDTO;
import uk.gov.pmrv.api.reporting.repository.ChargingZoneRepository;
import uk.gov.pmrv.api.reporting.transform.ChargingZoneMapper;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Validated
@Service
@RequiredArgsConstructor
public class ChargingZoneService {

    private final ChargingZoneRepository chargingZoneRepository;
    private static final ChargingZoneMapper chargingZoneMapper = Mappers.getMapper(ChargingZoneMapper.class);

    private static final String POST_CODE_PATTERN = "^(?<code>[a-zA-Z0-9]{2,4}\\s[0-9])[a-zA-Z0-9]{0,2}$";

    public List<ChargingZoneDTO> getChargingZonesByPostCode(@NotBlank String postCode) {
        Pattern pattern = Pattern.compile(POST_CODE_PATTERN);
        Matcher matcher = pattern.matcher(postCode);
        if(matcher.matches()) {
            postCode = matcher.group("code");
        }

        return chargingZoneMapper.toChargingZoneDTOs(chargingZoneRepository.findByPostCodesCodeIgnoreCase(postCode));
    }
}

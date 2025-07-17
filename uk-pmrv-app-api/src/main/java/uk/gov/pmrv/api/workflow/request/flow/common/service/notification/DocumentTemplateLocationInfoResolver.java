package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.service.CountryService;

@Service
@RequiredArgsConstructor
public class DocumentTemplateLocationInfoResolver {

    private final CountryService countryService;
    
    public String constructLocationInfo(LocationDTO location) {
        if(ObjectUtils.isEmpty(location)) {
            return null;
        }

        return switch (location.getType()) {
            case OFFSHORE -> location.toString();
            case ONSHORE -> constructAddressInfo(((LocationOnShoreDTO) location).getAddress());
            case ONSHORE_STATE -> constructOnShoreStateLocationInfo((LocationOnShoreStateDTO) location);
            default -> throw new UnsupportedOperationException("Unsupported type: " + location.getType());
        };
    }
    
    public String constructAddressInfo(AddressDTO address) {
        String countryName = countryService.getReferenceData().stream()
                .filter(country -> address.getCountry().equals(country.getCode()))
                .map(Country::getName)
                .findFirst().orElse("");
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(address.getLine1());
        if(StringUtils.hasLength(address.getLine2())) {
            addressBuilder.append("\n").append(address.getLine2());
        }
        addressBuilder.append("\n").append(address.getCity());
        addressBuilder.append("\n").append(address.getPostcode());
        addressBuilder.append("\n").append(countryName);
        return addressBuilder.toString();
    }

    private String constructOnShoreStateLocationInfo(LocationOnShoreStateDTO location) {
        String countryName = countryService.getReferenceData().stream()
            .filter(country -> location.getCountry().equals(country.getCode()))
            .map(Country::getName)
            .findFirst().orElse("");

        StringBuilder addressBuilder = new StringBuilder();

        addressBuilder.append(location.getLine1());
        if(StringUtils.hasLength(location.getLine2())) {
            addressBuilder.append("\n").append(location.getLine2());
        }

        addressBuilder.append("\n").append(location.getCity());
        if(StringUtils.hasLength(location.getState())) {
            addressBuilder.append("\n").append(location.getState());
        }
        if(StringUtils.hasLength(location.getPostcode())) {
            addressBuilder.append("\n").append(location.getPostcode());
        }
        addressBuilder.append("\n").append(countryName);
        return addressBuilder.toString();
    }
}

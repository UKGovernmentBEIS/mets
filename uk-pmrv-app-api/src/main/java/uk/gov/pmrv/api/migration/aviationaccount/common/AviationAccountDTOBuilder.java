package uk.gov.pmrv.api.migration.aviationaccount.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.repository.CountryRepository;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class AviationAccountDTOBuilder {
	
	private final CountryRepository countryRepository;
	
	public AviationAccountCreationDTO constructAviationAccountDTO(AviationEmitter emitter, EmissionTradingScheme scheme) {
        return AviationAccountCreationDTO
        		.builder()
        		.name(emitter.getFldName())
        		.emissionTradingScheme(scheme)
        		.sopId(emitter.getFldNapBenchmarkAllowances())
        		.crcoCode(emitter.getFldCrcoCode())
        		.commencementDate(emitter.getFldFirstFlyDate())
        		.build();
	}

	public LocationOnShoreStateDTO constructLocationDTO(AviationEmitter emitter) {
		Map<String, String> countriesMapping = AviationAccountCountriesMapper.getEtswapToPmrvCountriesMapping();
		String country = null;
		// needed for corsia account 14270
		if ("14270".equals(emitter.getEmitterDisplayId())) {
			buildEmitterLocationFromEmpOperatorDetails(emitter);
		}
		if (emitter.getCountry() != null) {
			Optional.ofNullable(countriesMapping.get(emitter.getCountry())).ifPresent(emitter::setCountry);

			country = countryRepository.findByName(emitter.getCountry())
					.map(Country::getCode)
					.orElse(emitter.getCountry());
		}	
		return LocationOnShoreStateDTO
				.builder()
				.line1(emitter.getAddressLine1())
				.line2(emitter.getAddressLine2())
				.city(emitter.getCity())
				.state(emitter.getStateProvinceRegion())
				.postcode(emitter.getPostCodeZip())
				.country(country)
				.type(LocationType.ONSHORE_STATE)
				.build();
	}

	private void buildEmitterLocationFromEmpOperatorDetails(AviationEmitter emitter) {
		emitter.setAddressLine1("6 Southside Road");
		emitter.setCity("St Georges");
		emitter.setPostCodeZip("DD 03");
		emitter.setCountry("Bermuda");
	}

	public ReportingStatusReason constructReportingStatusReason(AviationEmitter emitter) {
		return ReportingStatusReason
				.builder()
				.reason(emitter.getReportingStatusReason())
				.build();
	}

}

package uk.gov.pmrv.api.referencedata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * The country validator (validates against the country code (e.g. GR))
 *
 */
public class CountryValidator implements ConstraintValidator<Country, String> {

	@Autowired
	private final CountryService countryService;

	/**
	 * This no-args constructor is only used by Hibernate to instantiate this validator. It only occurs when
	 * an EMP is persisted in the DB since the EMP entity contains a @LocationOnShoreStateDTO.
	 * The service cannot be injected in that case, but the data are already spring validated beforehand.
	 */
	public CountryValidator() {
		this.countryService = null;
	}
	
	public CountryValidator(CountryService countryService) {
		this.countryService = countryService;
	}

	@Override
	public boolean isValid(String countryCode, ConstraintValidatorContext constraintValidatorContext) {
		if (countryService != null && !ObjectUtils.isEmpty(countryCode)) {
			return countryService.getReferenceData().stream().anyMatch(country -> country.getCode().equals(countryCode));
		}
		return true;
	}
}

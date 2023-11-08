package uk.gov.pmrv.api.aviationreporting.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryType {

    EEA_COUNTRY("EEACountry"),
    EEA_DEPENDENT_COUNTRY("EEADependentCountry"),
    EFTA_COUNTRY("EFTACountry"),
    ACCESSION("Accession"),
    CLOSELY_CONNECTED_TO_EEA_COUNTRY("CloselyConnectedToEEACountry"),
    THIRD_COUNTRY("3rdCountry"),
    UKETS_FLIGHTS_TO_EEA_REPORTED("UKETSFlightsToEEAReported"),
    UKETS_FLIGHTS_TO_EEA_NOT_REPORTED("UKETSFlightsToEEANotReported");

    private final String description;
}

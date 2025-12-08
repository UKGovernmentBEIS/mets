package uk.gov.pmrv.api.integration.registry.accountcreated.common.validation;


import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;

public enum RegistryAccountHolderType {
    INDIVIDUAL,
    ORGANISATION;


    public static RegistryAccountHolderType fromLegalEntityType(LegalEntityType legalEntityType) {
        return LegalEntityType.SOLE_TRADER.equals(legalEntityType)
                ? INDIVIDUAL
                : ORGANISATION;
    }

    public static RegistryAccountHolderType fromLegalStatusType(OrganisationLegalStatusType legalStatusType) {
        return OrganisationLegalStatusType.INDIVIDUAL.equals(legalStatusType)
                ? INDIVIDUAL
                : ORGANISATION;
    }
}

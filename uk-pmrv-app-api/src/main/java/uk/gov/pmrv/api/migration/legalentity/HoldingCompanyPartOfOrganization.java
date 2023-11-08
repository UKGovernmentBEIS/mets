package uk.gov.pmrv.api.migration.legalentity;

import java.util.Objects;

public enum HoldingCompanyPartOfOrganization {
    YES,
    NO;

    public static HoldingCompanyPartOfOrganization convertFrom(String belongsToHoldingCompany) {
        if (Objects.isNull(belongsToHoldingCompany) || !YES.toString().equals(belongsToHoldingCompany.toUpperCase())) {
            return NO;
        }
        return YES;
    }
}

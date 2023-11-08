package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuelUpliftSupplierRecordType {

    FUEL_DELIVERY_NOTES("Fuel delivery notes"),
    FUEL_INVOICES("Fuel invoices");

    private String description;
}

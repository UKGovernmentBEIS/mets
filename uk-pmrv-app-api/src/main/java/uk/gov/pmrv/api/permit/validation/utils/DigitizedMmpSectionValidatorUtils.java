package uk.gov.pmrv.api.permit.validation.utils;

public class DigitizedMmpSectionValidatorUtils {
    public static final String ERROR_NO_SUB_INSTALLATIONS =
            "At least one sub-installation must be provided";
    public static final String ERROR_OPRED_SUBINSTALLATION =
            "User is OPRED and has sub installations or user is not OPRED and doesn't have sub installations.";
    public static final String ERROR_DUPLICATE_SUBINSTALLATION_TYPES =
            "Sub installation types do not appear uniquely.";
    public static final String ERROR_INVALID_SUB_INSTALLATION_SIZE= "The number of sub installations is invalid. " +
            "At most 10 sub installations of the product benchmark type and 7 of the fallback approach type must exist.";
    public static final String ERROR_FUEL_ELECTRICITY_EXCHANGEABILITY =
            "At least one sub installation supports Fuel and Electricity Exchangeability and does not have it " +
                    "or at least one sub installation does not support Fuel and Electricity Exchangeability and does have it.";
    public static final String ERROR_DIRECTLY_ATTRIBUTABLE_EMISSIONS =
            "At least one sub installation has invalid Directly Attributable Emissions input.";
    public static final String ERR0R_FUEL_INPUT_DATA_SOURCE =
            "At least one sub installation has invalid Fuel Input and Relevant Emission Factor data source input.";
    public static final String ERR0R_IMPORTED_EXPORTED_MEASURABLE_HEAT_DATA_SOURCE =
            "At least one sub installation has invalid Imported Exported Measurable Heat data source input.";
    public static final String ERROR_INVALID_WASTE_GAS_INPUT =
            "At least one sub installation has invalid waste gas input.";
    public static final String ERROR_INVALID_SPECIAL_PRODUCT_INPUT =
            "At least one sub installation has invalid special product input.";
    public static final String ERROR_INVALID_ANNUAL_LEVEL_INPUT =
            "At least one sub installation has invalid annual level input.";
    public static final String ERR0R_INVALID_MEASURABLE_HEAT =
            "At least one sub installation has invalid measurable heat input.";
    public static final String DIGITIZED_MMP_FLAG_CONFIG_KEY = "ui.features.digitized-mmp";
    public static final String ERROR_INVALID_FUEL_INPUT_FLOWS_DATASOURCES = "Invalid fuel input flows datasources";
    public static final String ERROR_INVALID_MEASURABLE_HEAT_FLOWS_DATASOURCES = "Invalid measurable heat flows datasources";
    public static final String ERROR_INVALID_WASTE_GAS_FLOWS_DATASOURCES = "Invalid waste gas flows data sources";
}

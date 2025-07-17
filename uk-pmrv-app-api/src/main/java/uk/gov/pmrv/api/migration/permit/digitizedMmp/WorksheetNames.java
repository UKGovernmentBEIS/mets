package uk.gov.pmrv.api.migration.permit.digitizedMmp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@AllArgsConstructor
public enum WorksheetNames {
    C_INSTALLATION_DESCRIPTION(Values.C_INSTALLATION_DESCRIPTION),
    D_METHODS_PROCEDURES(Values.D_METHODS_PROCEDURES),
    E_ENERGY_FLOWS(Values.E_ENERGY_FLOWS),
    F_PRODUCT_BM(Values.F_PRODUCT_BM),
    G_FALLBACK(Values.G_FALLBACK),
    H_SPECIAL_BM(Values.H_SPECIAL_BM);

    /** The name */
    private final String name;

    @UtilityClass
    public static class Values {
        public final String C_INSTALLATION_DESCRIPTION = "C_InstallationDescription";
        public final String D_METHODS_PROCEDURES = "D_MethodsProcedures";
        public final String E_ENERGY_FLOWS = "E_EnergyFlows";
        public final String F_PRODUCT_BM = "F_ProductBM";
        public final String G_FALLBACK = "G_Fall-back";
        public final String H_SPECIAL_BM = "H_SpecialBM";
    }
}

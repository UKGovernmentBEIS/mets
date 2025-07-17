package uk.gov.pmrv.api.migration.permit.digitizedMmp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@AllArgsConstructor
public enum MmpFileType {
    MMP(Values.MMP),
    FLOW_DIAGRAM(Values.FLOW_DIAGRAM);

    /** The name */
    private final String name;

    @UtilityClass
    public static class Values {
        public final String MMP = "MMP";
        public final String FLOW_DIAGRAM = "FLOW_DIAGRAM";
    }
}

package uk.gov.pmrv.api.account.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * Enumerates the Location type.
 */
@Getter
@AllArgsConstructor
public enum LocationType {

    ONSHORE(Values.ONSHORE),
    OFFSHORE (Values.OFFSHORE),
    ONSHORE_STATE (Values.ONSHORE_STATE);

    /** The name */
    private final String name;

    @UtilityClass
    public static class Values {
        public final String ONSHORE = "Onshore";
        public final String OFFSHORE = "Offshore";
        public final String ONSHORE_STATE = "Onshore_state";
    }
}

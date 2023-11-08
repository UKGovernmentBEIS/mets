package uk.gov.pmrv.api.competentauthority;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

/**
 * Competent authorities.
 */
@Getter
@AllArgsConstructor
public enum CompetentAuthorityEnum {

    ENGLAND("EA", "E"),
    NORTHERN_IRELAND("NIEA", "N"),
    OPRED("DECC", "D"),
    SCOTLAND("SEPA", "S"),
    WALES("NRW", "W"),
    ;

    private final String code;
    private final String oneLetterCode;

    
    public String getLogoPath() {
        return this.name().toLowerCase() + File.separator + "logo.jpg";
    }
    
}

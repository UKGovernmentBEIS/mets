package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnnexIIPoint2 {
    NAPHTHA_GASOLINE_HYDROTREATER("Naphtha/Gasoline Hydrotreater"),
    AROMATIC_SOLVENT_EXTRACTION("Aromatic Solvent Extraction"),
    TDP_TDA("TDP/ TDA"),
    HYDRODEALKYLATION("Hydrodealkylation"),
    XYLENE_ISOMERISATION("Xylene Isomerisation"),
    PARAXYLENE_PRODUCTION("Paraxylene production"),
    CYCLOHEXANE_PRODUCTION("Cyclohexane production"),
    CUMENE_PRODUCTION("Cumene production");

    private final String description;

    public static AnnexIIPoint2 getByValue(String value) {
        return Arrays.stream(values())
                .filter(section -> section.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}

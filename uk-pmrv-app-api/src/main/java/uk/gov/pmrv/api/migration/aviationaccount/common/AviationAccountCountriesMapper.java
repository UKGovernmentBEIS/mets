package uk.gov.pmrv.api.migration.aviationaccount.common;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class AviationAccountCountriesMapper {

    public static Map<String, String> getEtswapToPmrvCountriesMapping() {
        return Map.of(
            "Turkey", "Türkiye",
            "Hong Kong Special Administrative Region of China", "Hong Kong",
            "Taiwan - Republic of China", "Taiwan",
            "Brunei Darussalam", "Brunei",
            "Cayman Islands", "Cayman",
            "Faeroe Islands", "Faroe Islands");
    }
}

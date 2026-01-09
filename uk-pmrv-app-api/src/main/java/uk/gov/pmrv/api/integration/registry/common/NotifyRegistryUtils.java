package uk.gov.pmrv.api.integration.registry.common;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class NotifyRegistryUtils {
    public static String REQUEST_LOG_FORMAT = "Registry integration: '{}' - accountId: '{}' - integration point: '{}' - data: '{}'";
    public static String RESPONSE_LOG_FORMAT = "Registry integration: '{}' - registry id '{}' - integration point: '{}' - data: '{}'";
    public static String MISSING_REGISTRY_ID_ERROR_MESSAGE = "No Registry ID exists in METS account";
    public static String OPERATOR_ID_INTEGRATION_POINT_KEY = "set-operator-id";
    public static String ACCOUNT_CREATED_INTEGRATION_POINT_KEY = "account-created";
    public static String AVIATION_SERVICE_KEY = "Aviation";
    public static String INSTALLATION_SERVICE_KEY = "Installation";

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String toRegistryRegionCode(CompetentAuthorityEnum competentAuthority) {
        return switch (competentAuthority) {
            case ENGLAND -> "EA";
            case NORTHERN_IRELAND -> "DAERA";
            case OPRED -> "OPRED";
            case SCOTLAND -> "SEPA";
            case WALES -> "NRW";
        };
    }

    public static String replaceGBCountryCode(String country) {
        List<String> gbCountryCodes = Arrays.asList("GB-ENG", "GB-NIR", "GB-SCT", "GB-WLS");
        if (ObjectUtils.isEmpty(country)) {return country;}
        if (gbCountryCodes.contains(country)) {return "UK";}
        return country;
    }


}

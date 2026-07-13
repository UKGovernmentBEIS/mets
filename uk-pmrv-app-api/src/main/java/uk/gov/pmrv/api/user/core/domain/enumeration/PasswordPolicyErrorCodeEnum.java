package uk.gov.pmrv.api.user.core.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * The Keycloak client rest points enum.
 */
@Getter
@AllArgsConstructor
public enum PasswordPolicyErrorCodeEnum {

    INVALID_MIN_LENGTH("invalidPasswordMinLengthMessage"),
    INVALID_MAX_LENGTH("invalidPasswordMaxLengthMessage"),
    BLACKLISTED_PATTERN("invalidPasswordRegexBlacklistMessage"),
    PWNED("invalidPasswordPwnedMessage"),
    PWNED_SERVICE_UNAVAILABLE("invalidPasswordPwnedServiceUnavailableMessage"),
    OTHER(null);

    private final String keycloakError;


    @JsonCreator
    public static PasswordPolicyErrorCodeEnum fromValue(String value) {
        return Arrays.stream(values())
                .filter(e -> Objects.equals(e.keycloakError, value))
                .findFirst()
                .orElse(OTHER);
    }

}

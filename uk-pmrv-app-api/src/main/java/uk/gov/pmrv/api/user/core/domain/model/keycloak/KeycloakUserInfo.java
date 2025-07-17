package uk.gov.pmrv.api.user.core.domain.model.keycloak;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserInfo {

    private String id;

    private String firstName;

    private String lastName;
    
    private String email;

    private boolean enabled;

    private Map<String, String> attributes;

    @JsonAnyGetter
    public Map<String, String> getAttributes() {
        return attributes;
    }

}

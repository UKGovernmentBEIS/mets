package uk.gov.pmrv.api.web.controller.terms;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTermsVersionDTO {
    private final Short termsVersion;
}
package uk.gov.pmrv.api.migration.permit.activationdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsActivationDate {

    private String etsAccountId;
    private LocalDate activationDate;
}

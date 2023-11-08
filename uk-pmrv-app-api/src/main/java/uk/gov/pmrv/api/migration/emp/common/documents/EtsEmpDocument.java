package uk.gov.pmrv.api.migration.emp.common.documents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpDocument {

    @NotNull
    private Long accountId;

    @NotBlank
    private String id;

    @NotBlank
    private String etsFilename;

    @NotBlank
    private String migratedFilename;
}

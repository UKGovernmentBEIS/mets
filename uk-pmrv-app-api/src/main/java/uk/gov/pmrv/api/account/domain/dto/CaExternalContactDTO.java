package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaExternalContactDTO {

    private Long id;

    private String name;

    private String email;

    private String description;

    private LocalDateTime lastUpdatedDate;
}

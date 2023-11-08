package uk.gov.pmrv.api.common.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingRequest {

	@NotNull
    @Min(value = 0, message = "{parameter.page.typeMismatch}")
    private Long pageNumber;
    
    @NotNull
    @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
    private Long pageSize;
}

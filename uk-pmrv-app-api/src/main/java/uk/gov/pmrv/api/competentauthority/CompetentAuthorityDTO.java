package uk.gov.pmrv.api.competentauthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompetentAuthorityDTO {

    @EqualsAndHashCode.Include()
    private CompetentAuthorityEnum id;

    private String email;

    private String name;

}

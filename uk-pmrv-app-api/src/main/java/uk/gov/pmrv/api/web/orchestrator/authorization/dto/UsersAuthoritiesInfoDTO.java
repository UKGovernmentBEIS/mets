package uk.gov.pmrv.api.web.orchestrator.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UsersAuthoritiesInfoDTO {

    @Builder.Default
    private List<UserAuthorityInfoDTO> authorities = new ArrayList<>();

    private boolean editable;
}

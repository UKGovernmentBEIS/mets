package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint1;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProduct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefineryProductsSP extends SpecialProduct {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<@NotNull AnnexIIPoint1> refineryProductsRelevantCWTFunctions = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min =1, max = 6)
    private List<@Valid @NotNull RefineryProductsQuantitiesOfSupplementalFieldDataSource> refineryProductsDataSources = new ArrayList<>();
}

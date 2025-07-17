package uk.gov.pmrv.api.web.controller.workflow.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersService;

import java.util.List;
import java.util.Optional;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/{accountType}/items/assigned-to-others")
@Tag(name = "Items Assigned To Others")
@Validated
@RequiredArgsConstructor
public class ItemAssignedToOthersController {

    private final List<ItemAssignedToOthersService> services;

    @GetMapping
    @Operation(summary = "Retrieves the items assigned to users different than the logged-in user of the same role type who participate in the same accounts")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ItemDTOResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<ItemDTOResponse> getAssignedToOthersItems(
            @Parameter(hidden = true) AppUser user,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
            @RequestParam("page") @Parameter(name = "page", description = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam("size") @Parameter(name = "size", description = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Long pageSize) {

        Optional<ItemAssignedToOthersService> itemService = services.stream()
                .filter(itemAssignedToOthersService -> itemAssignedToOthersService.getRoleType().equals(user.getRoleType()))
                .findFirst();

        return itemService.map(itemAssignedToOthersService ->
		new ResponseEntity<>(itemAssignedToOthersService.getItemsAssignedToOthers(user, accountType,
				PagingRequest.builder().pageNumber(page).pageSize(pageSize).build()), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ItemDTOResponse.emptyItemDTOResponse(), HttpStatus.OK));
    }
}

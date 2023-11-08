package uk.gov.pmrv.api.web.controller.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateUpdateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateQueryService;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateUpdateService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Notification Templates")
public class NotificationTemplateController {

    private final NotificationTemplateQueryService notificationTemplateQueryService;
    private final NotificationTemplateUpdateService notificationTemplateUpdateService;

    @GetMapping(path = "/v1.0/{accountType}/notification-templates")
    @Operation(summary = "Retrieves the notification templates associated with current user")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateSearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<TemplateSearchResults> getCurrentUserNotificationTemplates(
            @Parameter(hidden = true) PmrvUser pmrvUser,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
            @RequestParam(value = "role")  @NotNull @Parameter(name = "role", description = "The role type") RoleType roleType,
            @RequestParam(value = "term", required = false) @Size(min = 3, max=256) @Parameter(name = "term", description = "The term to search") String term,
            @RequestParam(value = "page") @NotNull @Parameter(name = "page", description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam(value = "size") @NotNull @Parameter(name = "size", description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Long pageSize
    ) {
        return new ResponseEntity<>(
            notificationTemplateQueryService.getNotificationTemplatesBySearchCriteria(
                NotificationTemplateSearchCriteria.builder()
                    .competentAuthority(pmrvUser.getCompetentAuthority())
                    .accountType(accountType)
                    .term(term)
                    .roleType(roleType)
                    .paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
                    .build()
            ),
            HttpStatus.OK);
    }

    @GetMapping(path = "/v1.0/notification-templates/{id}")
    @Operation(summary = "Retrieves the notification template with the provided id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotificationTemplateDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<NotificationTemplateDTO> getNotificationTemplateById(
            @Parameter(description = "The notification template id") @PathVariable("id") Long id) {
        return new ResponseEntity<>(notificationTemplateQueryService.getManagedNotificationTemplateById(id), HttpStatus.OK);
    }

    @PutMapping(path = "/v1.0/notification-templates/{id}")
    @Operation(summary = "Updates the notification template with the provided id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#id")
    public ResponseEntity<Void> updateNotificationTemplate(
            @PathVariable("id") @Parameter(description = "The notification template id") Long id,
            @RequestBody @Valid @Parameter(description = "The data to update the notification template", required = true)
                    NotificationTemplateUpdateDTO templateUpdateDTO) {
        notificationTemplateUpdateService.updateNotificationTemplate(id, templateUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityService;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.WorkflowTemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationDocumentTemplateCommonParamsProviderTest {

    @InjectMocks
    private InstallationDocumentTemplateCommonParamsProvider provider;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;

    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private DateService dateService;
    @Mock
    private CompetentAuthorityService competentAuthorityService;


    @Test
    void constructCommonTemplateParams() throws IOException {
        LocalDateTime submissionDate = LocalDateTime.of(2022, 1, 1, 1, 1);
        Long accountId = 1L;
        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();

        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .build();
        Request request = Request.builder().id("1").accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE)
                .submissionDate(submissionDate)
                .payload(requestPayload)
                .build();
        InstallationAccountWithoutLeHoldingCompanyDTO accountDetails = InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .name("accountname")
                .emitterType(EmitterType.GHGE)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .accountType(AccountType.INSTALLATION)
                .siteName("accountsitename")
                .location(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("gridRef")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .line2("line2")
                                .city("city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                        .name("lename")
                        .address(AddressDTO.builder()
                                .line1("le_line1")
                                .line2("le_line2")
                                .city("le_city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .installationCategory(InstallationCategory.B)
                .build();
        String signatory = "signatoryUserId";
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        String permitId = "permitId";

        String caCentralInfo = "ca central info";

        when(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(request.getAccountId())).thenReturn(accountDetails);
        when(permitQueryService.getPermitIdByAccountId(request.getAccountId())).thenReturn(Optional.of(permitId));
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY))
            .thenReturn(Optional.of("primaryId"));
        when(userAuthService.getUserByUserId("primaryId")).thenReturn(accountPrimaryContact);
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.SERVICE))
            .thenReturn(Optional.of("serviceId"));
        when(userAuthService.getUserByUserId("serviceId"))
            .thenReturn(UserInfoDTO.builder()
                .firstName("service fn")
                .lastName("service ln")
                .userId("serviceContact")
                .email("serviceContact@email.com")
                .build());
        when(documentTemplateLocationInfoResolver.constructLocationInfo(accountDetails.getLocation()))
            .thenReturn("accountLocation");
        when(documentTemplateLocationInfoResolver.constructAddressInfo(accountDetails.getLegalEntity().getAddress()))
            .thenReturn("leLocation");

        RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
                .firstName("signtoryFn").lastName("signatoryLn").jobTitle("signatoryJobTitle")
                .signature(FileInfoDTO.builder().name("signature.pdf").uuid(UUID.randomUUID().toString()).build())
                .build();
        when(regulatorUserAuthService.getRegulatorUserById(signatory)).thenReturn(signatoryUser);
        when(appProperties.getCompetentAuthorityCentralInfo()).thenReturn(caCentralInfo);
        when(competentAuthorityService.getCompetentAuthority(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION)).thenReturn(ca);

        FileDTO signatorySignature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize("content".length())
                .fileType("type")
                .build();
        when(userAuthService.getUserSignature(signatoryUser.getSignature().getUuid()))
                .thenReturn(Optional.of(signatorySignature));
        when(dateService.getLocalDateTime()).thenReturn(LocalDateTime.of(2021, 1, 2, 1, 1));

        //invoke
        TemplateParams result = provider.constructCommonTemplateParams(request, signatory);

        assertThat(result).isEqualTo(TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(ca)
                        .logo(Files.readAllBytes(Paths.get("src", "main", "resources", "images", "ca", CompetentAuthorityEnum.ENGLAND.getLogoPath())))
                        .build())
                .competentAuthorityCentralInfo(caCentralInfo)
                .signatoryParams(SignatoryTemplateParams.builder()
                        .fullName(signatoryUser.getFullName())
                        .jobTitle(signatoryUser.getJobTitle())
                        .signature(signatorySignature.getFileContent())
                        .build())
                .accountParams(InstallationAccountTemplateParams.builder()
                        .name(accountDetails.getName())
                        .accountType(accountDetails.getAccountType())
                        .siteName(accountDetails.getSiteName())
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .emitterType(accountDetails.getEmitterType().name())
                        .location("accountLocation")
                        .legalEntityName(accountDetails.getLegalEntity().getName())
                        .legalEntityLocation("leLocation")
                        .primaryContact(accountPrimaryContact.getFullName())
                        .primaryContactEmail(accountPrimaryContact.getEmail())
                        .serviceContact("service fn service ln")
                        .serviceContactEmail("serviceContact@email.com")
                        .installationCategory("Category B")
                        .build())
                .workflowParams(WorkflowTemplateParams.builder()
                    .requestId("1")
                    .requestType(RequestType.PERMIT_ISSUANCE.name())
                    .requestTypeInfo("Permit Application")
                    .requestSubmissionDate(Date.from(submissionDate.atZone(ZoneId.systemDefault()).toInstant()))
                    .requestEndDate(LocalDateTime.of(2021, 1, 2, 1, 1))
                    .build())
                .permitId(permitId)
                .build());

        verify(installationAccountQueryService, times(1)).getAccountWithoutLeHoldingCompanyDTOById(request.getAccountId());
        verify(permitQueryService, times(1)).getPermitIdByAccountId(request.getAccountId());
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.SERVICE);
        verify(userAuthService, times(1)).getUserByUserId("primaryId");
        verify(userAuthService, times(1)).getUserByUserId("serviceId");
        verify(documentTemplateLocationInfoResolver, times(1)).constructLocationInfo(accountDetails.getLocation());
        verify(documentTemplateLocationInfoResolver, times(1)).constructAddressInfo(accountDetails.getLegalEntity().getAddress());
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(signatory);
        verify(userAuthService, times(1)).getUserSignature(signatoryUser.getSignature().getUuid());
        verify(appProperties, times(1)).getCompetentAuthorityCentralInfo();
        verify(dateService, times(1)).getLocalDateTime();
    }

    @Test
    void constructCommonTemplateParams_signature_not_exist() {
    	LocalDateTime submissionDate = LocalDateTime.now();
        Long accountId = 1L;
        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();

        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .build();
        String signatory = "signatoryUserId";
        Request request = Request.builder().id("1").accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE).submissionDate(submissionDate)
                .payload(requestPayload)
                .build();

        InstallationAccountWithoutLeHoldingCompanyDTO accountDetails = InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .name("accountname")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                        .name("lename")
                        .address(AddressDTO.builder()
                                .line1("le_line1")
                                .line2("le_line2")
                                .city("le_city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .installationCategory(InstallationCategory.B)
                .build();

    	String permitId = "permit";

        when(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(request.getAccountId())).thenReturn(accountDetails);
        when(permitQueryService.getPermitIdByAccountId(request.getAccountId())).thenReturn(Optional.of(permitId));
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY))
            .thenReturn(Optional.of("primaryId"));
        when(userAuthService.getUserByUserId("primaryId"))
            .thenReturn(UserInfoDTO.builder()
                .firstName("primary fn")
                .lastName("primary ln")
                .userId("primaryContact")
                .email("primaryContact@email.com")
                .build());
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.SERVICE))
            .thenReturn(Optional.of("serviceId"));
        when(userAuthService.getUserByUserId("serviceId"))
            .thenReturn(UserInfoDTO.builder()
                .firstName("service fn")
                .lastName("service ln")
                .userId("serviceContact")
                .email("serviceContact@email.com")
                .build());
        when(documentTemplateLocationInfoResolver.constructLocationInfo(accountDetails.getLocation()))
            .thenReturn("accountLocation");
        when(documentTemplateLocationInfoResolver.constructAddressInfo(accountDetails.getLegalEntity().getAddress()))
            .thenReturn("leLocation");

        RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
                .firstName("signtoryFn").lastName("signatoryLn").jobTitle("signatoryJobTitle")
                .build();
        when(regulatorUserAuthService.getRegulatorUserById(signatory)).thenReturn(signatoryUser);
        when(competentAuthorityService.getCompetentAuthority(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION)).thenReturn(ca);

        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> provider.constructCommonTemplateParams(request, signatory));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.USER_SIGNATURE_NOT_EXIST);
    }
}

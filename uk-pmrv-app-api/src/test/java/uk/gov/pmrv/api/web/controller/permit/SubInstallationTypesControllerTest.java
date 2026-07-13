package uk.gov.pmrv.api.web.controller.permit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationTypeDetails;
import uk.gov.pmrv.api.permit.service.monitoringmethodologyplan.SubInstallationService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class SubInstallationTypesControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/sub-installation-types";

    private MockMvc mockMvc;

    @InjectMocks
    private SubInstallationTypesController controller;

    @Mock
    private SubInstallationService subInstallationService;

    @Mock
    private Validator validator;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSubInstallationTypesDetails_returnsResults() throws Exception {
        List<SubInstallationTypeDetails> results = List.of(
                SubInstallationTypeDetails.builder()
                        .subInstallationType(SubInstallationType.HYDROGEN)
                        .isCoveredByUKCBAM(false)
                        .isValid(false)
                        .build(),
                SubInstallationTypeDetails.builder()
                        .subInstallationType(SubInstallationType.HYDROGEN_CBAM)
                        .isCoveredByUKCBAM(true)
                        .isValid(true)
                        .build()
        );

        when(subInstallationService.getSubInstallationTypesDetails())
                .thenReturn(results);

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders.get(CONTROLLER_PATH + "/details")
                ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                "\"subInstallationType\":\"HYDROGEN\"",
                "\"coveredByUKCBAM\":false",
                "\"valid\":false",
                "\"subInstallationType\":\"HYDROGEN_CBAM\"",
                "\"coveredByUKCBAM\":true",
                "\"valid\":true"
        );

        verify(subInstallationService, times(1)).getSubInstallationTypesDetails();
    }
}

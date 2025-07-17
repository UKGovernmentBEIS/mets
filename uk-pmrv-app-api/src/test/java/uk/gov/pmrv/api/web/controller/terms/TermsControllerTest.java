package uk.gov.pmrv.api.web.controller.terms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.gov.netz.api.terms.Terms;
import uk.gov.netz.api.terms.TermsDTO;
import uk.gov.netz.api.terms.TermsMapper;
import uk.gov.netz.api.terms.TermsService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TermsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TermsController termsController;

    @Mock
    private TermsService termsService;

    @Mock
    private TermsMapper termsMapper;

    private static final String TERMS_URL = "myurl";

    private static final short VERSION = 1;

    public static final String TERMS_CONTROLLER_PATH = "/v1.0/terms";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(termsController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void getLatestTerms() throws Exception {

        when(termsService.getLatestTerms()).thenReturn(buildMockTermsEntity());

        when(termsMapper.transformToTermsDTO(any())).thenReturn(buildMockTermsDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(TERMS_CONTROLLER_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value(TERMS_URL));
    }

    private Terms buildMockTermsEntity() {
        Terms terms = new Terms();
        terms.setId(1L);
        terms.setUrl(TERMS_URL);
        terms.setVersion(VERSION);
        return terms;
    }

    private TermsDTO buildMockTermsDTO() {
        TermsDTO termsDTO = new TermsDTO();
        termsDTO.setUrl(TERMS_URL);
        termsDTO.setVersion(VERSION);
        return termsDTO;
    }
}

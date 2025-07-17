package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class DigitizedPlanProceduresTest {

    private static Validator validator;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void validate_procedure_json_mapping() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(String.format("src/test/resources/files/mmp/%s.json","digitizedplan_procedures"))));
        Map<ProcedureType,Procedure> procedures =  objectMapper.readValue(
                jsonContent.getBytes(),
                objectMapper.getTypeFactory().constructMapType(
                        Map.class,
                        ProcedureType.class,
                        Procedure.class
                )
        );
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(procedures);
        assertNotNull(procedures);
        assertTrue(constraintViolations.isEmpty());
        assertEquals(4, procedures.size());
        assertEquals("nameA", procedures.get(ProcedureType.ASSIGNMENT_OF_RESPONSIBILITIES).getProcedureName());
        assertEquals("descriptionA", procedures.get(ProcedureType.ASSIGNMENT_OF_RESPONSIBILITIES).getProcedureDescription());
        assertEquals("nameB", procedures.get(ProcedureType.MONITORING_PLAN_APPROPRIATENESS).getProcedureName());
        assertEquals("descriptionB", procedures.get(ProcedureType.MONITORING_PLAN_APPROPRIATENESS).getProcedureDescription());
        assertEquals("nameC", procedures.get(ProcedureType.DATA_FLOW_ACTIVITIES).getProcedureName());
        assertEquals("itSystemC", procedures.get(ProcedureType.DATA_FLOW_ACTIVITIES).getItSystemUsed());
        assertEquals("nameD", procedures.get(ProcedureType.CONTROL_ACTIVITIES).getProcedureName());
        assertEquals("locationD", procedures.get(ProcedureType.CONTROL_ACTIVITIES).getLocationOfRecords());
    }


}

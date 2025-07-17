package uk.gov.pmrv.api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElementsUtils;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElementsUtilsEqualResult;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElementsValidator;
import uk.gov.pmrv.api.CustomLocalValidatorFactoryBean;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UniqueElementsUtilsTest {

    private Validator validator;
    private final List<ConstraintValidator<?,?>> customConstraintValidators =
        Collections.singletonList(new UniqueElementsValidator());

    @BeforeEach
    public void setUp() {
         try (ValidatorFactory factory = new CustomLocalValidatorFactoryBean(customConstraintValidators)) {
            validator = factory.getValidator();
        }
    }

    @Test
    void uniqueElementsUtilsEqual_objectsAreEqual_returnTrueAndNoViolatedFields() throws IllegalAccessException {
        AircraftTypeInfo a1 = AircraftTypeInfo.builder().model("model1").manufacturer("man1").designatorType("des1").build();

        AircraftTypeDetailsCorsia ad1 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a1)
                .subtype("subtype1")
                .numberOfAircrafts(10L)
                .build();

        AircraftTypeDetailsCorsia ad2 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a1)
                .subtype("subtype2")
                .numberOfAircrafts(20L)
                .build();

        UniqueElementsUtilsEqualResult result = UniqueElementsUtils.equal(ad1,ad2);

        assertThat(result.getResult()).isTrue();
        assertThat(result.getViolatedFields()).isEmpty();
    }

    @Test
    void uniqueElementsUtilsEqual_objectsAreNotEqual_returnFalseAndViolatedFields() throws IllegalAccessException {
        AircraftTypeInfo a1 = AircraftTypeInfo.builder().model("model1").manufacturer("man1").designatorType("des1").build();
        AircraftTypeInfo a2 = AircraftTypeInfo.builder().model("model2").manufacturer("man2").designatorType("des2").build();

        AircraftTypeDetailsCorsia ad1 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a1)
                .subtype("subtype1")
                .numberOfAircrafts(10L)
                .build();

        AircraftTypeDetailsCorsia ad2 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a2)
                .subtype("subtype2")
                .numberOfAircrafts(20L)
                .build();

        UniqueElementsUtilsEqualResult result = UniqueElementsUtils.equal(ad1,ad2);

        assertThat(result.getResult()).isFalse();
        assertThat(result.getViolatedFields()).containsExactly("aircraftTypeInfo");
    }

    @Test
    void uniqueElementsValidator_withNullInput_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ()-> validator.validate(null));
    }

    @Test
    void uniqueElementsValidator_withNullIterable_shouldReturnNoViolation() {
       EmpEmissionSourcesCorsia empEmissionSourcesCorsia = EmpEmissionSourcesCorsia.builder().aircraftTypes(null).build();
       Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(empEmissionSourcesCorsia);
       assertThat(violations.stream().filter(v->
            v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("UniqueElements")
        ).toList()).hasSize(0);
    }

    @Test
    void uniqueElementsValidator_withEmptyIterable_shouldReturnNoViolation() {
       EmpEmissionSourcesCorsia empEmissionSourcesCorsia = EmpEmissionSourcesCorsia.builder().aircraftTypes(Set.of()).build();
       Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(empEmissionSourcesCorsia);
       assertThat(violations.stream().filter(v->
            v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("UniqueElements")
        ).toList()).hasSize(0);
    }

    @Test
    public void uniqueElementsValidator_noViolations_shouldReturnNoViolation() {
        AircraftTypeInfo a1 = AircraftTypeInfo.builder().model("model1").manufacturer("man1").designatorType("des1").build();
        AircraftTypeInfo a2 = AircraftTypeInfo.builder().model("model2").manufacturer("man2").designatorType("des2").build();

        AircraftTypeDetailsCorsia ad1 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a1)
                .subtype("subtype1")
                .numberOfAircrafts(10L)
                .build();

        AircraftTypeDetailsCorsia ad2 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a2)
                .subtype("subtype2")
                .numberOfAircrafts(20L)
                .build();

        EmpEmissionSourcesCorsia empEmissionSourcesCorsia = EmpEmissionSourcesCorsia
                .builder()
                .aircraftTypes(Set.of(ad1,ad2))
                .build();

        Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(empEmissionSourcesCorsia);
        assertThat(violations.stream().filter(v->
            v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("UniqueElements")
        ).toList()).hasSize(0);
    }

    @Test
    public void uniqueElementsValidator_withViolations_shouldReturnViolation() {

        AircraftTypeInfo a = AircraftTypeInfo.builder().model("model1").manufacturer("man1").designatorType("des1").build();


        AircraftTypeDetailsCorsia ad1 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a)
                .subtype("subtype1")
                .numberOfAircrafts(10L)
                .build();

        AircraftTypeDetailsCorsia ad2 = AircraftTypeDetailsCorsia
                .builder()
                .aircraftTypeInfo(a)
                .subtype("subtype2")
                .numberOfAircrafts(20L)
                .build();

        EmpEmissionSourcesCorsia empEmissionSourcesCorsia = EmpEmissionSourcesCorsia
                .builder()
                .aircraftTypes(Set.of(ad1,ad2))
                .build();

        Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(empEmissionSourcesCorsia);
        assertThat(violations.stream().filter(v->
            v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("UniqueElements")
        ).toList()).hasSize(1);
    }
}

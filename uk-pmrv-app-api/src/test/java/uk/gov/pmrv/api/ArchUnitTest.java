package uk.gov.pmrv.api;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static java.util.stream.Collectors.toList;
import static uk.gov.pmrv.api.ArchUnitTest.BASE_PACKAGE;

@AnalyzeClasses(packages = BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTest {

    static final String BASE_PACKAGE = "uk.gov.pmrv.api";

    static final String COMMON_PACKAGE = BASE_PACKAGE + ".common..";
    static final String NOTIFICATIONAPI_PACKAGE = BASE_PACKAGE + ".notificationapi..";
    static final String NOTIFICATION_PACKAGE = BASE_PACKAGE + ".notification..";
    static final String AUTHORIZATION_PACKAGE = BASE_PACKAGE + ".authorization..";
    static final String CA_PACKAGE = BASE_PACKAGE + ".competentauthority..";
    static final String VERIFICATION_BODY_PACKAGE = BASE_PACKAGE + ".verificationbody..";
    static final String USER_PACKAGE = BASE_PACKAGE + ".user..";
    static final String ACCOUNT_PACKAGE = BASE_PACKAGE + ".account..";
    static final String PERMIT_PACKAGE = BASE_PACKAGE + ".permit..";
    static final String REPORTING_PACKAGE = BASE_PACKAGE + ".reporting..";

    static final String EMP_PACKAGE = BASE_PACKAGE + ".emissionsmonitoringplan..";
    static final String AVIATION_REPORTING_PACKAGE = BASE_PACKAGE + ".aviationreporting..";

    static final String WORKFLOW_PACKAGE = BASE_PACKAGE + ".workflow..";


    static final String MIGRATION_PACKAGE = BASE_PACKAGE + ".migration..";
    static final String WEB_PACKAGE = BASE_PACKAGE + ".web..";

    static final List<String> ALL_PACKAGES = List.of(
            COMMON_PACKAGE,
            NOTIFICATIONAPI_PACKAGE,
            NOTIFICATION_PACKAGE,
            AUTHORIZATION_PACKAGE,
            CA_PACKAGE,
            VERIFICATION_BODY_PACKAGE,
            USER_PACKAGE,
            ACCOUNT_PACKAGE,
            PERMIT_PACKAGE,
            REPORTING_PACKAGE,
            WORKFLOW_PACKAGE,

            MIGRATION_PACKAGE,
            WEB_PACKAGE
    );

    /**
     CYCLIC2: verificationBody/authorization due to impact on Verifier authorities on VB status change (event)
     CYCLIC3: account/users
     CYCLIC4: permit/reporting for CalculationActivityDataMonitoringTier in CalculationParameterType
     **/

    @ArchTest
    public static final ArchRule commonPackageChecks =
            noClasses().that()
                    .resideInAPackage(COMMON_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            COMMON_PACKAGE));

    @ArchTest
    public static final ArchRule notificationPackageChecks =
            noClasses().that()
                    .resideInAPackage(NOTIFICATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                    		NOTIFICATIONAPI_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE));

    @ArchTest
    public static final ArchRule authorizationPackageChecks =
            noClasses().that()
                    .resideInAPackage(AUTHORIZATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            AUTHORIZATION_PACKAGE,
                            COMMON_PACKAGE,
                            CA_PACKAGE,
                            VERIFICATION_BODY_PACKAGE /* CYCLIC2: due to impact on Verifier authorities on VB status change */));

    @ArchTest
    public static final ArchRule competentAuthorityPackageChecks =
            noClasses().that()
                    .resideInAPackage(CA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            CA_PACKAGE,
                            COMMON_PACKAGE));

    @ArchTest
    public static final ArchRule verificationBodyPackageChecks =
            noClasses().that()
                    .resideInAPackage(VERIFICATION_BODY_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            VERIFICATION_BODY_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE));

    @ArchTest
    public static final ArchRule userPackageChecks =
            noClasses().that()
                    .resideInAPackage(USER_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            USER_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            NOTIFICATIONAPI_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE /* CYCLIC3: to get installation name for notification */,
                            CA_PACKAGE, /* for regulator invitation */
                            VERIFICATION_BODY_PACKAGE /* for verifier invitation */));
    @ArchTest
    public static final ArchRule accountPackageChecks =
            noClasses().that()
                    .resideInAPackage(ACCOUNT_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            ACCOUNT_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            USER_PACKAGE, /* CYCLIC3:  getServiceContactDetails */
                            VERIFICATION_BODY_PACKAGE,
                            NOTIFICATION_PACKAGE));

    @ArchTest
    public static final ArchRule permitPackageChecks =
            noClasses().that()
                    .resideInAPackage(PERMIT_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            PERMIT_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            ACCOUNT_PACKAGE,
                            REPORTING_PACKAGE /* CYCLIC4: for CalculationActivityDataMonitoringTier in CalculationParameterType*/));

    @ArchTest
    public static final ArchRule reportingPackageChecks =
            noClasses().that()
                    .resideInAPackage(REPORTING_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            REPORTING_PACKAGE,
                            COMMON_PACKAGE,
                            PERMIT_PACKAGE,
                            ACCOUNT_PACKAGE,
                            VERIFICATION_BODY_PACKAGE));

    @ArchTest
    public static final ArchRule empPackageChecks =
            noClasses().that()
                    .resideInAPackage(EMP_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            EMP_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            ACCOUNT_PACKAGE,
                            AVIATION_REPORTING_PACKAGE));

    @ArchTest
    public static final ArchRule aviationreportingPackageChecks =
            noClasses().that()
                    .resideInAPackage(AVIATION_REPORTING_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            AVIATION_REPORTING_PACKAGE,
                            COMMON_PACKAGE,
                            EMP_PACKAGE,
                            VERIFICATION_BODY_PACKAGE,
                            ACCOUNT_PACKAGE));

    @ArchTest
    public static final ArchRule workflowPackageChecks =
            noClasses().that()
                    .resideInAPackage(WORKFLOW_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            WORKFLOW_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            NOTIFICATIONAPI_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE,
                            PERMIT_PACKAGE,
                            REPORTING_PACKAGE,
                            USER_PACKAGE,
                            VERIFICATION_BODY_PACKAGE));



    private static String[] except(String... packages) {
        return ALL_PACKAGES.stream()
                .filter(p -> !Arrays.asList(packages).contains(p))
                .collect(toList())
                .toArray(String[]::new);
    }
}
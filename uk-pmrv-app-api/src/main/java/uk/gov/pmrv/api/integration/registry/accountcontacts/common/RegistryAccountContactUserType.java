package uk.gov.pmrv.api.integration.registry.accountcontacts.common;

public enum RegistryAccountContactUserType {

    OPERATOR_ADMIN("operator_admin"),
    OPERATOR("operator"),
    CONSULTANT_AGENT("consultant_agent"),
    EMITTER("emitter_contact");

    private final String roleCode;

    RegistryAccountContactUserType(String roleCode) {
        this.roleCode = roleCode;
    }

    public static RegistryAccountContactUserType fromRoleCode(String roleCode) {
        for (RegistryAccountContactUserType type : RegistryAccountContactUserType.values()) {
            if (type.roleCode.equals(roleCode)) {return type;}
        }
        throw new IllegalArgumentException("No registry account contact user type found for role code: " + roleCode);
    }
}

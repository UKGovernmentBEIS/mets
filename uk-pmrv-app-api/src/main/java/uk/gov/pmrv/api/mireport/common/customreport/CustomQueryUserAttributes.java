package uk.gov.pmrv.api.mireport.common.customreport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomQueryUserAttributes {
    
    FIRSTNAME("$userfirstname", "firstName"),
    LASTNAME("$userlastname", "lastName"),
    FULLNAME("$userfullname", "fullName"),
    TELEPHONE("$usertelephone", "telephone"),
    EMAIL("$useremail", "email"),
    JOB_TITLE("$userjobtitle", "jobTitle"),
    LAST_LOGIN_DATE("$userlastlogindate", "lastLoginDate");
    
    private final String keyword;
    private final String attribute;
    
    public Predicate<String> getPredicate() {
        return s -> s.endsWith(keyword);
    }
    
    public static List<Predicate<String>> getAllPredicates() {
        List<Predicate<String>> allPredicates = new ArrayList<Predicate<String>>();
        Arrays.asList(CustomQueryUserAttributes.values()).stream()
            .forEach(p -> allPredicates.add(p.getPredicate()));  
        
        return allPredicates;
    }
}
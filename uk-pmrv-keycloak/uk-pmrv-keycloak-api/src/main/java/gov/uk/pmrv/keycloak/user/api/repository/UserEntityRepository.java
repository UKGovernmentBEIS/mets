package gov.uk.pmrv.keycloak.user.api.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import org.keycloak.models.jpa.entities.UserEntity;

public class UserEntityRepository {

    private final EntityManager entityManager;

    public UserEntityRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<UserEntity> findUserEntities(List<String> userIds) {
        List<String> uniqueUserIds = userIds.stream().distinct().collect(Collectors.toList());
        List<UserEntity> results = new ArrayList<>();
        int partitionSize = 32000;
        
        for (int i = 0; i < uniqueUserIds.size(); i += partitionSize) {
            results.addAll(
                    entityManager.createQuery("select u from UserEntity u where u.id in (:userIds)", UserEntity.class)
                    .setParameter("userIds", uniqueUserIds.subList(i, Math.min(i + partitionSize, uniqueUserIds.size())))
                    .getResultList()
                    );
        }
        
        return results;
    }

}

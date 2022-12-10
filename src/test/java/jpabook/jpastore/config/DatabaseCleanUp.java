package jpabook.jpastore.config;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("test")
@Slf4j
public class DatabaseCleanUp implements InitializingBean {

    private final EntityManager em;

    private List<String> entityNames;

    public DatabaseCleanUp(EntityManager em) {
        this.em = em;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        entityNames = em.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null
                        && !e.getName().equalsIgnoreCase("album")
                        && !e.getName().equalsIgnoreCase("dvd")
                        && !e.getName().equalsIgnoreCase("book"))
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void cleanUp() {
        em.flush();

        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (String entityName : entityNames) {
            StringBuilder sb = new StringBuilder(entityName);

            if(sb.toString().equalsIgnoreCase("delivery")
                    || sb.toString().equalsIgnoreCase("category")){

                sb.deleteCharAt(sb.length() - 1).append("ies");
            } else {
                sb.append("s");
            }

            em.createNativeQuery("TRUNCATE TABLE " + sb).executeUpdate();
//            em.createNativeQuery("ALTER TABLE " + sb
//                    + " ALTER COLUMN " + entityName + "_id" + " RESTART WITH 1").executeUpdate();
        }
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}

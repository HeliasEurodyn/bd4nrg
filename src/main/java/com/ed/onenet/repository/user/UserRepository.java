package com.ed.onenet.repository.user;

import com.ed.onenet.dto.user.KeyrockUserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRepository {

    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    public UserRepository(EntityManager entityManager, PlatformTransactionManager transactionManager, PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.transactionManager = transactionManager;
        this.passwordEncoder = passwordEncoder;
        transactionTemplate = new TransactionTemplate(this.transactionManager);
    }


    public void registerNewUser(KeyrockUserResponse keyrockUserResponse) {
        Boolean exists = this.checkExistance(keyrockUserResponse.getId(), "user");
        if (exists) {
            return;
        }
        List<Object[]> fields = this.getUserFields();
        String oauth2UserId = this.getDefaultOauth2User();
        this.saveUser(fields, oauth2UserId, keyrockUserResponse);
    }

    public List<Object[]> getUserFields() {
        Query query = this.entityManager.createNativeQuery("DESCRIBE user");
        return query.getResultList();
    }

    public Boolean checkExistance(String id, String table) {
        Query query = this.entityManager.createNativeQuery("SELECT * FROM " + table + " WHERE id = :id ");
        query.setParameter("id", id);
        List<Object[]> results = query.getResultList();

        return results.size() > 0;
    }

    public Boolean checkApplicationExistance(String id) {
        Query query = this.entityManager.createNativeQuery("SELECT * FROM application WHERE app_id = :id ");
        query.setParameter("id", id);
        List<Object[]> results = query.getResultList();

        return results.size() > 0;
    }

    public void saveUser(List<Object[]> fields, String oauth2UserId, KeyrockUserResponse keyrockUserResponse) {

        List<String> queryFields = fields.stream().filter(field -> !field[0].toString().equals("id"))
                .filter(field -> !field[0].toString().equals("created_by"))
                .filter(field -> !field[0].toString().equals("created_on"))
                .filter(field -> !field[0].toString().equals("modified_by"))
                .filter(field -> !field[0].toString().equals("modified_on"))
                .filter(field -> !field[0].toString().equals("email"))
                .filter(field -> !field[0].toString().equals("username"))
                .filter(field -> !field[0].toString().equals("provider"))
                .filter(field -> !field[0].toString().equals("password"))
                .filter(field -> !field[0].toString().equals("organization_id"))

                .map(field -> field[0].toString()).collect(Collectors.toList());

        String fieldsString = String.join(",", queryFields);

        String userQueryString = " INSERT INTO user (id, organization_id, created_by, created_on, modified_by, modified_on, email, username, provider, password , " + fieldsString + " ) " +
                " SELECT '" + keyrockUserResponse.getId() + "',null, 'oauth2', CURRENT_TIMESTAMP, 'oauth2', CURRENT_TIMESTAMP," + " '" + keyrockUserResponse.getEmail() + "', '" + keyrockUserResponse.getEmail() + "', 'keyrock', " + "'" + passwordEncoder.encode(keyrockUserResponse.getPassword()) + "', " + fieldsString + " FROM user " + " WHERE id = '" + oauth2UserId + "' ";

        String roleQueryString = " INSERT INTO user_role (user_id, role_id) " + " SELECT '" + keyrockUserResponse.getId() + "', role_id " + " FROM user_role " + " WHERE user_id = '" + oauth2UserId + "'";

        String languageQueryString = " INSERT INTO user_language (user_id, language_id) " + " SELECT '" + keyrockUserResponse.getId() + "', language_id " + " FROM user_language " + " WHERE user_id = '" + oauth2UserId + "'";

        transactionTemplate.execute(transactionStatus -> {
            entityManager.createNativeQuery(userQueryString).executeUpdate();

            entityManager.createNativeQuery(roleQueryString).executeUpdate();

            entityManager.createNativeQuery(languageQueryString).executeUpdate();
            transactionStatus.flush();
            return null;
        });
    }
    @Transactional
    public void resetPassword(String id, String password) {
        Query query = entityManager.createNativeQuery(" UPDATE user SET password = :password WHERE id = :id ");
        query.setParameter("id", id);
        query.setParameter("password", password);
        query.executeUpdate();
    }


    public String getDefaultOauth2User() {
        Query query = this.entityManager.createNativeQuery("SELECT `oauth_prototype_user_id` FROM `settings` WHERE id = '1'");
        List<String> oauth2UserResults = query.getResultList();
        return oauth2UserResults.get(0);
    }


    public void registerOrganization(String id, String name, String description) {

        Boolean exists = this.checkExistance(id, "organization");
        if (exists) {
            return;
        }

        this.saveOrganization(id, name, description);
    }

    public void registerOrganizationUserJoin(String user_id, String organization_id) {
        Boolean exists = this.checkUserWithNoOrganization(user_id);
        if (exists) {
            return;
        }

        this.saveOrganizationUserLink(user_id, organization_id);
    }

    public void saveOrganizationUserLink(Object user_id, Object organization_id) {
        Query query = entityManager.createNativeQuery(" UPDATE user SET organization_id = :organization_id WHERE id = :user_id ");
        query.setParameter("user_id", user_id);
        query.setParameter("organization_id", organization_id);
        query.executeUpdate();
    }

    public Boolean checkUserWithNoOrganization(String user_id) {
        Query query = this.entityManager.createNativeQuery("SELECT * FROM user WHERE organization_id = null and id = :user_id ");
        query.setParameter("user_id", user_id);
        List<Object[]> results = query.getResultList();

        return results.size() > 0;
    }



    public void saveOrganization(String id, String name, String description) {
        Query query = entityManager.createNativeQuery(" INSERT INTO organization (id, name, description) VALUES ( :id, :name, :description );");
        query.setParameter("id", id);
        query.setParameter("name", name);
        query.setParameter("description", description);
        query.executeUpdate();
    }

    public void registerApplication(String id,
                                    String name,
                                    String description,
                                    String image,
                                    String url,
                                    String redirectUri,
                                    String redirectSignOutUri,
                                    String grantType,
                                    String responseType,
                                    String tokenTypes,
                                    String jwtSecret,
                                    String clientType) {

        Boolean exists = this.checkApplicationExistance(id);
        if (exists) {
            return;
        }

        this.saveApplication(id,
                name,
                description,
                image,
                url,
                redirectUri,
                redirectSignOutUri,
                grantType,
                responseType,
                tokenTypes,
                jwtSecret,
                clientType);
    }


    public void saveApplication(String id,
                                    String name,
                                    String description,
                                    String image,
                                    String url,
                                    String redirectUri,
                                    String redirectSignOutUri,
                                    String grantType,
                                    String responseType,
                                    String tokenTypes,
                                    String jwtSecret,
                                    String clientType) {

       String queryStr = "INSERT INTO `application`(" +
                "`app_id`, " +
                "`name`, " +
                "`description`, " +
                "`image`, " +
                "`url`, " +
                "`redirect_uri`, " +
                "`redirect_sign_out_uri`, " +
                "`grant_type`, " +
                "`response_type`, " +
                "`token_types`, " +
                "`jwt_secret`, " +
                "`client_type` " +
               ") " +
               "VALUES " +
               "( " +
                ":app_id, " +
                ":name, " +
                ":description, " +
                ":image, " +
                ":url, " +
                ":redirect_uri, " +
                ":redirect_sign_out_uri, " +
                ":grant_type, " +
                ":response_type, " +
                ":token_types, " +
                ":jwt_secret, " +
                ":client_type" +
                ")";

        Query query = entityManager.createNativeQuery(queryStr);


        query.setParameter("app_id" , id);
        query.setParameter("name " ,  name);
        query.setParameter("description" , description);
        query.setParameter("image" , image);
        query.setParameter("url" , url);
        query.setParameter("redirect_uri" , redirectUri);
        query.setParameter("redirect_sign_out_uri" , redirectSignOutUri);
        query.setParameter("grant_type" , grantType);
        query.setParameter("response_type" , responseType);
        query.setParameter("token_types" , tokenTypes);
        query.setParameter("jwt_secret" , jwtSecret);
        query.setParameter("client_type" , clientType);


        query.executeUpdate();

    }
}


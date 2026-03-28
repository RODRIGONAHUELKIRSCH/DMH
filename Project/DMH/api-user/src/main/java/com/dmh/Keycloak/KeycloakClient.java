package com.dmh.Keycloak;

import lombok.Getter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class KeycloakClient {

    @Getter
    private final Keycloak keycloak;
    private final String realm;


    public KeycloakClient(
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret
    ) {
        this.realm = realm;


        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

public String createUser(String nombre, String apellido, String email, String pwd) {

    UserRepresentation user = new UserRepresentation();
    user.setUsername(email);
    user.setEmail(email);
    user.setEnabled(true);
    user.setFirstName(nombre);
    user.setLastName(apellido);

    UsersResource usersResource = keycloak.realm(realm).users();

    usersResource.create(user);

    List<UserRepresentation> users = usersResource.search(email, true);
    if (users.isEmpty()) {
        throw new RuntimeException("Usuario creado pero no se pudo obtener el ID");
    }

    String userId = users.getFirst().getId();

    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(pwd);
    credential.setTemporary(false);

    keycloak.realm(realm)
            .users()
            .get(userId)
            .resetPassword(credential);

    return userId;
}

    public void deleteUser(String keycloakId) {
        keycloak.realm("dmh-realm").users().get(keycloakId).remove();
    }

//    public AccessTokenResponse login(String email, String password) {
//
//        Keycloak keycloakUser = KeycloakBuilder.builder()
//                .serverUrl(serverUrl)
//                .realm(realm)
//                .grantType(OAuth2Constants.PASSWORD)
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .username(email)
//                .password(password)
//                .build();
//
//        return keycloakUser.tokenManager().getAccessToken();
//    }
    public void sendEmailVerification(String userId) {

        keycloak.realm(realm)
            .users()
            .get(userId)
            .sendVerifyEmail();
        }

    public void resetPassword(String userId) {

        keycloak.realm(realm)
                .users()
                .get(userId)
                .executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }
}
package br.com.keycloak.client.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;

import org.keycloak.OAuth2Constants;
import org.keycloak.RSATokenVerifier;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.authentication.ClientCredentialsProviderUtils;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.util.JsonSerialization;

public class LoginClientTest {

	public static void main(String[] args) throws IOException, VerificationException {

		InputStream config = LoginClientTest.class
				.getResourceAsStream("/META-INF/keycloak-client-secret.json");
		KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(config);
		
		Form params = new Form();
		params.param(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS);
        Map<String, String> reqHeaders = new HashMap<>();
        Map<String, String> reqParams = new HashMap<>();		
		ClientCredentialsProviderUtils.setClientCredentials(deployment, reqHeaders, reqParams);		
		

		Client client = ClientBuilder.newClient();
		Builder request = client
				.target(deployment.getTokenUrl()).request();				
		
		
        for (Map.Entry<String, String> header : reqHeaders.entrySet()) {
        	request.header(header.getKey(), header.getValue());
        }
        for (Map.Entry<String, String> param : reqParams.entrySet()) {
        	params.param(param.getKey(), param.getValue());
        }
        
        String json = request.post(Entity.form(params), String.class);
        AccessTokenResponse tokenResp = JsonSerialization.readValue(json, AccessTokenResponse.class);
        AccessToken tokenParsed = RSATokenVerifier.verifyToken(tokenResp.getToken(), deployment.getRealmKey(), deployment.getRealmInfoUrl());
        
        Access access = (Access)tokenParsed.getResourceAccess().get("account");
        System.out.println(access.getRoles());
		System.out.println(access.isUserInRole("manage-account"));
		
		
	}
}

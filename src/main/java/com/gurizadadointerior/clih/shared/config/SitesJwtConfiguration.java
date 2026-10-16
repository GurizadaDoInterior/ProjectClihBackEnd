package com.gurizadadointerior.clih.shared.config;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

@Configuration
@ConditionalOnProperty(name="clih.sites.public-key")
class SitesJwtConfiguration {
 @Bean
 JwtDecoder sitesJwtDecoder(@Value("${clih.sites.public-key}") String encoded,
   @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer) throws Exception {
  var key=(RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(encoded)));
  var decoder=NimbusJwtDecoder.withPublicKey(key).build();
  OAuth2TokenValidator<Jwt> audience=jwt -> jwt.getAudience().contains("clih-api")
    ? OAuth2TokenValidatorResult.success()
    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Invalid audience",null));
  decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer),audience));
  return decoder;
 }
}

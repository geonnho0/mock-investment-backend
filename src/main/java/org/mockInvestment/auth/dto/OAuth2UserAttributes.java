package org.mockInvestment.auth.dto;

public interface OAuth2UserAttributes {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();

    default String getUsername() {
        return getProvider() + " " + getProviderId();
    }

}

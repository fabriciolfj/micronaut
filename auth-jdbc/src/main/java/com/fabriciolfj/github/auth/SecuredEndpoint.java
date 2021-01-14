package com.fabriciolfj.github.auth;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/secured")
public class SecuredEndpoint {

    @Get("/status")
    public List<Object> status(Principal principal) {
        final Authentication details = (Authentication) principal;
        log.info("User details: {}", details);
        return Arrays.asList(
                details.getName(),
                details.getAttributes().get("hair_color"),
                details.getAttributes().get("language")
        );
    }
}

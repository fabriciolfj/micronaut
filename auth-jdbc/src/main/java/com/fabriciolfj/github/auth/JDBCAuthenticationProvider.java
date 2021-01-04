package com.fabriciolfj.github.auth;

import com.fabriciolfj.github.auth.persistence.UserEntity;
import com.fabriciolfj.github.auth.persistence.UserRepository;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class JDBCAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCAuthenticationProvider.class);
    private final UserRepository userRepository;

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flowable.create(emitter -> {
            final String identity = (String) authenticationRequest.getIdentity();
            LOG.debug("User {} tries to login...", identity);

            final Optional<UserEntity> userEntity = userRepository.findByEmail(identity);
            if (userEntity.isPresent()) {
                LOG.info("Found user: {}", userEntity.get().getEmail());
                final String secret = (String) authenticationRequest.getSecret();
                if (userEntity.get().getPassword().equals(secret)) {
                    emitter.onNext(new UserDetails(identity, new ArrayList<>()));
                    emitter.onComplete();
                    return;
                }
            }

            emitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong username or password!")));
        }, BackpressureStrategy.ERROR);
    }
}

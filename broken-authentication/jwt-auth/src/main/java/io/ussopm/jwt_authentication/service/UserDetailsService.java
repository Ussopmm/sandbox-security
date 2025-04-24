package io.ussopm.jwt_authentication.service;

import io.ussopm.jwt_authentication.model.User;
import io.ussopm.jwt_authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> customer = userRepository.findByUsername(username);

        return customer.map(io.ussopm.jwt_authentication.model.UserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Customer with username " + username + " was not found"));

    }
}

package kz.iitu.hello.security;

import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.repository.PanMaratUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PanMaratUserDetailsServiceImpl implements UserDetailsService {

    private final PanMaratUsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PanMaratUser user = usersRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("PanMaratUser not found: " + username));

        String role = "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}

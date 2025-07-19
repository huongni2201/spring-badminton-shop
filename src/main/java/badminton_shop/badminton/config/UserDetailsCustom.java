package badminton_shop.badminton.config;

import badminton_shop.badminton.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        badminton_shop.badminton.domain.User user = this.userService.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return new User(user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }
}

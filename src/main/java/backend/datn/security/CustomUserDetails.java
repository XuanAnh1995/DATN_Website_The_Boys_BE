package backend.datn.security;

import backend.datn.entities.Customer;
import backend.datn.entities.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isEnabled;

    public CustomUserDetails(Employee employee) {
        this.username = employee.getUsername();
        this.password = employee.getPassword();
        this.isEnabled = employee.getStatus() == 1;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getRole().getName().toUpperCase()));
    }

    public CustomUserDetails(Customer customer) {
        this.username = customer.getUsername();
        this.password = customer.getPassword();
        this.isEnabled = customer.getStatus() != null && customer.getStatus();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}

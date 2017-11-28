package in.hocg.web.modules.security.details.user;

import in.hocg.web.modules.security.IGrantedAuthority;
import in.hocg.web.modules.system.domain.Role;
import in.hocg.web.modules.system.domain.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hocgin on 2017/10/25.
 * email: hocgin@gmail.com
 * 用户细节信息
 */
@Data
public class IUser implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Date lastPasswordResetDate;
    private String id;
    
    private IUser(
            String id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            
            Date lastPasswordResetDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.lastPasswordResetDate = lastPasswordResetDate;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
        return true;
    }
    
    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }
    
    public static IUser toIUser(User user) {
        return new IUser(user.getId(),
                user.getUsername(),
                user.getPassword(),
                getGrantedAuthority(user.getRole()),
                user.getLastPasswordResetAt());
    }
    
    private static List<? extends GrantedAuthority> getGrantedAuthority(Collection<Role> roles) {
        return CollectionUtils.isEmpty(roles) ? Collections.emptyList() : roles.stream()
                .map(IGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
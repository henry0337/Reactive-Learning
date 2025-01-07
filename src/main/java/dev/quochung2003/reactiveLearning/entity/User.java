package dev.quochung2003.reactiveLearning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public final class User implements UserDetails {
    @Id
    @Getter
    private UUID id;

    @Getter
    @Column("display_name")
    private String name;

    private String email;

    private String password;

    @Getter
    @Builder.Default
    private String avatar = String.valueOf(URI.create(
            "https://www.google.com/url?sa=i&url=https%3A%2F%2Ficon-library.com%2Ficon%2Fanonymous-user-icon-20.html&psig=AOvVaw3QgiIFhN2LsMcpLEzKO1JY&ust=1735300745405000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCKCX-6ixxYoDFQAAAAAdAAAAABAE"
    ));

    @Getter
    @Builder.Default
    private Role role = Role.USER;

    @Getter
    @JsonIgnore
    @Builder.Default
    @Column("account_expired")
    private boolean isAccountExpired = false;

    @Getter
    @JsonIgnore
    @Builder.Default
    @Column("locked")
    private boolean isAccountLocked = false;

    @Getter
    @JsonIgnore
    @Builder.Default
    @Column("credentials_expired")
    private boolean isCredentialsExpired = false;

    @Getter
    @JsonIgnore
    @Builder.Default
    @Column("authenticable")
    private boolean authenticable = true;

    @Getter
    @JsonIgnore
    @Builder.Default
    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Getter
    @JsonIgnore
    @Builder.Default
    @Column("updated_at")
    private LocalDateTime updatedAt = null;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return authenticable;
    }
}

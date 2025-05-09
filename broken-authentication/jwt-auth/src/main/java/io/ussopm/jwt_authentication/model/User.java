package io.ussopm.jwt_authentication.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "t_jwt_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Column(name = "c_fullName")
    private String fullName;
    private Date createdTime;
    private String role;
    private String email;
    @Column(unique = true, length = 2000)
    private String token;
    private int failedAttempts;
    private Date lockTime;
    private Date passwordUpdateTime;
    @Override
    public String getAuthority() {
        return getRole();
    }
}

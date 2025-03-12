package io.ussopm.jwt_authentication.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity
@Table(name = "t_authority")
@Data
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "c_role")
    private String role;


    @Override
    public String getAuthority() {
        return role;
    }

    @OneToMany(mappedBy = "authority")
    private List<User> users;
}

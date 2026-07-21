package com.centegy.user_service.model;

import com.centegy.user_service.model.enums.RoleLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "Update Role Set is_active = false where id=?")
@SQLRestriction("is_active = true")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Title;

    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private RoleLevel level;


}

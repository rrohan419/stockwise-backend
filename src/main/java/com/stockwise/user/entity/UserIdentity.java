package com.stockwise.user.entity;

import org.hibernate.annotations.DynamicUpdate;

import com.stockwise.common.emuns.Provider;
import com.stockwise.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_identities")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String uuid;
	
	@Column
    private String picture;

	@Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(unique = true, nullable = false)
    private String providerId;
    
    @Column
    private String email;
    
    @Column
    private String phone;

    @ManyToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    private User user;
}

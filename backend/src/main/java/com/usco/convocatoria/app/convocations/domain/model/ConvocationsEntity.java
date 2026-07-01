package com.usco.convocatoria.app.convocations.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import com.usco.convocatoria.app.categories.domain.model.CategoriesEntity;
import com.usco.convocatoria.app.convocations.domain.model.enums.ConvocationsStates;
import com.usco.convocatoria.app.user.domain.model.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocations")
@SQLDelete(sql = "UPDATE convocations SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ConvocationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "initial_date", nullable = false)
    private LocalDateTime initialDate;

    @Column(name = "final_date", nullable = false)
    private LocalDateTime finalDate;

    @Column(name = "quota", nullable = false)
    private Integer quota;

    @Column(name = "state", nullable = false, columnDefinition = "convocations_states")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ConvocationsStates state;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity createdBy;

    @ManyToMany
    @JoinTable(
        name = "convocation_categories",
        joinColumns = @JoinColumn(name = "convocation_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CategoriesEntity> categories = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.state = ConvocationsStates.BORRADOR;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

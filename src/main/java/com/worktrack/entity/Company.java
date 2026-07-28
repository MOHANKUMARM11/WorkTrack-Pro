package com.worktrack.entity;

import com.worktrack.common.entity.BaseEntity;
import com.worktrack.constants.CompanyStatus;
import com.worktrack.constants.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "registration_number", unique = true, length = 50)
    private String registrationNumber;

    @Column(length = 100)
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStatus status;

    @Column(nullable = false)
    private String timezone;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
}
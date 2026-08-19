package com.worktrack.entity;

import com.worktrack.constants.EmployeeStatus;
import com.worktrack.constants.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_location_id")
    private OfficeLocation officeLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Attendance> attendanceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    @Builder.Default
    private List<Leave> leaves = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Payroll> payrolls = new ArrayList<>();
}
package com.thimionii.spring_batch_csv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "customer")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    private Integer id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "male|female|genderqueer", message = "Gender must be male, female, or genderqueer")
    private String gender;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "970-\\d{3}-\\d{3}", message = "Contact number must follow the format 970-XXX-XXX")
    private String contactNo;

    @NotBlank(message = "Country is required")
    private String country;

    @Past(message = "Date of birth must be in the past")
    private String dob;

}

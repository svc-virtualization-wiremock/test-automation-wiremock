package com.sudhirt.api.rest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sudhirt.api.rest.constant.AddressType;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    @NotBlank
    @Column(nullable = false)
    private String address1;
    private String address2;
    @NotBlank
    @Column(nullable = false)
    private String city;
    private String state;
    @NotBlank
    @Column(nullable = false)
    private String country;
    @NotBlank
    @Column(nullable = false)
    private String zipcode;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Customer customer;
}

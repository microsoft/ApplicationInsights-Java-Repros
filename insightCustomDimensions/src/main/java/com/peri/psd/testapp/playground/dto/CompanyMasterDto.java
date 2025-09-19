package com.peri.psd.testapp.playground.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompanyMasterDto {

    private Long id;
    private String name;
    private String street;
    private String postalCode;
    private String city;

}

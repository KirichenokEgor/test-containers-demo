package com.azati.study.testcontainersdemo.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class MyItemResDto {
    private Integer id;
    private String name;
    private int number;
    private String status;
    private LocalDateTime lastChangedDateTime;
}

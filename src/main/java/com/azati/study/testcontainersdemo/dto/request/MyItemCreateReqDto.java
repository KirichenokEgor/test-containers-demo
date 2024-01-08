package com.azati.study.testcontainersdemo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class MyItemCreateReqDto {
    private String name;
    private int number;
}

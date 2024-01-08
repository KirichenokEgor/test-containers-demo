package com.azati.study.testcontainersdemo.component;

import com.azati.study.testcontainersdemo.dto.response.MyItemResDto;
import com.azati.study.testcontainersdemo.entity.MyItem;

public class MyItemMappers {
    public static MyItemResDto toMyItemResDto(MyItem item) {
        return new MyItemResDto()
                .setId(item.getId())
                .setName(item.getName())
                .setNumber(item.getNumber())
                .setStatus(item.getStatus().name())
                .setLastChangedDateTime(item.getLastChangedDateTime());
    }
}

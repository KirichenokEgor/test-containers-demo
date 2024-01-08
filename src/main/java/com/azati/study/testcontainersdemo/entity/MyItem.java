package com.azati.study.testcontainersdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
public class MyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private int number;
    @Enumerated(value = STRING)
    private MyItemStatus status;
    private LocalDateTime lastChangedDateTime;
}

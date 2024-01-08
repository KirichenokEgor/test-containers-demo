package com.azati.study.testcontainersdemo.repository;

import com.azati.study.testcontainersdemo.entity.MyItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyItemRepository extends JpaRepository<MyItem, Integer> {
}

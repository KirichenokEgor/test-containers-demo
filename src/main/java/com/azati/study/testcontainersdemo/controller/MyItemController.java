package com.azati.study.testcontainersdemo.controller;

import com.azati.study.testcontainersdemo.dto.request.MyItemCreateReqDto;
import com.azati.study.testcontainersdemo.dto.request.MyItemUpdateReqDto;
import com.azati.study.testcontainersdemo.dto.response.MyItemResDto;
import com.azati.study.testcontainersdemo.service.MyItemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.azati.study.testcontainersdemo.component.MyItemMappers.toMyItemResDto;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/MyItem")
@AllArgsConstructor
public class MyItemController {

    private final MyItemService service;

    @ResponseStatus(CREATED)
    @PostMapping(produces = {APPLICATION_JSON_VALUE})
    public MyItemResDto create(@RequestBody MyItemCreateReqDto dto) {
        var item = service.create(dto);
        return toMyItemResDto(item);
    }

    @PutMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public MyItemResDto update(@PathVariable Integer id, @RequestBody MyItemUpdateReqDto dto) {
        var item = service.update(id, dto);
        return toMyItemResDto(item);
    }

    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public MyItemResDto get(@PathVariable Integer id) {
        var item = service.get(id);
        return toMyItemResDto(item);
    }

    @PostMapping(value = "/{id}/prepare-for-closing", produces = {APPLICATION_JSON_VALUE})
    public MyItemResDto prepareForClosing(@PathVariable Integer id) {
        var item = service.prepareForClosing(id);
        return toMyItemResDto(item);
    }

    @PostMapping(value = "/{id}/close", produces = {APPLICATION_JSON_VALUE})
    public MyItemResDto close(@PathVariable Integer id) {
        var item = service.close(id);
        return toMyItemResDto(item);
    }
}

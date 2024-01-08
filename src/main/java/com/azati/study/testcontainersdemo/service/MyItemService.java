package com.azati.study.testcontainersdemo.service;

import com.azati.study.testcontainersdemo.component.MyItemEntityUtils;
import com.azati.study.testcontainersdemo.dto.request.MyItemCreateReqDto;
import com.azati.study.testcontainersdemo.dto.request.MyItemUpdateReqDto;
import com.azati.study.testcontainersdemo.entity.MyItem;
import com.azati.study.testcontainersdemo.entity.MyItemStatus;
import com.azati.study.testcontainersdemo.exception.MyItemNotFoundException;
import com.azati.study.testcontainersdemo.exception.ValidationException;
import com.azati.study.testcontainersdemo.repository.MyItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class MyItemService {
    private final MyItemRepository repository;
    private final MyItemEntityUtils entityUtils;

    public MyItem create(MyItemCreateReqDto dto) {
        return repository.save(entityUtils.createMyItem(dto));
    }

    public MyItem update(Integer id, MyItemUpdateReqDto dto) {
        var item = getById(id);
        return repository.save(entityUtils.updateMyItem(item, dto));
    }

    public MyItem get(Integer id) {
        return getById(id);
    }

    public MyItem prepareForClosing(Integer id) {
        var item = getById(id);
        validatePrepareForClosing(item);
        return repository.save(entityUtils.prepareForClosingMyItem(item));
    }

    public MyItem close(Integer id) {
        var item = getById(id);
        validateClosing(item);
        return repository.save(entityUtils.closeMyItem(item));
    }

    private MyItem getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new MyItemNotFoundException(format("MyItem with id=%s doesn't exist", id)));
    }

    private void validatePrepareForClosing(MyItem item) {
        var validStatusesList = List.of(MyItemStatus.CREATED, MyItemStatus.UPDATED);
        if (!validStatusesList.contains(item.getStatus())) {
            throw new ValidationException(format("MyItem with id=%s is already prepared for closing or closed (wrong status)", item.getId()));
        }
    }

    private void validateClosing(MyItem item) {
        var validStatusesList = List.of(MyItemStatus.READY_TO_CLOSE);
        if (!validStatusesList.contains(item.getStatus())) {
            throw new ValidationException(format("MyItem with id=%s can't be closed (wrong status)", item.getId()));
        }
    }
}

package com.azati.study.testcontainersdemo;

import com.azati.study.testcontainersdemo.controller.MyItemController;
import com.azati.study.testcontainersdemo.dto.request.MyItemCreateReqDto;
import com.azati.study.testcontainersdemo.dto.request.MyItemUpdateReqDto;
import com.azati.study.testcontainersdemo.entity.MyItem;
import com.azati.study.testcontainersdemo.entity.MyItemStatus;
import com.azati.study.testcontainersdemo.exception.MyItemNotFoundException;
import com.azati.study.testcontainersdemo.exception.ValidationException;
import com.azati.study.testcontainersdemo.repository.MyItemRepository;
import com.azati.study.testcontainersdemo.service.DateTimeService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.lang.String.format;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class TestContainersDemoApplicationTests {

    private static final int ITEM_NUMBER_1 = 123;
    private static final int ITEM_NUMBER_2 = 234;
    private static final String ITEM_NAME_1 = "ITEM_NAME_1";
    private static final String ITEM_NAME_2 = "ITEM_NAME_2";
    private static final String NOT_FOUND_MESSAGE = "MyItem with id=%s doesn't exist";
    private static final String ALREADY_PREPARED_OR_CLOSED_MESSAGE = "MyItem with id=%s is already prepared for closing or closed (wrong status)";
    private static final String CANT_BE_CLOSED_MESSAGE = "MyItem with id=%s can't be closed (wrong status)";
    private static final LocalDateTime ITEM_CHANGED_DATE_TIME_1 = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    private static final LocalDateTime ITEM_CHANGED_DATE_TIME_2 = ITEM_CHANGED_DATE_TIME_1.plusDays(1L);

    @Autowired
    MyItemRepository repository;

    @Autowired
    MyItemController controller;

    @MockBean
    DateTimeService dateTimeService;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));


    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void clearDB() {
        repository.deleteAll();
    }

    @Test
    void createMyItemTest() {
        //given
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_1);
        var dto = new MyItemCreateReqDto().setNumber(ITEM_NUMBER_1).setName(ITEM_NAME_1);

        //when
        var item = controller.create(dto);

        //then
        then(item.getId()).isNotNull();
        then(item.getNumber()).isEqualTo(ITEM_NUMBER_1);
        then(item.getName()).isEqualTo(ITEM_NAME_1);
        then(item.getStatus()).isEqualTo(MyItemStatus.CREATED.name());
        then(item.getLastChangedDateTime()).isEqualTo(ITEM_CHANGED_DATE_TIME_1);
    }

    @Test
    void updateMyItemTest() {
        //given
        var item = repository.save(new MyItem()
                .setNumber(ITEM_NUMBER_1)
                .setName(ITEM_NAME_1)
                .setStatus(MyItemStatus.CREATED)
                .setLastChangedDateTime(ITEM_CHANGED_DATE_TIME_1));
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);
        var updateDto = new MyItemUpdateReqDto().setNumber(ITEM_NUMBER_2).setName(ITEM_NAME_2);

        //when
        var updatedItem = controller.update(item.getId(), updateDto);

        //then
        then(updatedItem.getId()).isEqualTo(item.getId());
        then(updatedItem.getNumber()).isEqualTo(ITEM_NUMBER_2);
        then(updatedItem.getName()).isEqualTo(ITEM_NAME_2);
        then(updatedItem.getStatus()).isEqualTo(MyItemStatus.UPDATED.name());
        then(updatedItem.getLastChangedDateTime()).isEqualTo(ITEM_CHANGED_DATE_TIME_2);
    }

    @Test
    void updateMyItemTestErrorNotExistingId() {
        //given
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);
        var updateDto = new MyItemUpdateReqDto().setNumber(ITEM_NUMBER_2).setName(ITEM_NAME_2);
        var id = 12142;

        //when
        var throwable = catchThrowable(() -> controller.update(id, updateDto));

        //then
        then(throwable.getClass()).isEqualTo(MyItemNotFoundException.class);
        then(throwable.getMessage()).isEqualTo(format(NOT_FOUND_MESSAGE, id));
    }

    @ParameterizedTest
    @EnumSource(value = MyItemStatus.class, names = {"CREATED", "UPDATED"})
    void prepareMyItemTest(MyItemStatus status) {
        //given
        var item = repository.save(new MyItem()
                .setNumber(ITEM_NUMBER_1)
                .setName(ITEM_NAME_1)
                .setStatus(status)
                .setLastChangedDateTime(ITEM_CHANGED_DATE_TIME_1));
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);

        //when
        var preparedItem = controller.prepareForClosing(item.getId());

        //then
        then(preparedItem.getId()).isEqualTo(item.getId());
        then(preparedItem.getNumber()).isEqualTo(ITEM_NUMBER_1);
        then(preparedItem.getName()).isEqualTo(ITEM_NAME_1);
        then(preparedItem.getStatus()).isEqualTo(MyItemStatus.READY_TO_CLOSE.name());
        then(preparedItem.getLastChangedDateTime()).isEqualTo(ITEM_CHANGED_DATE_TIME_2);
    }

    @ParameterizedTest
    @EnumSource(value = MyItemStatus.class, names = {"READY_TO_CLOSE", "CLOSED"})
    void prepareMyItemTestErrorWrongStatus(MyItemStatus status) {
        //given
        var item = repository.save(new MyItem()
                .setNumber(ITEM_NUMBER_1)
                .setName(ITEM_NAME_1)
                .setStatus(status)
                .setLastChangedDateTime(ITEM_CHANGED_DATE_TIME_1));
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);

        //when
        var throwable = catchThrowable(() -> controller.prepareForClosing(item.getId()));

        //then
        then(throwable.getClass()).isEqualTo(ValidationException.class);
        then(throwable.getMessage()).isEqualTo(format(ALREADY_PREPARED_OR_CLOSED_MESSAGE, item.getId()));
    }

    @Test
    void prepareMyItemTestErrorNotExistingId() {
        //given
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);
        var id = 12142;

        //when
        var throwable = catchThrowable(() -> controller.prepareForClosing(id));

        //then
        then(throwable.getClass()).isEqualTo(MyItemNotFoundException.class);
        then(throwable.getMessage()).isEqualTo(format(NOT_FOUND_MESSAGE, id));
    }

    @ParameterizedTest
    @EnumSource(value = MyItemStatus.class, names = {"READY_TO_CLOSE"})
    void closeMyItemTest(MyItemStatus status) {
        //given
        var item = repository.save(new MyItem()
                .setNumber(ITEM_NUMBER_1)
                .setName(ITEM_NAME_1)
                .setStatus(status)
                .setLastChangedDateTime(ITEM_CHANGED_DATE_TIME_1));
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);

        //when
        var preparedItem = controller.close(item.getId());

        //then
        then(preparedItem.getId()).isEqualTo(item.getId());
        then(preparedItem.getNumber()).isEqualTo(ITEM_NUMBER_1);
        then(preparedItem.getName()).isEqualTo(ITEM_NAME_1);
        then(preparedItem.getStatus()).isEqualTo(MyItemStatus.CLOSED.name());
        then(preparedItem.getLastChangedDateTime()).isEqualTo(ITEM_CHANGED_DATE_TIME_2);
    }

    @ParameterizedTest
    @EnumSource(value = MyItemStatus.class, names = {"CREATED", "UPDATED", "CLOSED"})
    void closeMyItemTestErrorWrongStatus(MyItemStatus status) {
        //given
        var item = repository.save(new MyItem()
                .setNumber(ITEM_NUMBER_1)
                .setName(ITEM_NAME_1)
                .setStatus(status)
                .setLastChangedDateTime(ITEM_CHANGED_DATE_TIME_1));
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);

        //when
        var throwable = catchThrowable(() -> controller.close(item.getId()));

        //then
        then(throwable.getClass()).isEqualTo(ValidationException.class);
        then(throwable.getMessage()).isEqualTo(format(CANT_BE_CLOSED_MESSAGE, item.getId()));
    }

    @Test
    void closeMyItemTestErrorNotExistingId() {
        //given
        when(dateTimeService.getCurrentLocalDateTime()).thenReturn(ITEM_CHANGED_DATE_TIME_2);
        var id = 12142;

        //when
        var throwable = catchThrowable(() -> controller.close(id));

        //then
        then(throwable.getClass()).isEqualTo(MyItemNotFoundException.class);
        then(throwable.getMessage()).isEqualTo(format(NOT_FOUND_MESSAGE, id));
    }

    @Test
    void getMyItemTest() {
        //given
        var item = repository.save(new MyItem()
                .setNumber(ITEM_NUMBER_1)
                .setName(ITEM_NAME_1)
                .setStatus(MyItemStatus.CREATED)
                .setLastChangedDateTime(ITEM_CHANGED_DATE_TIME_1));

        //when
        var receivedItem = controller.get(item.getId());

        //then
        then(receivedItem.getId()).isEqualTo(item.getId());
        then(receivedItem.getNumber()).isEqualTo(item.getNumber());
        then(receivedItem.getName()).isEqualTo(item.getName());
        then(receivedItem.getStatus()).isEqualTo(item.getStatus().name());
        then(receivedItem.getLastChangedDateTime()).isEqualTo(item.getLastChangedDateTime());
    }

    @Test
    void getMyItemTestErrorNotExistingId() {
        //given
        var id = 12142;

        //when
        var throwable = catchThrowable(() -> controller.get(id));

        //then
        then(throwable.getClass()).isEqualTo(MyItemNotFoundException.class);
        then(throwable.getMessage()).isEqualTo(format(NOT_FOUND_MESSAGE, id));
    }

}
package com.aveng.wapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import com.aveng.wapp.domain.DiffEntity;
import com.aveng.wapp.repository.DiffRepository;
import com.aveng.wapp.service.dto.Diff;
import com.aveng.wapp.service.dto.DiffType;
import com.aveng.wapp.service.dto.StringDiffResult;
import com.aveng.wapp.service.exception.ApplicationException;
import com.aveng.wapp.service.mapper.DiffMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@RunWith(MockitoJUnitRunner.class)
public class DiffServiceTest {

    @Mock
    private DiffRepository diffRepository;
    @Mock
    private DiffMapper diffMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private StringDiffer stringDiffer;

    @InjectMocks
    private DiffService service;

    public static final String VALID_INPUT =
        "{\n\"resource\": \"abcdefg123----\",\n\"target\": \"abcdefg----123\",\n\"test\": 60\n}";

    public static final String VALID_JSON_INPUT = Base64.getEncoder().encodeToString(VALID_INPUT.getBytes());

    @Before
    public void setUp() {

    }

    @SneakyThrows
    @Test
    public void accept_new_diff_success() {

        when(diffRepository.findByDiffId(eq(1L))).thenReturn(Optional.empty());

        mockDiff(Diff.builder().build());

        ArgumentCaptor<Diff> diffAC = ArgumentCaptor.forClass(Diff.class);

        Diff diff = service.acceptDiffInput(1l, VALID_JSON_INPUT, DiffType.LEFT);

        verify(diffMapper, times(1)).map(diffAC.capture());
        verify(objectMapper, times(1)).readTree(eq(VALID_INPUT));
        assertEquals(diffAC.getValue().getLeftText(), VALID_INPUT);
        assertEquals(1L, diffAC.getValue().getDiffId());
        assertNotNull(diff);
    }

    @SneakyThrows
    @Test
    public void accept_existing_diff_success() {

        when(diffRepository.findByDiffId(eq(1L))).thenReturn(
            Optional.of(DiffEntity.builder().diffId(1L).rightText("rightText").build()));

        mockDiff(Diff.builder().diffId(1L).rightText("rightText").build());

        ArgumentCaptor<Diff> diffAC = ArgumentCaptor.forClass(Diff.class);

        Diff diff = service.acceptDiffInput(1L, VALID_JSON_INPUT, DiffType.LEFT);

        verify(diffMapper, times(1)).map(diffAC.capture());
        verify(objectMapper, times(1)).readTree(eq(VALID_INPUT));
        assertEquals(diffAC.getValue().getLeftText(), VALID_INPUT);
        assertEquals(diffAC.getValue().getRightText(), "rightText");
        assertEquals(1L, diffAC.getValue().getDiffId());
        assertNotNull(diff);
    }

    @SneakyThrows
    @Test
    public void accept_new_diff_emptyInput() {

        ApplicationException applicationException = null;

        try {
            service.acceptDiffInput(1L, "", DiffType.LEFT);
        } catch (ApplicationException e) {
            applicationException = e;
        }

        assertEquals(applicationException.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertEquals(applicationException.getMessage(), "Diff input cannot be empty!");
    }

    @SneakyThrows
    @Test
    public void accept_new_diff_invalid_json_input() {
        JsonMappingException cause = new JsonMappingException("test");
        when(objectMapper.readTree(anyString())).thenThrow(cause);

        ApplicationException applicationException = null;
        try {
            service.acceptDiffInput(1L, "sdsadsad", DiffType.LEFT);
        } catch (ApplicationException e) {
            applicationException = e;
        }

        assertEquals(applicationException.getCause(), cause);
        assertEquals(applicationException.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertEquals(applicationException.getMessage(), "Input is not a valid JSON!");
    }

    @SneakyThrows
    @Test
    public void accept_new_right_diff_success() {

        when(diffRepository.findByDiffId(eq(1L))).thenReturn(Optional.empty());

        mockDiff(Diff.builder().build());

        ArgumentCaptor<Diff> diffAC = ArgumentCaptor.forClass(Diff.class);

        Diff diff = service.acceptDiffInput(1l, VALID_JSON_INPUT, DiffType.RIGHT);

        verify(diffMapper, times(1)).map(diffAC.capture());
        verify(objectMapper, times(1)).readTree(eq(VALID_INPUT));
        assertEquals(diffAC.getValue().getRightText(), VALID_INPUT);
        assertEquals(1L, diffAC.getValue().getDiffId());
        assertNotNull(diff);
    }

    private void mockDiff(Diff diff) {
        when(diffMapper.map(any(Diff.class))).thenReturn(new DiffEntity());
        when(diffMapper.map(any(DiffEntity.class))).thenReturn(diff);
    }

    @Test
    public void diff_not_found() {

        when(diffRepository.findByDiffId(eq(1L))).thenReturn(Optional.empty());

        ApplicationException applicationException = null;
        try {
            service.diff(1L);
        } catch (ApplicationException e) {
            applicationException = e;
        }

        assertEquals(applicationException.getStatus(), HttpStatus.NOT_FOUND.value());
        assertEquals(applicationException.getMessage(), "Requested diff not found!");
    }

    @Test
    public void diff_empty_left_text() {

        when(diffRepository.findByDiffId(eq(1L))).thenReturn(Optional.of(DiffEntity.builder().build()));
        when(diffMapper.map(any(DiffEntity.class))).thenReturn(Diff.builder().rightText("dsad").build());

        ApplicationException applicationException = null;
        try {
            service.diff(1L);
        } catch (ApplicationException e) {
            applicationException = e;
        }

        assertEquals(applicationException.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertEquals(applicationException.getMessage(), "Diff left and right inputs must exist!");
    }

    @Test
    public void diff_empty_right_text() {

        when(diffRepository.findByDiffId(eq(1L))).thenReturn(Optional.of(DiffEntity.builder().build()));
        when(diffMapper.map(any(DiffEntity.class))).thenReturn(Diff.builder().leftText("dsad").build());

        ApplicationException applicationException = null;
        try {
            service.diff(1L);
        } catch (ApplicationException e) {
            applicationException = e;
        }

        assertEquals(applicationException.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertEquals(applicationException.getMessage(), "Diff left and right inputs must exist!");
    }

    @Test
    public void diff_success() {

        StringDiffResult expectedResult = StringDiffResult.builder().message("sdasdas").build();

        when(diffRepository.findByDiffId(eq(1L))).thenReturn(Optional.of(DiffEntity.builder().build()));

        when(diffMapper.map(any(DiffEntity.class))).thenReturn(
            Diff.builder().rightText("right").leftText("left").build());

        when(stringDiffer.compare(anyString(), anyString())).thenReturn(expectedResult);

        ArgumentCaptor<String> leftAC = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> rightAC = ArgumentCaptor.forClass(String.class);

        StringDiffResult diff = service.diff(1L);

        verify(stringDiffer, times(1)).compare(leftAC.capture(), rightAC.capture());

        assertEquals(diff, expectedResult);
        assertEquals("left", leftAC.getValue());
        assertEquals("right", rightAC.getValue());
    }
}
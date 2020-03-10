package com.aveng.wapp.service;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.wapp.repository.DiffRepository;
import com.aveng.wapp.service.dto.Diff;
import com.aveng.wapp.service.dto.DiffType;
import com.aveng.wapp.service.dto.StringDiffResult;
import com.aveng.wapp.service.exception.ApplicationException;
import com.aveng.wapp.service.mapper.DiffMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * A service for all diff needs
 *
 * @author apaydin
 */
@Service
@Slf4j
public class DiffService {

    private DiffRepository diffRepository;
    private DiffMapper diffMapper;
    private ObjectMapper objectMapper;
    private StringDiffer stringDiffer;

    @Autowired
    public DiffService(DiffRepository diffRepository, DiffMapper diffMapper, ObjectMapper objectMapper,
        StringDiffer stringDiffer) {
        this.diffRepository = diffRepository;
        this.diffMapper = diffMapper;
        this.objectMapper = objectMapper;
        this.stringDiffer = stringDiffer;
    }

    /**
     * Given diff id, creates or updates a diff's text and returns the diff resource.
     *
     * @param diffId id of the diff
     * @param base64EncodedInput a String representing an base64 encoded JSON object
     * @param diffType Specifies which diff input will be updated
     * @return resulting diff
     */
    @Transactional
    public Diff acceptDiffInput(long diffId, String base64EncodedInput, DiffType diffType) {

        String decodedString = validateInput(base64EncodedInput);

        Diff diff = retrieveDiff(diffId).orElse(Diff.builder().diffId(diffId).build());

        switch (diffType) {
            case LEFT:
                diff.setLeftText(decodedString);
                break;
            case RIGHT:
                diff.setRightText(decodedString);
                break;
        }

        diffRepository.save(diffMapper.map(diff));

        return diff;
    }

    /**
     * Calculates the diff with the given diff id
     *
     * @param diffId id of the requested diff
     * @return Resulting {@link StringDiffResult}
     */
    public StringDiffResult diff(long diffId) {

        Diff diff = retrieveDiff(diffId).orElseThrow(
            () -> new ApplicationException(HttpStatus.NOT_FOUND, "Requested diff not found!", Level.INFO));

        if (StringUtils.isEmpty(diff.getLeftText()) || StringUtils.isEmpty(diff.getRightText())) {
            throw ApplicationException.getValidationException("Diff left and right inputs must exist!");
        }

        return stringDiffer.compare(diff.getLeftText(), diff.getRightText());
    }

    private String validateInput(String base64EncodedInput) {

        if (StringUtils.isEmpty(base64EncodedInput)) {
            throw ApplicationException.getValidationException("Diff input cannot be empty!");
        }

        String decodedString = decodeBase64(base64EncodedInput);

        checkValidJSON(decodedString);

        return decodedString;
    }

    private Optional<Diff> retrieveDiff(long id) {
        return diffRepository.findByDiffId(id).map(diffEntity -> diffMapper.map(diffEntity));
    }

    private String decodeBase64(String base64EncodedInput) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedInput);
        return new String(decodedBytes);
    }

    private void checkValidJSON(String jsonInString) {
        try {
            objectMapper.readTree(jsonInString);
        } catch (IOException e) {
            throw ApplicationException.getValidationException("Input is not a valid JSON!", e);
        }
    }
}

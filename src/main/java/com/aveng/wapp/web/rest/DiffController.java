package com.aveng.wapp.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aveng.wapp.service.DiffService;
import com.aveng.wapp.service.dto.Diff;
import com.aveng.wapp.service.dto.StringDiff;
import com.aveng.wapp.service.dto.StringDiffResult;
import com.aveng.wapp.web.rest.model.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author apaydin
 */
@RestController
@RequestMapping("/v1/diff")
@Slf4j
public class DiffController {

    private DiffService diffService;

    @Autowired
    public DiffController(DiffService diffService) {
        this.diffService = diffService;
    }

    @PostMapping(value = "/{id}/left")
    public ResponseEntity<ApiResponse<Diff>> acceptLeftDiff(@PathVariable long id,
        @RequestBody String base64EncodedInput) {

        log.info("Accepted left input: {} for id: {}", base64EncodedInput, id);

        Diff diff = diffService.acceptLeft(id, base64EncodedInput);

        return ResponseEntity.ok(ApiResponse.<Diff>builder()
            .message("Left diff accepted")
            .data(diff)
            .build());
    }

    @PostMapping(value = "/{id}/right")
    public ResponseEntity<ApiResponse<Diff>> acceptRightDiff(@PathVariable long id,
        @RequestBody String base64EncodedInput) {

        log.info("Accepted right input: {} for id: {}", base64EncodedInput, id);

        Diff diff = diffService.acceptRight(id, base64EncodedInput);

        return ResponseEntity.ok(ApiResponse.<Diff>builder()
            .message("right diff accepted")
            .data(diff)
            .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<StringDiff>>> retrieveDiff(@PathVariable long id) {

        StringDiffResult diffResult = diffService.diff(id);

        return ResponseEntity.ok(ApiResponse.<List<StringDiff>>builder()
            .message(diffResult.getMessage())
            .data(diffResult.getStringDiffs())
            .build());
    }
}

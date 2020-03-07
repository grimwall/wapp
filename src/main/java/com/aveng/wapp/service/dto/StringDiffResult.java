package com.aveng.wapp.service.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author apaydin
 */
@Data
@Builder
public class StringDiffResult {

    String message;

    @Builder.Default
    List<StringDiff> stringDiffs = new ArrayList<>();
}

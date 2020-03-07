package com.aveng.wapp.service.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author apaydin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diff {

    private String id;

    @NotNull
    private long diffId;

    private String leftText;

    private String rightText;
}

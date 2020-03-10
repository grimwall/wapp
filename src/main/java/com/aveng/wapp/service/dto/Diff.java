package com.aveng.wapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    private String id;

    private long diffId;

    private String leftText;

    private String rightText;
}

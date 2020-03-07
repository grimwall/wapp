package com.aveng.wapp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single continous diff between two strings.
 *
 * @author apaydin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StringDiff {
    /**
     * Offset of the start of the diff starting from 0
     */
    int offset;

    /**
     * Length of the continuous diff
     */
    int length;
}

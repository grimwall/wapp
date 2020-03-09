package com.aveng.wapp.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.aveng.wapp.service.dto.StringDiff;
import com.aveng.wapp.service.dto.StringDiffResult;

public class StringDifferTest {

    private StringDiffer stringDiffer = new StringDiffer();

    @Test
    public void compare_equal_strings() {
        StringDiffResult result = stringDiffer.compare("same", "same");

        assertEquals("Provided strings are equal.", result.getMessage());
    }

    @Test
    public void compare_unequal_length_strings() {
        StringDiffResult result = stringDiffer.compare("same", "samex");

        assertEquals("Provided strings are not equal in length.", result.getMessage());
    }

    @Test
    public void compare_unequal_strings() {

        String left = "word word ward guad x";
        String rigt = "word ward guad word y";

        StringDiffResult result = stringDiffer.compare(left, rigt);

        assertEquals("Provided strings have diffs.", result.getMessage());

        List<StringDiff> stringDiffs = result.getStringDiffs();

        assertEquals(4, stringDiffs.size());
        assertValidDiff(stringDiffs.get(0), 6, 1);
        assertValidDiff(stringDiffs.get(1), 10, 3);
        assertValidDiff(stringDiffs.get(2), 15, 3);
        assertValidDiff(stringDiffs.get(3), 20, 1);
    }

    @Test
    public void compare_unequal_one_char_strings() {

        String left = "x";
        String rigt = "y";

        StringDiffResult result = stringDiffer.compare(left, rigt);

        assertEquals("Provided strings have diffs.", result.getMessage());

        List<StringDiff> stringDiffs = result.getStringDiffs();

        assertEquals(1, stringDiffs.size());
        assertValidDiff(stringDiffs.get(0), 0, 1);
    }

    private void assertValidDiff(StringDiff stringDiff, int offset, int length) {
        assertEquals(offset, stringDiff.getOffset());
        assertEquals(length, stringDiff.getLength());
    }
}
package com.aveng.wapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.aveng.wapp.service.dto.StringDiff;
import com.aveng.wapp.service.dto.StringDiffResult;

/**
 * A Service for comparing two strings
 *
 * @author apaydin
 */
@Service
public class StringDiffer {

    /**
     * Compares two strings and if they are matching in length, finds their diffs
     *
     * @param leftText left input
     * @param rightText right input
     * @return Message indicating the diff result and if present, a list of diffs
     */
    public StringDiffResult compare(@NonNull String leftText, @NonNull String rightText) {

        if (Objects.equals(leftText, rightText)) {
            return StringDiffResult.builder().message("Provided strings are equal.").build();
        }

        if (leftText.length() != rightText.length()) {
            return StringDiffResult.builder().message("Provided strings are not equal in length.").build();
        }

        List<StringDiff> stringDiffs = findDiffs(leftText, rightText);

        return StringDiffResult.builder().message("Provided strings have diffs.").stringDiffs(stringDiffs).build();
    }

    private List<StringDiff> findDiffs(@NonNull String leftText, @NonNull String rightText) {

        List<StringDiff> stringDiffs = new ArrayList<>();

        boolean hasStartedADiff = false;
        StringDiff currentDiff = null;
        char left;
        char right;

        /*
         * Walk the strings one char at a time. If the current chars are not equal,
         * start a diff and continue. Finish the current diff at the first encountered equal char.
         */
        for (int i = 0; i < leftText.length(); i++) {

            left = leftText.charAt(i);
            right = rightText.charAt(i);

            //create a new diff point
            if (!hasStartedADiff && left != right) {
                currentDiff = new StringDiff();
                currentDiff.setOffset(i);
                hasStartedADiff = true;

                //last char diff must be added! (edge case)
                if (i == leftText.length() - 1) {
                    currentDiff.setLength(1);
                    stringDiffs.add(currentDiff);
                }
            } else if (hasStartedADiff && left == right) {
                //finish the current diff
                currentDiff.setLength(i - currentDiff.getOffset());
                stringDiffs.add(currentDiff);
                hasStartedADiff = false;
            }
            // no need to do anything, keep going
        }

        return stringDiffs;
    }
}

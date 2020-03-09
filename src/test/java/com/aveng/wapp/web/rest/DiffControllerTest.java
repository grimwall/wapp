package com.aveng.wapp.web.rest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import com.aveng.wapp.WappApplication;
import com.aveng.wapp.domain.DiffEntity;
import com.aveng.wapp.repository.DiffRepository;
import com.aveng.wapp.web.rest.model.ApiResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WappApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiffControllerTest {

    public static final String VALID_INPUT =
        "{\n\"resource\": \"abcdefg123----\",\n\"target\": \"abcdefg----123\",\n\"test\": 60\n}";

    public static final String VALID_INPUT_2 =
        "{\n\"resource\": \"abcdefg555----\",\n\"target\": \"abcdefg-xx-123\",\n\"test\": 61\n}";

    public static final String VALID_ENCODED_INPUT = Base64.getEncoder().encodeToString(VALID_INPUT.getBytes());
    public static final String VALID_ENCODED_INPUT_2 = Base64.getEncoder().encodeToString(VALID_INPUT_2.getBytes());

    public static final String LEFT_TEXT = "leftText";
    public static final String ID = "id";
    public static final String RIGHT_TEXT = "rightText";
    public static final String LEFT_PATH = "/v1/diff/%d/left";
    public static final String RIGHT_PATH = "/v1/diff/%d/right";

    @Autowired
    private DiffRepository diffRepository;

    @LocalServerPort
    private int port;

    @After
    public void tearDown() {
        diffRepository.deleteAll();
    }

    @Test
    public void acceptLeftDiff_new_diff_success() {

        ApiResponse apiResponse = postDiff(VALID_ENCODED_INPUT, LEFT_PATH, HttpStatus.OK);

        Map data = (Map) apiResponse.getData();
        assertEquals(data.get(LEFT_TEXT), VALID_INPUT);

        DiffEntity persistedDiff = diffRepository.findByDiffId(1L).orElse(null);

        assertEquals(1L, persistedDiff.getDiffId());
        assertEquals(persistedDiff.getId(), data.get(ID));
        assertEquals(persistedDiff.getLeftText(), VALID_INPUT);
    }

    @Test
    public void acceptLeftDiff_update_existing_diff_success() {

        postDiff(VALID_ENCODED_INPUT, LEFT_PATH, HttpStatus.OK);

        DiffEntity diffEntityInitial = diffRepository.findByDiffId(1L).orElse(null);

        assertNotNull(diffEntityInitial);

        ApiResponse apiResponse = postDiff(VALID_ENCODED_INPUT_2, LEFT_PATH, HttpStatus.OK);

        Map data = (Map) apiResponse.getData();
        assertEquals(data.get(LEFT_TEXT), VALID_INPUT_2);

        DiffEntity updatedDiff = diffRepository.findByDiffId(1L).orElse(null);

        assertEquals(diffEntityInitial.getId(), updatedDiff.getId());
        assertEquals(1L, updatedDiff.getDiffId());
        assertEquals(updatedDiff.getId(), data.get(ID));
        assertEquals(updatedDiff.getLeftText(), VALID_INPUT_2);
    }

    @Test
    public void acceptLeftDiff_invalid_json_bad_request() {

        ApiResponse apiResponse = postDiff("gbdsjhagdhasGHDJHJHKDSGHAJD", LEFT_PATH, HttpStatus.BAD_REQUEST);

        assertEquals(apiResponse.getMessage(), "Input is not a valid JSON!");

        assertTrue(CollectionUtils.isEmpty(diffRepository.findAll()));
    }

    @Test
    public void acceptRightDiff_new_diff_success() {

        ApiResponse apiResponse = postDiff(VALID_ENCODED_INPUT, RIGHT_PATH, HttpStatus.OK);

        Map data = (Map) apiResponse.getData();
        assertEquals(data.get(RIGHT_TEXT), VALID_INPUT);

        DiffEntity persistedDiff = diffRepository.findByDiffId(1L).orElse(null);

        assertEquals(1L, persistedDiff.getDiffId());
        assertEquals(persistedDiff.getId(), data.get(ID));
        assertEquals(persistedDiff.getRightText(), VALID_INPUT);
    }

    @Test
    public void accept_both_diff_success() {

        postDiff(VALID_ENCODED_INPUT, LEFT_PATH, HttpStatus.OK);

        ApiResponse apiResponse = postDiff(VALID_ENCODED_INPUT_2, RIGHT_PATH, HttpStatus.OK);

        Map data = (Map) apiResponse.getData();
        assertEquals(data.get(LEFT_TEXT), VALID_INPUT);
        assertEquals(data.get(RIGHT_TEXT), VALID_INPUT_2);

        DiffEntity updatedDiff = diffRepository.findByDiffId(1L).orElse(null);

        assertEquals(1L, updatedDiff.getDiffId());
        assertEquals(updatedDiff.getId(), data.get(ID));
        assertEquals(updatedDiff.getLeftText(), VALID_INPUT);
        assertEquals(updatedDiff.getRightText(), VALID_INPUT_2);
    }

    @Test
    public void retrieveDiff_success() {

        postDiff(VALID_ENCODED_INPUT, LEFT_PATH, HttpStatus.OK);

        postDiff(VALID_ENCODED_INPUT_2, RIGHT_PATH, HttpStatus.OK);

        ApiResponse diffResponse = given().when()
            .port(port)
            .get(String.format("/v1/diff/%d/", 1))
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ApiResponse.class);

        List data = (List) diffResponse.getData();

        assertEquals(3, data.size());

        assertValidDiff((Map) data.get(0), 22, 3);
        assertValidDiff((Map) data.get(1), 51, 2);
        assertValidDiff((Map) data.get(2), 69, 1);
    }

    @Test
    public void retrieveDiff_equal_inputs_success() {

        postDiff(VALID_ENCODED_INPUT, LEFT_PATH, HttpStatus.OK);

        postDiff(VALID_ENCODED_INPUT, RIGHT_PATH, HttpStatus.OK);

        ApiResponse diffResponse = given().when()
            .port(port)
            .get(String.format("/v1/diff/%d/", 1))
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ApiResponse.class);

        assertEquals("Provided strings are equal.", diffResponse.getMessage());
    }

    @Test
    public void retrieveDiff_unequal_length_inputs_success() {

        String validInputLonger =
            "{\n\"resource\": \"abcdefg555----\",\n\"target\": \"abcdefg-xxxx-123\",\n\"test\": 61\n}";

        String validEncodedInputLonger = Base64.getEncoder().encodeToString(validInputLonger.getBytes());

        postDiff(VALID_ENCODED_INPUT, LEFT_PATH, HttpStatus.OK);

        postDiff(validEncodedInputLonger, RIGHT_PATH, HttpStatus.OK);

        ApiResponse diffResponse = given().when()
            .port(port)
            .get(String.format("/v1/diff/%d/", 1))
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ApiResponse.class);

        assertEquals("Provided strings are not equal in length.", diffResponse.getMessage());
    }

    @Test
    public void retrieveDiff_not_found() {

        ApiResponse diffResponse = given().when()
            .port(port)
            .get(String.format("/v1/diff/%d/", 1))
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .as(ApiResponse.class);

        assertEquals("Requested diff not found!", diffResponse.getMessage());
    }

    @Test
    public void retrieveDiff_need_both_inputs_bad_request() {

        postDiff(VALID_ENCODED_INPUT, LEFT_PATH, HttpStatus.OK);

        ApiResponse diffResponse = given().when()
            .port(port)
            .get(String.format("/v1/diff/%d/", 1))
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .as(ApiResponse.class);

        assertEquals("Diff left and right inputs must exist!", diffResponse.getMessage());
    }

    private void assertValidDiff(Map diff, int offset, int length) {
        assertEquals(offset, diff.get("offset"));
        assertEquals(length, diff.get("length"));
    }

    private ApiResponse postDiff(String validEncodedInput, String path, HttpStatus expectedStatus) {
        return given().when()
            .port(port)
            .body(validEncodedInput)
            .post(String.format(path, 1))
            .then()
            .statusCode(expectedStatus.value())
            .extract()
            .as(ApiResponse.class);
    }
}

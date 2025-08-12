package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;


public class ScoreControllerRA {

	private Long existingMovieId, nonExistingMovieId;

	private String clientUsername, adminUsername, adminPassword, clientPassword;

	private String clientToken, invalidToken, adminToken;

	private JSONObject jsonObject;

	private Map<String, Object> postScoreInstance = new HashMap<>();

	@BeforeEach
	void setUp() throws Exception {

		baseURI = "http://localhost:8080/";

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";

		adminUsername = "maria@gmail.com";
		adminPassword = "123456";


		postScoreInstance.put("movieId", 1);
		postScoreInstance.put("score", 4);

		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);

	}

		@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {		

		postScoreInstance.put("movieId", "40");

		//transforma objeto em json

			JSONObject jsonBody = new JSONObject(postScoreInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(404);

	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {

		postScoreInstance.put("movieId", null);

		//transforma objeto em json

		JSONObject jsonBody = new JSONObject(postScoreInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Required field"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {

		postScoreInstance.put("score", -1);

		//transforma objeto em json

		JSONObject jsonBody = new JSONObject(postScoreInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Score should be greater than or equal to zero"));
	}
}

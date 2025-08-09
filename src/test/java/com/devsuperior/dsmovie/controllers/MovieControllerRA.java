package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

	private Long existingMovieId, nonExistingMovieId;

	private String clientToken, invalidToken, adminToken;

	private String adminUsername, clientUsername, clientPassword, adminPassword;

	private Map<String, Object> postMovie = new HashMap<>();

	private JSONObject jsonObject;

	@BeforeEach
	void setUp() throws Exception {

		baseURI = "http://localhost:8090/";



		clientUsername = "alex@gmail.com";
		adminUsername = "maria@gmail.com";

		clientPassword = "123456";
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken =  adminToken + "p";

		postMovie.put("title", "Test Movie");
		postMovie.put("score", 0.0);
		postMovie.put("count", 0);
		postMovie.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");


	}


	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

		given()
				.get("/movies")
				.then()
				.statusCode(200)
				.body("content.title", hasItems("The Witcher", "Venom: Tempo de Carnificina", "Matrix Resurrections"));

	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		

		String movieName = "The Witcher";

		given()
				.get("/movies?={movieName}", movieName)
				.then()
				.statusCode(200)
				.body("content.id[0]", equalTo(1))
				.body("content.title[0]", equalTo("The Witcher"))
				.body("content.score[0]", is(4.5F))
				.body("content.count[0]", is(2))
				.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));

	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {

		existingMovieId = 2L;

		given()
				.get("movies/{id}", existingMovieId)
				.then()
				.statusCode(200)
				.body("id", equalTo(2))
				.body("title", equalTo("Venom: Tempo de Carnificina"))
				.body("score", is(3.3F))
				.body("count", is(3))
				.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/vIgyYkXkg6NC2whRbYjBD7eb3Er.jpg"));

	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {


		nonExistingMovieId = 40L;

		given()
				.get("movies/{id}", nonExistingMovieId)
				.then()
				.statusCode(404);

	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {		

		postMovie.put("title", "");

		//transforma objeto em json
		JSONObject jsonBody = new JSONObject(postMovie);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Title must be between 5 and 80 characters"))
				.body("errors.message[1]", equalTo("Required field"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

		//transforma objeto em json
		JSONObject jsonBody = new JSONObject(postMovie);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);

	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

		//transforma objeto em json
		JSONObject jsonBody = new JSONObject(postMovie);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(401);

	}
}

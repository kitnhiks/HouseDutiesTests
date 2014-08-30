package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class OccupantAPITest{

	private static final String HOUSE_URL = BASE_URL+"house/";
	private static String OCCUPANT_URL;
	private static String houseId;
	private static String occupantId;
	private static ArrayList<String> createdHouseIds = new ArrayList<String>();

	@Test
	public void should_200_POST_GET_DELETE_occupant_with_minimum_correct_json(){
		String newOccupant = "{\"name\":\"TEST_CORRECT_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);

		// POST occupant
		Response postOccupantResponse = HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant, headers);
		assertResponseStatusCode(200, postOccupantResponse);
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");

		// GET occupant
		assertResponseStatusCode(200, HttpHelper.getResource(OCCUPANT_URL+id, headers));

		// DELETE occupant
		assertResponseStatusCode(200, HttpHelper.deleteResource(OCCUPANT_URL+id, headers));
	}

	@Test
	public void should_500_POST_occupant_with_incorrect_json(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		String newOccupant = "{\"badkey\":\"TEST_INCORRECT_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		assertResponseStatusCode(500, HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant, headers));
	}

	@Test
	public void should_500_POST_occupant_with_invalid_json(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		String newOccupant = "name\":\"TEST_INVALID_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		assertResponseStatusCode(500, HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant, headers));
	}

	@Test
	public void should_403_POST_occupant_with_bad_key(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, "BAD_KEY");
		String newOccupant = "{\"name\":\"TEST_CORRECT_OCCUPANT_WITH_BAD_KEY\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		assertResponseStatusCode(403, HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant, headers));
	}

	@Test
	public void should_403_POST_occupant_with_no_key(){
		String newOccupant = "{\"name\":\"TEST_CORRECT_OCCUPANT_WITH_NO_KEY\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		assertResponseStatusCode(403, HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant));
	}

	@Test
	public void should_404_GET_occupant_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.getResource(OCCUPANT_URL+"9999999999999999999999999999999", headers));
	}

	@Test
	public void should_403_GET_occupant_with_bad_key(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, "BAD_KEY");
		assertResponseStatusCode(403, HttpHelper.getResource(OCCUPANT_URL+occupantId, headers));
	}

	@Test
	public void should_403_GET_occupant_unexisting(){
		assertResponseStatusCode(403, HttpHelper.getResource(OCCUPANT_URL+occupantId));
	}

	@Test
	public void should_403_DELETE_occupant_with_bad_key(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, "BAD_KEY");
		assertResponseStatusCode(403, HttpHelper.getResource(OCCUPANT_URL+occupantId, headers));
	}

	@Test
	public void should_403_DELETE_occupant_with_no_key(){
		assertResponseStatusCode(403, HttpHelper.getResource(OCCUPANT_URL+occupantId));
	}

	@Test
	public void should_404_DELETE_occupant_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.deleteResource(OCCUPANT_URL+"9999999999999999999999999999999", headers));
	}

	////////// Tools //////////

	@BeforeClass
	public static void createHouse(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE_OccupantAPITest\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			throw new RuntimeException(postHouseResponse.getStatusLine());
		}
		// Id
		houseId = new JsonPath(postHouseResponse.asString()).getString("id");

		createdHouseIds.add(houseId);

		// Url
		OCCUPANT_URL = HOUSE_URL+houseId+"/occupant/";

		String newOccupant = "{\"name\":\"TEST_OCCUPANT_OccupantAPITest\", \"password\":\"TEST_OCCUPANT_PWD\"}";

		// POST occupant
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		Response postOccupantResponse = HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant, headers);
		if (postOccupantResponse.getStatusCode()!=200){
			throw new RuntimeException(postOccupantResponse.getStatusLine());
		}
		occupantId = new JsonPath(postOccupantResponse.asString()).getString("id");
	}

	@AfterClass
	public static void cleanHouses(){
		// Remove created houses
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE_URL+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}
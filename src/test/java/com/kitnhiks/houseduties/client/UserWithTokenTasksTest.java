package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsTask;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class UserWithTokenTasksTest{

	private static final String HOUSE_URL = BASE_URL+"house/";
	private static final String TASKS_URL = BASE_URL+"tasks/";
	private static final String CATEGORIES_URL = TASKS_URL+"categories/";
	private static ArrayList<String> createdHouseIds = new ArrayList<String>();
	private static ArrayList<String> createdOccupantsIds = new ArrayList<String>();

	private static String houseId;
	private static String occupantId;
	private static String token;

	@BeforeClass
	public static void createHouseAndOccupant(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			fail (postHouseResponse.getStatusLine());
		}
		// Id
		houseId = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(houseId);
		// Token
		token = postHouseResponse.header(AUTH_KEY_HEADER);

		// Post Occupant
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		String newOccupant = "{\"name\":\"TEST_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers);
		if (postOccupantResponse.getStatusCode()!=200){
			fail (postOccupantResponse.getStatusLine());
		}
		occupantId = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(occupantId);
	}

	@Test
	public void as_a_connected_user_i_can_retrieve_all_tasks(){
		// List all tasks
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		Response getTasksResponse = HttpHelper.getResource(TASKS_URL+1, headers);

		if (getTasksResponse.getStatusCode()!=200){
			fail(getTasksResponse.getStatusLine());
		}

		JSONArray tasksList = (JSONArray) JSONValue.parse(getTasksResponse.asString());
		for(Object task : tasksList){
			assertJsonIsTask((JSONObject) task);
		}
	}
	
	@Test
	public void as_a_connected_user_i_can_retrieve_all_categories(){
		// List all categories
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		Response getCategoriesResponse = HttpHelper.getResource(CATEGORIES_URL, headers);

		if (getCategoriesResponse.getStatusCode()!=200){
			fail(getCategoriesResponse.getStatusLine());
		}

		JSONArray categoriesList = (JSONArray) JSONValue.parse(getCategoriesResponse.asString());
		for(Object category : categoriesList){
			assertJsonIsCategory((JSONObject) category);
		}
	}
	
	@Test
	public void as_a_connected_user_i_can_retrieve_all_tasks_from_a_given_category(){
		// List all tasks
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		Response getTasksResponse = HttpHelper.getResource(TASKS_URL+1, headers);

		if (getTasksResponse.getStatusCode()!=200){
			fail(getTasksResponse.getStatusLine());
		}

		JSONArray tasksList = (JSONArray) JSONValue.parse(getTasksResponse.asString());
		for(Object task : tasksList){
			assertJsonIsTask((JSONObject) task);
			assertEquals(1, ((Number)((JSONObject) task).get("categoryId")).intValue());
		}
	}

	@AfterClass
	public static void cleanHousesAndOccupants(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);

		// Remove created occupants
		for (String id : createdOccupantsIds){
			HttpHelper.deleteResource(HOUSE_URL+houseId+"/occupant/"+id+"/tasks", headers);
			HttpHelper.deleteResource(HOUSE_URL+houseId+"/occupant/"+id, headers);
		}
		createdOccupantsIds = new ArrayList<String>();

		// Remove created houses
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE_URL+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}
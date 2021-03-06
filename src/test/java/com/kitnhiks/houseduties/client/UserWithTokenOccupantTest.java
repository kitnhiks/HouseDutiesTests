package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsOccupant;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class UserWithTokenOccupantTest{

	private static final String HOUSE_URL = BASE_URL+"house/";
	private static final String TASKS_URL = BASE_URL+"tasks/";
	private static ArrayList<String> createdHouseIds = new ArrayList<String>();
	private static ArrayList<String> createdOccupantsIds = new ArrayList<String>();

	private static String houseId;
	private static String token;

	@BeforeClass
	public static void createHouse(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE_UserWithTokenOccupantTest\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			fail(postHouseResponse.getStatusLine());
		}
		// Id
		houseId = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(houseId);
		// Token
		token = postHouseResponse.header(AUTH_KEY_HEADER);
	}

	@Test
	public void as_a_connected_user_i_can_add_an_occupant_to_my_house(){
		// Post Occupant
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		String newOccupant = "{\"name\":\"TEST_NEW_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers);

		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}

		// Id ?
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(id);

		Response getOccupantsResponse = HttpHelper.getResource(HOUSE_URL+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail(getOccupantsResponse.getStatusLine());
		}

		List<Map<String, String>> occupantsList = new JsonPath(getOccupantsResponse.asString()).getList("");

		assertEquals(1, occupantsList.size());
		Map <String, String> occupant =occupantsList.get(0);
		assertJsonIsOccupant(occupant);
		assertEquals("TEST_NEW_OCCUPANT", occupant.get("name"));
		assertEquals(0, occupant.get("points"));
	}

	@Test
	public void as_a_connected_user_i_can_add_an_occupant_without_password_to_my_house(){
		// Post Occupant
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		String newOccupant = "{\"name\":\"TEST_NEW_OCCUPANT_WITHOUT_PASSWORD\"}";
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers);

		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}

		// Id ?
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(id);

		Response getOccupantsResponse = HttpHelper.getResource(HOUSE_URL+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail(getOccupantsResponse.getStatusLine());
		}

		List<Map<String, String>> occupantsList = new JsonPath(getOccupantsResponse.asString()).getList("");

		assertEquals(1, occupantsList.size());
		Map <String, String> occupant = occupantsList.get(0);
		assertJsonIsOccupant(occupant);
		assertEquals("TEST_NEW_OCCUPANT_WITHOUT_PASSWORD", occupant.get("name"));
		assertEquals(0, occupant.get("points"));
	}

	public void as_a_connected_user_i_can_remove_an_occupant_from_my_house(){
		// Retrieve created house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		String newOccupant = "{\"name\":\"TEST_OCCUPANT_UserWithTokenOccupantTest\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers);

		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}

		// Id ?
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(id);

		Response deleteOccupantsResponse = HttpHelper.deleteResource(HOUSE_URL+houseId+"/occupant/"+id, headers);


		Response getOccupantsResponse = HttpHelper.getResource(HOUSE_URL+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail(getOccupantsResponse.getStatusLine());
		}

		createdOccupantsIds.remove(id);

		List<Map<String, String>> occupantsList = new JsonPath(deleteOccupantsResponse.asString()).getList("");

		assertEquals(0, occupantsList.size());
	}

	@Test
	public void as_a_connected_user_i_can_list_all_occupants_from_my_house_in_alphabetical_order(){
		// Add occupants
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);

		ArrayList<String> newOccupants = new ArrayList<String>();
		newOccupants.add("{\"name\":\"CTEST_OCCUPANT1\"}");
		newOccupants.add("{\"name\":\"ATEST_OCCUPANT2\", \"password\":\"TEST_OCCUPANT2_PWD\"}");
		newOccupants.add("{\"name\":\"BTEST_OCCUPANT3\"}");


		Response postOccupantResponse;
		ArrayList<String> ids = new ArrayList<String>();
		String id;
		for (String newOccupant : newOccupants){
			postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers);
			if (postOccupantResponse.getStatusCode()!=200){
				fail(postOccupantResponse.getStatusLine());
			}
			// Id ?
			id = new JsonPath(postOccupantResponse.asString()).getString("id");
			ids.add(id);
			createdOccupantsIds.add(id);
		}

		Response getOccupantsResponse = HttpHelper.getResource(HOUSE_URL+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail(getOccupantsResponse.getStatusLine());
		}

		List<Map<String, String>> occupantsList = new JsonPath(getOccupantsResponse.asString()).getList("");

		assertEquals(3, occupantsList.size());


		assertEquals("ATEST_OCCUPANT2", occupantsList.get(0).get("name"));
		assertEquals("BTEST_OCCUPANT3", occupantsList.get(1).get("name"));
		assertEquals("CTEST_OCCUPANT1", occupantsList.get(2).get("name"));
	}

	@Test
	public void as_a_connected_user_i_can_list_all_occupants_from_my_house_in_alphabetical_order_even_after_adding_a_task(){
		// Add occupants
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);

		ArrayList<String> newOccupants = new ArrayList<String>();
		newOccupants.add("{\"name\":\"CTEST_OCCUPANT1\"}");
		newOccupants.add("{\"name\":\"ATEST_OCCUPANT2\", \"password\":\"TEST_OCCUPANT2_PWD\"}");
		newOccupants.add("{\"name\":\"BTEST_OCCUPANT3\"}");


		Response postOccupantResponse;
		ArrayList<String> ids = new ArrayList<String>();
		String id;
		for (String newOccupant : newOccupants){
			postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers);
			if (postOccupantResponse.getStatusCode()!=200){
				fail(postOccupantResponse.getStatusLine());
			}
			// Id ?
			id = new JsonPath(postOccupantResponse.asString()).getString("id");
			ids.add(id);
			createdOccupantsIds.add(id);
		}

		// Add task to occupant	
		// List all tasks
		Response getTasksResponse = HttpHelper.getResource(TASKS_URL+1, headers);

		if (getTasksResponse.getStatusCode()!=200){
			fail(getTasksResponse.getStatusLine());
		}

		JSONArray tasksList = (JSONArray) JSONValue.parse(getTasksResponse.asString());

		// Choose one task
		JSONObject newTask = (JSONObject) tasksList.get(tasksList.size()/2);
		String newTaskJson = JSONValue.toJSONString(newTask);
		Response postTaskResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+ids.get(1)+"/task", newTaskJson, headers);
		if (postTaskResponse.getStatusCode()!=200){
			fail(postTaskResponse.getStatusLine());
		}

		Response getOccupantsResponse = HttpHelper.getResource(HOUSE_URL+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail(getOccupantsResponse.getStatusLine());
		}

		List<Map<String, String>> occupantsList = new JsonPath(getOccupantsResponse.asString()).getList("");

		assertEquals(3, occupantsList.size());


		assertEquals("ATEST_OCCUPANT2", occupantsList.get(0).get("name"));
		assertEquals("BTEST_OCCUPANT3", occupantsList.get(1).get("name"));
		assertEquals("CTEST_OCCUPANT1", occupantsList.get(2).get("name"));
	}

	@Test
	public void as_a_connected_user_i_can_visualize_the_house_highscores(){
		// Add occupants
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);

		ArrayList<String> newOccupants = new ArrayList<String>();
		newOccupants.add("{\"name\":\"CTEST_OCCUPANT1\"}");
		newOccupants.add("{\"name\":\"ATEST_OCCUPANT2\", \"password\":\"TEST_OCCUPANT2_PWD\"}");
		newOccupants.add("{\"name\":\"BTEST_OCCUPANT3\"}");

		Response postOccupantResponse;
		ArrayList<String> ids = new ArrayList<String>();
		String id;
		for (String newOccupant : newOccupants){
			postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers);
			if (postOccupantResponse.getStatusCode()!=200){
				fail(postOccupantResponse.getStatusLine());
			}
			// Id ?
			id = new JsonPath(postOccupantResponse.asString()).getString("id");
			ids.add(id);
			createdOccupantsIds.add(id);
		}

		// List all tasks
		Response getTasksResponse = HttpHelper.getResource(TASKS_URL+1, headers);
		if (getTasksResponse.getStatusCode()!=200){
			fail(getTasksResponse.getStatusLine());
		}
		JSONArray tasksList = (JSONArray) JSONValue.parse(getTasksResponse.asString());

		// Choose one task
		JSONObject newTask = (JSONObject) tasksList.get(0);
		String newTaskJson = JSONValue.toJSONString(newTask);

		// Add 2 tasks to occupant 1
		for (int i=0; i<2; i++){
			postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+ids.get(0)+"/task", newTaskJson, headers);
			if (postOccupantResponse.getStatusCode()!=200){
				fail(postOccupantResponse.getStatusLine());
			}
		}

		// Add 3 tasks to occupant 2
		for (int i=0; i<3; i++){
			postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+ids.get(1)+"/task", newTaskJson, headers);
			if (postOccupantResponse.getStatusCode()!=200){
				fail(postOccupantResponse.getStatusLine());
			}
		}

		// Add 1 tasks to occupant 3
		postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+ids.get(2)+"/task", newTaskJson, headers);
		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}

		// Retrieve occupant ladder and check order
		Response getOccupantsLadder = HttpHelper.getResource(HOUSE_URL+houseId+"/occupants/ladder", headers);
		if (getOccupantsLadder.getStatusCode()!=200){
			fail(getOccupantsLadder.getStatusLine());
		}

		JSONArray occupantsList = (JSONArray) JSONValue.parse(getOccupantsLadder.asString());

		assertEquals(3, occupantsList.size());

		assertEquals("ATEST_OCCUPANT2", (String) ((JSONObject) occupantsList.get(0)).get("name"));
		assertEquals("CTEST_OCCUPANT1", (String) ((JSONObject) occupantsList.get(1)).get("name"));
		assertEquals("BTEST_OCCUPANT3", (String) ((JSONObject) occupantsList.get(2)).get("name"));
	}

	@Test
	public void as_a_connected_user_i_cannot_add_an_occupant_to_another_house(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE2\", \"password\":\"TEST_HOUSE2_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			fail(postHouseResponse.getStatusLine());
		}
		// Id
		String id = new JsonPath(postHouseResponse.asString()).getString("id");

		createdHouseIds.add(id);
		// Token
		String token2 = postHouseResponse.header(AUTH_KEY_HEADER);

		// Post Occupant
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token2);
		String newOccupant = "{\"name\":\"TEST_OCCUPANT_UserWithTokenOccupantTest\", \"password\":\"TEST_OCCUPANT_PWD\"}";

		assertResponseStatusCode(403, HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant", newOccupant, headers));

	}

	@After
	public void cleanOccupants(){
		// Remove created occupants
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdOccupantsIds){
			HttpHelper.deleteResource(HOUSE_URL+houseId+"/occupant/"+id, headers);
		}
		createdOccupantsIds = new ArrayList<String>();
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
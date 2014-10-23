package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsAssignedTask;
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

public class UserWithTokenOccupantTasksTest{

	private static final String HOUSE_URL = BASE_URL+"house/";
	private static final String TASKS_URL = BASE_URL+"tasks/";
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
	public void as_a_connected_user_i_can_add_a_task_to_an_occupant_todo_list_and_gain_the_points(){
		// List all tasks
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		Response getTasksResponse = HttpHelper.getResource(TASKS_URL, headers);

		if (getTasksResponse.getStatusCode()!=200){
			fail(getTasksResponse.getStatusLine());
		}

		//List<Map<String, Object>> tasksList = new JsonPath(getTasksResponse.asString()).getList("");
		JSONArray tasksList = (JSONArray) JSONValue.parse(getTasksResponse.asString());

		// Choose one task
		JSONObject newTask = (JSONObject) tasksList.get(tasksList.size()/2);
		String newTaskJson = JSONValue.toJSONString(newTask);
		int newTaskId = ((Number) newTask.get("id")).intValue();
		int newTaskPoints = ((Number) newTask.get("points")).intValue();

		// Add task to occupant	
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+occupantId+"/task", newTaskJson, headers);
		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}

		// Retrieve occupant tasks and check existence of added task
		Response getOccupantTasks = HttpHelper.getResource(HOUSE_URL+houseId+"/occupant/"+occupantId+"/tasks", headers);
		if (getOccupantTasks.getStatusCode()!=200){
			fail(getOccupantTasks.getStatusLine());
		}

		//List<Map<String, Object>> occupantTasksList = new JsonPath(getOccupantTasks.asString()).getList("");
		JSONArray occupantTasksList = (JSONArray) JSONValue.parse(getOccupantTasks.asString());

		assertEquals(1, occupantTasksList.size());
		JSONObject task = (JSONObject) occupantTasksList.get(0);
		assertJsonIsAssignedTask(task);
		assertEquals(newTaskId, ((Number) task.get("id")).intValue());

		// Retrieve occupant and check his points 
		Response getOccupant = HttpHelper.getResource(HOUSE_URL+houseId+"/occupant/"+occupantId, headers);
		if (getOccupant.getStatusCode()!=200){
			fail(getOccupant.getStatusLine());
		}

		JSONObject occupant = (JSONObject) JSONValue.parse(getOccupant.asString());

		assertEquals(newTaskPoints, ((Number) occupant.get("points")).intValue());

	}

	@Test
	public void as_an_non_occupant_i_can_not_add_any_task_to_the_occupant_of_a_house(){

		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE2\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			fail (postHouseResponse.getStatusLine());
		}
		// Id
		String house2Id = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(house2Id);
		// Token
		String token2 = postHouseResponse.header(AUTH_KEY_HEADER);

		// List all tasks
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token2);
		Response getTasksResponse = HttpHelper.getResource(TASKS_URL, headers);

		if (getTasksResponse.getStatusCode()!=200){
			fail(getTasksResponse.getStatusLine());
		}

		JSONArray tasksList = (JSONArray) JSONValue.parse(getTasksResponse.asString());

		// Choose one task
		JSONObject newTask = (JSONObject) tasksList.get(tasksList.size()/2);
		String newTaskJson = JSONValue.toJSONString(newTask);

		// Add task to occupant	
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+occupantId+"/task", newTaskJson, headers);
		assertEquals(403, postOccupantResponse.getStatusCode());
	}

	@Test
	public void as_a_connected_user_i_can_retrieve_an_occupant_tasks(){
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

		// List all tasks
		Response getTasksResponse = HttpHelper.getResource(TASKS_URL, headers);
		if (getTasksResponse.getStatusCode()!=200){
			fail(getTasksResponse.getStatusLine());
		}
		JSONArray tasksList = (JSONArray) JSONValue.parse(getTasksResponse.asString());
		
		JSONObject task0 = (JSONObject) tasksList.get(0);
		JSONObject task1 = (JSONObject) tasksList.get(2);
		JSONObject task2 = (JSONObject) tasksList.get(3);

		// Add 3 tasks to occupant
		postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+id+"/task", JSONValue.toJSONString(task0), headers);
		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}
		postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+id+"/task", JSONValue.toJSONString(task1), headers);
		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}
		postOccupantResponse = HttpHelper.postResourceJson(HOUSE_URL+houseId+"/occupant/"+id+"/task", JSONValue.toJSONString(task2), headers);
		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusLine());
		}
	
		Response getOccupantTasksResponse = HttpHelper.getResource(HOUSE_URL+houseId+"/occupant/"+id+"/tasks", headers);

		if (getOccupantTasksResponse.getStatusCode()!=200){
			fail(getOccupantTasksResponse.getStatusLine());
		}

		JSONArray occupantTasksList = (JSONArray) JSONValue.parse(getOccupantTasksResponse.asString());

		assertEquals(3, occupantTasksList.size());
		assertEquals(task0.get("id"), ((JSONObject) occupantTasksList.get(0)).get("id"));
		assertEquals(task1.get("id"), ((JSONObject) occupantTasksList.get(1)).get("id"));
		assertEquals(task2.get("id"), ((JSONObject) occupantTasksList.get(2)).get("id"));
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
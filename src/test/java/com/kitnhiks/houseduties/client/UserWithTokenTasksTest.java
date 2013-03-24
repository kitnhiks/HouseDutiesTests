package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class UserWithTokenTasksTest{

	private static final String HOUSE_URL = BASE_URL+"house/";
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

	public void as_a_connected_user_i_can_add_a_task_to_the_house_todo_list(){
		fail("tbi");
		// List all tasks

		// Choose one task

		// Add task to house

		// REtrieve house tasks and check existence of added task
	}

	public void as_a_connected_user_i_can_add_a_task_to_an_occupant_todo_list(){
		fail("tbi");
	}

	public void as_a_connected_user_i_can_visualize_all_tasks_done_by_an_occupant(){
		fail("tbi");
	}

	public void as_a_connected_user_i_can_mark_as_done_tasks_by_an_occupant_so_he_gains_the_associated_points(){
		fail("tbi");
	}

	public void as_an_nonhabitant_i_can_not_add_any_task_to_the_house(){
		fail("tbi");
	}

	public void cleanHouseTasks(){
		// TODO
	}

	public void cleanOccupantTasks(){
		// TODO
	}

	@AfterClass
	public static void cleanHousesAndOccupants(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);

		// Remove created occupants
		for (String id : createdOccupantsIds){
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
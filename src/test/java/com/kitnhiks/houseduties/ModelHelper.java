package com.kitnhiks.houseduties;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.jayway.restassured.path.json.JsonPath;


public class ModelHelper {

	public static void assertJsonIsHouse(JsonPath json){
		Map<String, String> house = json.get();
		assertJsonIsHouse(house);
	}

	public static void assertJsonIsHouse(Map<String, String> json){
		assertThat(json.keySet()).containsOnly("id", "name", "occupants", "tasks");
		assertThat(json.get("password")).isNull();
	}

	public static void assertJsonIsListOfHouse(JsonPath json){
		List<Map<String, String>> houseList = json.getList("");

		for (Map<String, String> house : houseList){
			assertJsonIsHouse(house);
		}
	}

	public static void assertJsonIsOccupant(Map<String, String> json){
		assertThat(json.keySet()).containsOnly("id", "name", "password", "email", "points", "tasks");
		assertThat(json.get("password")).isNull();
	}
	
	public static void assertJsonIsAssignedTask(JSONObject json){
		assertThat(json.keySet()).containsOnly("id", "name", "points", "categoryId", "priority", "doneDate");
	}
	
	public static void assertJsonIsTask(JSONObject json){
		assertThat(json.keySet()).containsOnly("id", "name", "points", "categoryId");
	}
}

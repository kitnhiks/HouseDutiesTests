package com.kitnhiks.houseduties;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.jayway.restassured.path.json.JsonPath;


public class ModelHelper {
	
	public static void assertJsonIsHouse(JsonPath json){
		Map<String, String> house = json.get();
		assertJsonIsHouse(house);
	}
	
	public static void assertJsonIsHouse(Map<String, String> json){
		assertThat(json.keySet()).containsOnly("id", "name", "occupants", "password");
		assertThat(json.get("password")).isNull();
	}
	
	public static void assertJsonIsListOfHouse(JsonPath json){
		List<Map<String, String>> houseList = json.getList("");
		
		for (Map<String, String> house : houseList){
			assertJsonIsHouse(house);
		}
	}
}
package car.tp2.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	private static Map<String, String> map = new HashMap<>();
	
	private Constants() {}
	
	public static String get(String key) {
		return map.get(key);
	}
	
	public static void put(String key, String value) {
		map.put(key, value);
	}

}

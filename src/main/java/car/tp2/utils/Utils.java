package car.tp2.utils;

import java.nio.file.Paths;

public class Utils {
	public static String getNormalizedPath(final String path) {
		return Paths.get(path.replace("//", "/")).normalize().toString().replace("\\", "/");
	}
}

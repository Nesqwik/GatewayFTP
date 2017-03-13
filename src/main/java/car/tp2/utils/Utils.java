package car.tp2.utils;

import java.nio.file.Paths;

/**
 * 
 * @author Louis GUILBERT et Jonathan LECOINTE
 *
 * Utils : 
 * Quelques fonctions utilitaires
 */
public class Utils {
	/**
	 * Normalise le chemin
	 * @param path chemin à normaliser
	 * @return chemin normalisé
	 */
	public static String getNormalizedPath(final String path) {
		return Paths.get(path.replace("//", "/")).normalize().toString().replace("\\", "/");
	}
}

package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Kayler
 * @since 11/12/2017
 */
public class GameNameMap {

	public enum LookupType {
		FULL_NAME, SHORT_NAME, LINK_PREFIX
	}

	private static final GameNameMap INSTANCE = new GameNameMap();

	@NotNull
	public static GameNameMap getInstance() {
		return INSTANCE;
	}

	private final HashMap<LookupType, MapBucketHashMap> hashMap = new HashMap<>();

	private GameNameMap() {
		for (LookupType type : LookupType.values()) {
			hashMap.put(type, new MapBucketHashMap());
		}

		put(new BIGame("Take On Helicopters", "TKOH", "TKOH"));
		put(new BIGame("Operation Flashpoint", "OFP", "ofp"));
		put(new BIGame("Arma 2: Army of the Czech Republic", "Arma 2:ACR", "arma2acr"));
		put(new BIGame("Arma 3: Zeus", "Zeus", "zeus"));
		put(new BIGame("Arma 2: British Armed Forces", "Arma 2:BAF", "arma2baf"));
		put(new BIGame("Australians in Vietnam", "AiV", "aiv"));
		put(new BIGame("Take On Mars", "TKOM", "TKOM"));
		put(new BIGame("Arma 2: Private Military Company", "Arma 2:PMC", "arma2pmc"));
		put(new BIGame("Bohemia Interactive", "BI", "bi"));
		put(new BIGame("Operation Flashpoint: Elite", "OFP:E", "ofpe"));
		put(new BIGame("Arma 2", "Arma 2", "arma2"));
		put(new BIGame("Arma 2: Operation Arrowhead", "Arma 2:OA", "arma2oa"));
		put(new BIGame("Armed Assault", "Arma", "arma"));
		put(new BIGame("Arma 3 Development Branch", "Arma 3 Dev", "arma3dev"));
		put(new BIGame("Virtual Battlespace 3", "VBS3", "vbs3"));
		put(new BIGame("Operation Flashpoint: Resistance", "OFP:R", "ofpr"));
		put(new BIGame("Arma 3", "Arma 3", "arma3"));
		put(new BIGame("Virtual Battlespace 1", "VBS1", "vbs1"));
		put(new BIGame("Virtual Battlespace 2", "VBS2", "vbs2"));
	}

	@NotNull
	public List<BIGame> getAllGames() {
		MapBucketHashMap bucketHashMap = hashMap.get(LookupType.FULL_NAME);
		List<BIGame> list = new ArrayList<>(bucketHashMap.size());
		bucketHashMap.forEach(new BiConsumer<String, BIGame>() {
			@Override
			public void accept(String s, BIGame biGame) {
				list.add(biGame);
			}
		});
		return list;
	}

	@NotNull
	public BIGame getGame(@NotNull LookupType type, @NotNull String query) {
		return hashMap.get(type).get(query);
	}

	private void put(@NotNull BIGame biGame) {
		BIGame put = hashMap.get(LookupType.FULL_NAME).put(biGame.getFullName(), biGame);
		if (put != null) {
			System.err.println("WARNING: linkPrefix collision:" + biGame);
		}
		hashMap.get(LookupType.SHORT_NAME).put(biGame.getShortName(), biGame);
		hashMap.get(LookupType.LINK_PREFIX).put(biGame.getLinkPrefix(), biGame);
	}


	private static class MapBucketHashMap extends HashMap<String, BIGame> {

	}
}

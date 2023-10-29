package com.nokona.formatter;

import com.nokona.model.LevelCode;

public class LevelCodeFormatter {

	public static LevelCode format(LevelCode levelCode) {
		levelCode.setLevelCode(formatLevelCode(levelCode.getLevelCode()));
		return levelCode;
	}

	public static String formatLevelCode(String levelCode) {
		return levelCode;
	}

}

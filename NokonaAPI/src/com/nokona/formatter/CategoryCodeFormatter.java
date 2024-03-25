package com.nokona.formatter;

import com.nokona.model.CategoryCode;

public class CategoryCodeFormatter {

	public static CategoryCode format(CategoryCode categoryCode) {
		categoryCode.setCategoryCode(formatCategoryCode(categoryCode.getCategoryCode()));
		return categoryCode;
	}

	public static String formatCategoryCode(String categoryCode) {
		return categoryCode;
	}

}

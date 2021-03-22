package com.nokona.formatter;

import com.nokona.model.User;

public class UserFormatter {

	public static User format(User user) {
		user.setFirstName(formatFirstName(user.getFirstName()));
		user.setLastName(formatLastName(user.getLastName()));
		user.setUserId(formatUserId(user.getUserId()));
		return user;
	}
	public static String formatFirstName(String firstName) {
		return firstName.trim().toUpperCase();
	}
	public static String formatLastName(String lastName) {
		return lastName.trim().toUpperCase();
	}
	public static String formatUserId(String userId) {
		return userId.trim().toUpperCase();
	}
}

package ru.job4j.olds;

public class PasswordValidator {
    public static String validate(String password) throws IllegalArgumentException {
        if (null == password) {
            throw new IllegalArgumentException();
        }
        if (password.length() < 8 || password.length() > 32) {
            throw new IllegalArgumentException("Password must consist of 8 to 32 symbols");
        }
        if (password.toLowerCase().equals(password)) {
            throw new IllegalArgumentException(
                    "You need to have at least 1 capital letter in password");
        }
        if (password.toUpperCase().equals(password)) {
            throw new IllegalArgumentException(
                    "You need to have at least 1 cursive letter in password");
        }
        if (!password.matches(".*\\d+.*")) {
            throw new IllegalArgumentException("You need to have at least 1 number in password");
        }
        if (!password.matches(".*[^a-z\\dA-z].*")) {
            throw new IllegalArgumentException(
                    "You need to have at least 1 special symbol in password");
        }
        String[] subStr = {"qwerty", "password", "admin", "user", "12345"};
        for (String str : subStr) {
            if (password.toLowerCase().contains(str)) {
                throw new IllegalArgumentException("You have to avoid typed strings");
            }
        }

        return password;
    }
}

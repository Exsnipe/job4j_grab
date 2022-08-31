package olds;

import org.junit.Test;
import ru.job4j.olds.PasswordValidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PasswordValidatorTest {
    @Test
    public  void whenPasswordIsNullThenException() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate(null)
        );
        assertEquals("There are no password", thrown.getMessage());
    }

    @Test
    public void whenPasswordIsShortThenException() {

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate("MaA$5fF")
        );
        assertEquals("Password must consist of 8 to 32 symbols", thrown.getMessage());
    }

   @Test
    public void whenPasswordIsLongThenException() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate("aA$5dfgghj677hjku$^&fhkyiljhfrdt8")
        );
        assertEquals("Password must consist of 8 to 32 symbols", thrown.getMessage());
    }

    @Test
    public void whenOnlyLowercaseLettersThenException() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate("maa$5ffj")
        );
        assertEquals("You need to have at least 1 capital letter in password", thrown.getMessage());
    }

    @Test
    public void whenOnlyCapitalLettersThenException() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate("MAA$5FFJ")
        );
        assertEquals("You need to have at least 1 cursive letter in password", thrown.getMessage());
    }

    @Test
    public void whenThereAreNoSpecialSymbolsThenException() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate("JjKk4kKl")
        );
        assertEquals("You need to have at least 1 special symbol in password", thrown.getMessage());
    }

    @Test
    public void whenThereAreTypedStringInPasswordThenException() {

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate("JjKk4kKl&qWerTY")
        );
        assertEquals("You have to avoid typed strings", thrown.getMessage());
    }

    @Test
    public void whenReturnPassword() {
        String password = "JjKk*4kKl";
        assertEquals(PasswordValidator.validate(password), password);
    }
}



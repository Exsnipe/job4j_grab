package olds;

import org.junit.Test;
import ru.job4j.olds.PasswordValidator;

import static org.junit.Assert.assertEquals;

public class PasswordValidatorTest {
    @Test(expected = IllegalArgumentException.class)
    public  void whenPasswordIsNullThenException() {
        PasswordValidator.validate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenPasswordIsShortThenException() {
        PasswordValidator.validate("MaA$5fF");
    }

   @Test(expected = IllegalArgumentException.class)
    public void whenPasswordIsLongThenException() {
        PasswordValidator.validate("aA$5dfgghj677hjku$^&fhkyiljhfrdt8");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenOnlyCursiveLettersThenException() {
        PasswordValidator.validate("maa$5ffj");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenOnlyCapitalLettersThenException() {
        PasswordValidator.validate("MAA$5FFJ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenThereAreNoSpecialSymbolsThenException() {
        PasswordValidator.validate("JjKk4kKl");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenThereAreTypedStringInPasswordThenException() {
        PasswordValidator.validate("JjKk4kKl&qWerTY");
    }

    @Test
    public void whenReturnPassword() {
        String password = "JjKk*4kKl";
        assertEquals(PasswordValidator.validate(password), password);
    }
}



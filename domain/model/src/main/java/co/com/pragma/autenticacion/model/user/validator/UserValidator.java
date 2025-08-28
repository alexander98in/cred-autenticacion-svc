package co.com.pragma.autenticacion.model.user.validator;

import co.com.pragma.autenticacion.model.user.UserConstraints;

import java.math.BigDecimal;

public final class UserValidator {

    private UserValidator() {}
    public static boolean isSalaryInRange(BigDecimal s) {
        return s != null &&
                s.compareTo(UserConstraints.MIN_SALARY) >= 0 &&
                s.compareTo(UserConstraints.MAX_SALARY) <= 0;
    }
}

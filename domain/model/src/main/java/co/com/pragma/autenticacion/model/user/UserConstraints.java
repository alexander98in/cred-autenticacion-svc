package co.com.pragma.autenticacion.model.user;

import java.math.BigDecimal;

public final class UserConstraints {
    private UserConstraints() {}
    public static final BigDecimal MIN_SALARY = new BigDecimal("10.0");
    public static final BigDecimal MAX_SALARY = new BigDecimal("15000000.0");
}

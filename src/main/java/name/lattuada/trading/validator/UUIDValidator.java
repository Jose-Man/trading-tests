package name.lattuada.trading.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.UUID;

public final class UUIDValidator implements ConstraintValidator<name.lattuada.trading.validator.UUID, UUID> {

    @Override
    public void initialize(name.lattuada.trading.validator.UUID notUsed) {
        // Not needed
    }

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext notUsed) {
        String regex = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
        return uuid.toString().toLowerCase(Locale.ROOT).matches(regex);
    }

}

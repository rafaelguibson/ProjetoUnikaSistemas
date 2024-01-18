package wicket.enums;

import org.apache.wicket.util.convert.IConverter;
import java.util.Locale;

public class EstadoConverter implements IConverter<Estado> {

    @Override
    public Estado convertToObject(String value, Locale locale) {
        try {
            return Estado.valueOf(value);
        } catch (IllegalArgumentException e) {
            // Retorne null ou lance uma exceção personalizada se a string não corresponder a um Estado válido
            return null;
        }
    }

    @Override
    public String convertToString(Estado value, Locale locale) {
        return value != null ? value.name() : "";
    }
}

package wicket.enums;
import org.apache.wicket.util.convert.IConverter;
import java.util.Locale;

public class StatusConverter implements IConverter<Status> {
    @Override
    public Status convertToObject(String value, Locale locale) {
        if ("Ativado".equalsIgnoreCase(value)) {
            return Status.ATIVO;
        } else if ("Inativo".equalsIgnoreCase(value)) {
            return Status.INATIVO;
        }
        // Se nenhum valor corresponder, você pode lançar uma exceção ou retornar um valor padrão.
        return null; // ou lançar uma exceção
    }

    @Override
    public String convertToString(Status value, Locale locale) {
        return value.toString();
    }
}

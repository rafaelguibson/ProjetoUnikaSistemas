package wicket.configs;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.core.util.resource.locator.ResourceStreamLocator;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupNotFoundException;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.loader.IMarkupLoader;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.IOException;

public class CustomMarkupLoader implements IMarkupLoader {
    private final IMarkupLoader defaultMarkupLoader;

    public CustomMarkupLoader(IMarkupLoader defaultMarkupLoader) {
        this.defaultMarkupLoader = defaultMarkupLoader;
    }

    @Override
    public Markup loadMarkup(MarkupContainer container, MarkupResourceStream markupResourceStream, IMarkupLoader baseLoader, boolean enforceReload) throws IOException, ResourceStreamNotFoundException {
        String containerClassName = container.getClass().getName();

        // Substituir o caminho do pacote 'classes' por 'pages'
        if (containerClassName.startsWith("wicket.classes")) {
            String newPath = containerClassName.replace("wicket.classes", "wicket.pages");

            // Ajustando o caminho para corresponder ao arquivo HTML
            String htmlFilePath = newPath.replace('.', '/') + ".html";

            // Carregar o recurso
            IResourceStream resourceStream = new ResourceStreamLocator().locate(container.getClass(), htmlFilePath);
            if (resourceStream != null) {
                return new Markup(new MarkupResourceStream(resourceStream));
            }
        }

        // Se não for uma classe do pacote 'classes', usar o carregador padrão
        return defaultMarkupLoader.loadMarkup(container, markupResourceStream, baseLoader, enforceReload);
    }
}

package frontend;

import org.apache.wicket.markup.html.basic.Label;

public class InicioPage extends BasePage {

    public InicioPage() {
        Label label = new Label("inicio", "Bem Vindo ao Gerenciamento de monitoradores...");
        add(label);
    }
}

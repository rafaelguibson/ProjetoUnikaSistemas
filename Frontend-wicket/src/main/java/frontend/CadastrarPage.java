package frontend;

import org.apache.wicket.markup.html.basic.Label;

public class CadastrarPage extends BasePage {
    public CadastrarPage() {
        Label label = new Label("titulo", "Cadastrar Novo Monitorador");
        add(label);
    }
}

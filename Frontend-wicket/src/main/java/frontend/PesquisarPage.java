package frontend;

import frontend.entities.Endereco;
import frontend.entities.Monitorador;
import frontend.httpClient.MonitoradorHttpClient;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PesquisarPage extends BasePage implements Serializable {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    public PesquisarPage() {
        Label label = new Label("label", "Listar Monitoradores");
        add(label);

        List<Monitorador> mntList = monitoradorHttpClient.listarTodos();

        ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", mntList) {
            @Override
            protected void populateItem(ListItem<Monitorador> listItem) {
                List<String> propertyNames = Arrays.asList("id", "tipoPessoa", "cpf", "cnpj",
                        "nome", "razaoSocial", "telefone", "email",
                        "rg", "inscricaoEstadual", "dataNascimento","ativo");


                propertyNames.forEach(propertyName ->
                        listItem.add(new Label(propertyName, new PropertyModel<>(listItem.getModel(), propertyName)))
                );
            }
        };
        add(monitoradorList);
    }
}

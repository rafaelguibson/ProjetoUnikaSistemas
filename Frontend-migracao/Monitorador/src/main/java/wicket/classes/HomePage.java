package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ComponentPropertyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;
import wicket.entities.Monitorador;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;
import java.util.List;

public class HomePage extends BasePage implements Serializable {
	private static final long serialVersionUID = 1L;
	MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
	List<Monitorador> mntList;

	public HomePage(final PageParameters parameters) {


		mntList = monitoradorHttpClient.listarTodos();

		ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", mntList) {
			@Override
			protected void populateItem(ListItem<Monitorador> item) {

				item.add(new Label("id", new PropertyModel<String>(item.getModel(),"id")));
				item.add(new Label("tipoPessoa", new PropertyModel<String>(item.getModel(),"tipoPessoa")));
				item.add(new Label("cpf", new PropertyModel<String>(item.getModel(),"cpf")));
				item.add(new Label("cnpj", new PropertyModel<String>(item.getModel(),"cnpj")));
				item.add(new Label("nome", new PropertyModel<String>(item.getModel(),"nome")));
				item.add(new Label("razaoSocial", new PropertyModel<String>(item.getModel(),"razaoSocial")));
				item.add(new Label("telefone", new PropertyModel<String>(item.getModel(),"telefone")));
				item.add(new Label("email", new PropertyModel<String>(item.getModel(),"email")));
				item.add(new Label("rg", new PropertyModel<String>(item.getModel(),"rg")));
				item.add(new Label("inscricaoEstadual", new PropertyModel<String>(item.getModel(),"inscricaoEstadual")));
				item.add(new Label("dataNascimento", new PropertyModel<String>(item.getModel(),"dataNascimento")));
				item.add(new Label("ativo", new PropertyModel<String>(item.getModel(),"ativo")));
			}
		};
		add(monitoradorList);

	} //Fora do construtor
}

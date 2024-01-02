package frontend;

import frontend.entities.Endereco;
import frontend.entities.Monitorador;
import frontend.httpClient.MonitoradorHttpClient;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PesquisarPage extends BasePage implements Serializable {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    FeedbackPanel fp;
    public PesquisarPage() {
        WebMarkupContainer formNewPF = new WebMarkupContainer("formNewPF");


        Label label = new Label("label", "Listar Monitoradores");
        add(label);

        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupPlaceholderTag(true);
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        Form<Void> form = new Form<>("form");
        sectionForm.add(form);


        AjaxLink<Void> cadastrarPF = new AjaxLink<>("addItemLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                formNewPF.setVisible(!formNewPF.isVisible());
                target.add(formNewPF);
            }
        };
        form.add(cadastrarPF);


        formNewPF.setOutputMarkupPlaceholderTag(true);
        formNewPF.setVisible(false);
        form.add(formNewPF);

        Monitorador monitorador = new Monitorador();
        form.setDefaultModel(new CompoundPropertyModel<>(monitorador));

        TextField<String> nome = new TextField<String>("nome");
        TextField<String> cpf = new TextField<String>("cpf");
        TextField<String> telefone = new TextField<String>("telefone");
        TextField<String> email = new TextField<String>("email");
        TextField<String> rg = new TextField<String>("rg");
        DateTextField dataNascimento = new DateTextField("dataNascimento", "yyyy-MM-dd");
        CheckBox inputAtivo = new CheckBox("ativo");

        AjaxLink<Void> btnSave = new AjaxLink<>("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Monitorador monitoradorSalvar = new Monitorador();
                monitoradorSalvar.setTipoPessoa("Jurídica");
                monitoradorSalvar.setNome(nome.getValue());
                monitoradorSalvar.setCpf(cpf.getValue());
                monitoradorSalvar.setTelefone(telefone.getValue());
                monitoradorSalvar.setEmail(email.getValue());
                monitoradorSalvar.setRg(rg.getValue());
                monitoradorSalvar.setDataNascimento(new Date());
                monitoradorSalvar.setAtivo(true);
                monitoradorSalvar.setEnderecos(new ArrayList<Endereco>());
                monitoradorHttpClient.salvar(monitoradorSalvar);

                monitorador.setNome("");
                monitorador.setCpf("");
                monitorador.setTelefone("");
                monitorador.setEmail("");
                monitorador.setRg("");

                formNewPF.setVisible(false);

                showInfo(target, "Adicionado com Sucesso!!");

                target.add(sectionForm);
            }
        };
        btnSave.add(new AjaxFormSubmitBehavior(form,"click") {});
        formNewPF.add(nome,cpf,telefone,email,rg,dataNascimento, inputAtivo, btnSave);

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
        monitoradorList.setOutputMarkupPlaceholderTag(true);
        monitoradorList.setOutputMarkupId(true);
        monitoradorList.setReuseItems(true);
        form.add(monitoradorList);
    }

    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }
}

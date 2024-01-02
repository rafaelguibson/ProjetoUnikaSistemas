package frontend;

import frontend.entities.Endereco;
import frontend.entities.Monitorador;
import frontend.httpClient.MonitoradorHttpClient;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.ArrayList;
import java.util.Date;


public class CadastrarPage extends BasePage {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    public CadastrarPage() {
        WebMarkupContainer formNewPF = new WebMarkupContainer("formNewPF");

        Label label = new Label("titulo", "Cadastrar Novo Monitorador");
        add(label);

        Form<Void> form = new Form<>("form");
        add(form);


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

                formNewPF.setVisible(false);
                target.add(formNewPF);
            }
        };
        btnSave.add(new AjaxFormSubmitBehavior(form,"click") {});
        formNewPF.add(nome,cpf,telefone,email,rg,dataNascimento, inputAtivo, btnSave);
    }
}

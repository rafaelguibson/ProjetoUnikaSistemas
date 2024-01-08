package frontend;

import frontend.Masks.MaskBehavior;
import frontend.entities.Endereco;
import frontend.entities.Monitorador;
import frontend.httpClient.MonitoradorHttpClient;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CadastrarPF extends Panel {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    FeedbackPanel fp;
    public CadastrarPF(String id) {
        super(id);
        Form<Void> form = new Form<>("form");
        add(form);

        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        TextField<String> nome = new TextField<String>("nome");
        TextField<String> cpf = new TextField<String>("cpf", Model.of(""));
        cpf.add(new MaskBehavior("000.000.000-00"));
        TextField<String> telefone = new TextField<String>("telefone");
        telefone.add(new MaskBehavior("(00) 00000-0000"));
        TextField<String> email = new TextField<String>("email");
        TextField<String> rg = new TextField<String>("rg");
        DateTextField dataNascimento = new DateTextField("dataNascimento", "yyyy-MM-dd");
        CheckBox inputAtivo = new CheckBox("ativo");

        Monitorador monitorador = new Monitorador();
        setDefaultModel(new CompoundPropertyModel<>(monitorador));
        AjaxLink<Void> btnSave = new AjaxLink<>("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Monitorador monitoradorSalvar = new Monitorador();
                monitoradorSalvar.setTipoPessoa("Física");
                monitoradorSalvar.setNome(nome.getValue());
                monitoradorSalvar.setCpf(cpf.getValue());
                monitoradorSalvar.setTelefone(telefone.getValue());
                monitoradorSalvar.setEmail(email.getValue());
                monitoradorSalvar.setRg(rg.getValue());
                monitoradorSalvar.setDataNascimento(new Date());
                monitoradorSalvar.setAtivo(true);
                monitoradorSalvar.setEnderecos(new ArrayList<Endereco>());

                if(Objects.nonNull(monitoradorHttpClient.salvar(monitoradorSalvar))) {
                    monitorador.setNome("");
                    monitorador.setCpf("");
                    monitorador.setTelefone("");
                    monitorador.setEmail("");
                    monitorador.setRg("");
                    showInfo(target, "Adicionado com Sucesso!!");
                } else {
                    showInfo(target, "Erro ao adicionar");
                }

            }
        };
        btnSave.add(new AjaxFormSubmitBehavior(form,"click") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
            }
        });

        form.add(nome,cpf,telefone, email, rg, dataNascimento, inputAtivo,btnSave);



    }
    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }
}

package frontend;


import frontend.Masks.MaskBehavior;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class CadastrarPJ extends Panel {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    FeedbackPanel fp;
    public CadastrarPJ(String id) {
        super(id);

        Form<Void> form = new Form<>("form");
        add(form);

        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        TextField<String> razaoSocial = new TextField<String>("razaoSocial");
        TextField<String> cnpj = new TextField<String>("cnpj", Model.of(""));
        cnpj.add(new MaskBehavior("00.000.000/0000-00"));
        TextField<String> telefone = new TextField<String>("telefone");
        telefone.add(new MaskBehavior("(00) 00000-0000"));
        TextField<String> email = new TextField<String>("email");
        TextField<String> inscricaoEstadual = new TextField<String>("inscricaoEstadual");
        CheckBox inputAtivo = new CheckBox("ativo");


        Monitorador monitorador = new Monitorador();
        setDefaultModel(new CompoundPropertyModel<>(monitorador));

        AjaxLink<Void> btnSave = new AjaxLink<>("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Monitorador monitoradorSalvar = new Monitorador();
                monitoradorSalvar.setTipoPessoa("Jurídica");
                monitoradorSalvar.setRazaoSocial(razaoSocial.getValue());
                monitoradorSalvar.setCnpj(cnpj.getValue());
                monitoradorSalvar.setTelefone(telefone.getValue());
                monitoradorSalvar.setEmail(email.getValue());
                monitoradorSalvar.setInscricaoEstadual(inscricaoEstadual.getValue());
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
        btnSave.add(new AjaxFormSubmitBehavior(form,"click") {});

        form.add(razaoSocial,cnpj,telefone, email, inscricaoEstadual,inputAtivo,btnSave);

    }
    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }
}

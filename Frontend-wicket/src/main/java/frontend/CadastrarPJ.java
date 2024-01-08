package frontend;


import frontend.Masks.MaskBehavior;
import frontend.entities.Endereco;
import frontend.entities.Monitorador;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CadastrarPJ extends Panel {
    public CadastrarPJ(String id) {
        super(id);

        Form<Void> form = new Form<>("form");
        add(form);
        TextField<String> razaoSocial = new TextField<String>("razaoSocial");
        TextField<String> cnpj = new TextField<String>("cnpj", Model.of(""));
        cnpj.add(new MaskBehavior("000.000.000-00"));
        TextField<String> telefone = new TextField<String>("telefone");
        TextField<String> email = new TextField<String>("email");
        TextField<String> inscricaoEstadual = new TextField<String>("inscricaoEstadual");
        CheckBox inputAtivo = new CheckBox("ativo");


        AjaxLink<Void> btnSave = new AjaxLink<>("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };
        btnSave.add(new AjaxFormSubmitBehavior(form,"click") {});

        form.add(razaoSocial,cnpj,telefone, email, inscricaoEstadual,inputAtivo,btnSave);

    }
}

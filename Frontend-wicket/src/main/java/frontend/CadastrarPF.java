package frontend;

import frontend.Masks.MaskBehavior;
import frontend.entities.Monitorador;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

public class CadastrarPF extends Panel {
    public CadastrarPF(String id) {
        super(id);
        Form<Void> form = new Form<>("form");
        add(form);

        TextField<String> nome = new TextField<String>("nome");
        TextField<String> cpf = new TextField<String>("cpf", Model.of(""));
        cpf.add(new MaskBehavior("000.000.000-00"));
        TextField<String> telefone = new TextField<String>("telefone");
        TextField<String> email = new TextField<String>("email");
        TextField<String> rg = new TextField<String>("rg");
        DateTextField dataNascimento = new DateTextField("dataNascimento", "yyyy-MM-dd");
        CheckBox inputAtivo = new CheckBox("ativo");

        Monitorador monitorador = new Monitorador();
        setDefaultModel(new CompoundPropertyModel<>(monitorador));
        AjaxLink<Void> btnSave = new AjaxLink<>("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };
        btnSave.add(new AjaxFormSubmitBehavior(form,"click") {});

        form.add(nome,cpf,telefone, email, rg, dataNascimento, inputAtivo,btnSave);



    }
}

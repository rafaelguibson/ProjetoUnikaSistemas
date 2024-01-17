package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import wicket.entities.Monitorador;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;

public class EditarPF extends Panel implements Serializable {

    FeedbackPanel fp;
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");

    public EditarPF(String id, Monitorador monitorador) {
        super(id);

        setOutputMarkupId(true);

        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        CompoundPropertyModel<Monitorador> model = new CompoundPropertyModel<>(monitorador);
        Form<Monitorador> form = new Form<>("form", model);
        form.setOutputMarkupId(true);
        add(form);

        form.add(new TextField<>("nome"));
        form.add(new TextField<>("cpf"));
        form.add(new TextField<>("telefone"));
        form.add(new TextField<>("email"));
        form.add(new TextField<>("rg"));
        form.add(new DateTextField("dataNascimento", "yyyy-MM-dd"));

        AjaxButton saveButton = new AjaxButton("saveMonitorador", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                // Aqui, o objeto monitorador já está atualizado com os dados do formulário
                System.out.println("Nome: " + monitorador.getNome());
                System.out.println("CPF: " + monitorador.getCpf());

                monitoradorHttpClient.alterar(monitorador);
                target.add(form); // Atualizar o formulário
                showInfo(target, "Monitorador atualizado com sucesso!");
                ModalWindow.closeCurrent(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                // Em caso de erro de validação do formulário
                target.add(fp); // Atualizar o painel de feedback
            }
        };

        form.add(saveButton);

        AjaxLink<Void> cancelButton = new AjaxLink<Void>("cancelButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ModalWindow.closeCurrent(target);
            }
        };
        form.add(cancelButton);
    }

    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }
}

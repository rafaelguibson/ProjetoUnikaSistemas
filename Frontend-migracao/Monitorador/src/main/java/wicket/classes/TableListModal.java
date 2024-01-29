package wicket.classes;
import com.amazonaws.services.dynamodbv2.xspec.M;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import wicket.entities.Monitorador;
import wicket.enums.TipoPessoa;
import wicket.http.MonitoradorHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableListModal extends Panel {

    private static final long serialVersionUID = 1L;

    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList = new ArrayList<>();
    FeedbackPanel fp;
    public TableListModal(String id, List<Monitorador> ExcelMonitoradorList) {
        super(id);

        WebMarkupContainer tableSection = new WebMarkupContainer("tableSection");
        tableSection.setOutputMarkupId(true);
        tableSection.setVisible(true);
        add(tableSection);

        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);




        ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", ExcelMonitoradorList) {

            @Override
            protected void populateItem(ListItem<Monitorador> item) {
                Monitorador monitorador = item.getModelObject();

                // Coluna do chebox para selecionar os monitoradores para deletar
                item.add(new CheckBox("selected", new PropertyModel<>(item.getModel(), "selected")));

                item.add(new Label("id", new PropertyModel<String>(item.getModel(), "id")));

                String tipoPessoa = monitorador.getTipoPessoa().equals(TipoPessoa.PF) ? "Física" : "Jurídica";
                item.add(new Label("tipoPessoa", tipoPessoa));

                // Combina Nome e Razão Social
                String nomeOuRazaoSocial = (monitorador.getTipoPessoa() == TipoPessoa.PF) ? monitorador.getNome() : monitorador.getRazaoSocial();
                item.add(new Label("nomeOuRazaoSocial", nomeOuRazaoSocial));

                // Combina CPF e CNPJ
                String cpfOuCnpj = (monitorador.getTipoPessoa() == TipoPessoa.PF) ? monitorador.getCpf() : monitorador.getCnpj();
                item.add(new Label("cpfOuCnpj", cpfOuCnpj));

                item.add(new Label("telefone", new PropertyModel<String>(item.getModel(), "telefone")));
                item.add(new Label("email", new PropertyModel<String>(item.getModel(), "email")));
                // Combina RG e Inscrição Estadual
                String rgOuInscricaoEstadual = (monitorador.getTipoPessoa() == TipoPessoa.PF) ? monitorador.getRg() : monitorador.getInscricaoEstadual();
                item.add(new Label("rgOuInscricaoEstadual", rgOuInscricaoEstadual));

                item.add(new Label("dataNascimento", new PropertyModel<String>(item.getModel(), "dataNascimento")));

                String status = monitorador.getStatus().toString();
                item.add(new Label("status", status));
            }
        };
        monitoradorList.setOutputMarkupId(true);
        monitoradorList.setReuseItems(true);

        AjaxLink<Void> btnSelectAll = new AjaxLink<Void>("btnSelectAll") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (Monitorador monitorador : ExcelMonitoradorList) {
                    if(!monitorador.isSelected()) {
                        monitorador.setSelected(true);
                        target.add(tableSection);
                    } else {
                        monitorador.setSelected(false);
                        target.add(tableSection);
                    }
                }
            }
        };
//        btnSelectAll.add(new AjaxFormSubmitBehavior("click") {});
        btnSelectAll.setOutputMarkupId(true);
        tableSection.add(monitoradorList, btnSelectAll);


        AjaxLink<Void> btnClose = new AjaxLink<Void>("btnClose") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ModalWindow.closeCurrent(target);
                target.add(tableSection);
            }
        };
//        btnClose.add(new AjaxFormSubmitBehavior(form,"click") {});
        btnClose.setOutputMarkupId(true);
        tableSection.add(btnClose);

        AjaxLink<Void> btnDelete = new AjaxLink<Void>("btnDelete") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<Monitorador> monitoradorRemove = mntList.stream().
                        filter(Monitorador::isSelected).collect(Collectors.toList());

                if(monitoradorRemove.isEmpty()) {
                    info("Selecione algum registro para remover...");
                } else {
                    info( "Foram removidos ("+monitoradorRemove.size()+") monitoradores...");
                }
                target.add(tableSection);
            }
        };
//        btnDelete.add(new AjaxFormSubmitBehavior(form,"click") {});
        btnDelete.setOutputMarkupId(true);
        tableSection.add(btnDelete);


        AjaxLink<Void> btnSave = new AjaxLink<Void>("btnSave") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };
//        btnUpload.add(new AjaxFormSubmitBehavior(form,"click") {});
        btnDelete.setOutputMarkupId(true);
        tableSection.add(btnSave);




    }
}
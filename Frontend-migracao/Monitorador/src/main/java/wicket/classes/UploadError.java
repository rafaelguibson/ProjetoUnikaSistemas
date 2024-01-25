package wicket.classes;
import com.amazonaws.services.dynamodbv2.xspec.M;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
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
    public TableListModal(String id, List<Monitorador> ExcelMonitoradorList, boolean load) {
        super(id);

        WebMarkupContainer tableSection = new WebMarkupContainer("tableSection");
        tableSection.setOutputMarkupId(true);
        tableSection.setVisible(load);
        add(tableSection);

        Form<Void> form = new Form<>("form");
        form.setOutputMarkupId(true);
        tableSection.add(form);

        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        tableSection.add(fp);

        ListView<Monitorador> monitoradorList = getComponents(ExcelMonitoradorList);
        form.add(monitoradorList);


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

        AjaxLink<Void> btnSave = new AjaxLink<Void>("btnSave") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    monitoradorHttpClient.saveAll(ExcelMonitoradorList);
                    fp.info("Lista Cadastrada com sucesso !");
                } catch (Exception e) {
                    fp.info(e.getMessage());
                    target.add(tableSection);
                }

            }
        };

        tableSection.add(btnSave);

    }

    private static ListView<Monitorador> getComponents(List<Monitorador> ExcelMonitoradorList) {
        ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", ExcelMonitoradorList) {

            @Override
            protected void populateItem(ListItem<Monitorador> item) {
                Monitorador monitorador = item.getModelObject();


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
        return monitoradorList;
    }
}

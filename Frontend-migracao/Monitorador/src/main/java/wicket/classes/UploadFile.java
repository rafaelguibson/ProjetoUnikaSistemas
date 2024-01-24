package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Bytes;
import wicket.entities.Monitorador;
import wicket.enums.TipoPessoa;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UploadFile  extends Panel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final FileUploadField fileUploadField;
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList = new ArrayList<>();
    private final int itemsPerPage = 5;
    private IModel<Monitorador> monitoradorModel = Model.of(new Monitorador());

    public UploadFile(String id) {
        super(id);
        WebMarkupContainer tableSection = new WebMarkupContainer("tableSection");
        tableSection.setOutputMarkupId(true);
        mntList = monitoradorHttpClient.listarTodos();

        ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", mntList) {

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

                item.add(new AjaxLink<Void>("btnExcluir") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {

                    }
                });
            }

        };

        monitoradorList.setOutputMarkupId(true);
        tableSection.add(monitoradorList);

        tableSection.setOutputMarkupId(true);
        add(tableSection);
        fileUploadField = new FileUploadField("fileUploadField");

        Form<Void> form = new Form<Void>("form"){
            @Override
            protected void onSubmit() {
                super.onSubmit();

                FileUpload fileUpload = fileUploadField.getFileUpload();

                try {
                    File file = new File("src/main/webapp/uploads/" +fileUpload.getClientFileName());
                    fileUpload.writeTo(file);
                    monitoradorHttpClient.uploadFile(file);
                    info("Upload completed!");

                } catch (Exception e) {
                    e.printStackTrace();
                    error("Upload failed!");
                }
            }
        };

        form.setMultiPart(true);
        //set a limit for uploaded file's size
        form.setMaxSize(Bytes.megabytes(100));
        form.add(fileUploadField);
        tableSection.add(new FeedbackPanel("feedbackPanel"));
        tableSection.add(form);

    }
}

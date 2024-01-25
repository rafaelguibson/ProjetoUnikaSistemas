package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
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
import java.util.stream.Collectors;

public class UploadFile  extends Panel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final FileUploadField fileUploadField;
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList = new ArrayList<>();
    FeedbackPanel fp;
    private IModel<Monitorador> monitoradorModel = Model.of(new Monitorador());

    public UploadFile(String id) {
        super(id);
        WebMarkupContainer tableSection = new WebMarkupContainer("tableSection");
        tableSection.setOutputMarkupId(true);

        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

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


            }

        };

        monitoradorList.setOutputMarkupId(true);
        tableSection.add(monitoradorList);

        tableSection.setOutputMarkupId(true);
        add(tableSection);
        fileUploadField = new FileUploadField("fileUploadField");

        Form<Void> form = getComponents();
        form.add(fileUploadField);
        tableSection.add(new FeedbackPanel("feedbackPanel"));
        tableSection.add(form);

        tableSection.add(new AjaxLink<Void>("btnSelectAll") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (Monitorador monitorador : mntList) {
                    if(!monitorador.isSelected()) {
                        monitorador.setSelected(true);
                        target.add(tableSection);
                    } else {
                        monitorador.setSelected(false);
                        target.add(tableSection);
                    }

                }
            }
        });

        AjaxLink<Void> btnClose = new AjaxLink<Void>("btnClose") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ModalWindow.closeCurrent(target);
                target.add(tableSection);
            }
        };
        btnClose.add(new AjaxFormSubmitBehavior(form,"click") {});
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
        btnDelete.add(new AjaxFormSubmitBehavior(form,"click") {});
        btnDelete.setOutputMarkupId(true);
        tableSection.add(btnDelete);


        AjaxLink<Void> btnUpload = new AjaxLink<Void>("btnUpload") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };
        btnUpload.add(new AjaxFormSubmitBehavior(form,"click") {});
        btnDelete.setOutputMarkupId(true);
        form.add(btnUpload);
    }

    private Form<Void> getComponents() {
        Form<Void> form = new Form<Void>("form"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                FileUpload fileUpload = fileUploadField.getFileUpload();

                try {
                    File file = new File("src/main/webapp/uploads/" +fileUpload.getClientFileName());
                    fileUpload.writeTo(file);
                    monitoradorHttpClient.uploadFile(file);
                    mntList.clear();
                    mntList.addAll(monitoradorHttpClient.uploadFile(file));
                    for(Monitorador item: mntList) {
                        System.out.println(item.toString());}
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
        return form;
    }
}

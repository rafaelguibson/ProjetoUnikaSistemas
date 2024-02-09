package wicket.classes;


import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.theme.DefaultTheme;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import wicket.entities.Monitorador;
import wicket.enums.TipoPessoa;
import wicket.http.MonitoradorHttpClient;
import org.apache.wicket.markup.html.link.ExternalLink;


import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class HomePage extends BasePage implements Serializable {
    private static final long serialVersionUID = 1L;
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList;
    List<Monitorador> excelMonitoradorList;
    private ModalWindow modal = new ModalWindow("modal");
    private IModel<Monitorador> monitoradorModel = Model.of(new Monitorador());
    final Class<? extends Page> currentPageClass = this.getPage().getClass();

    FeedbackPanel fp = new FeedbackPanel("feedbackPanel");

    public HomePage(final PageParameters parameters) throws IOException, InterruptedException {
        super(parameters);
        String filePath = parameters.get("filePath").toString("");
        if (!filePath.isEmpty()) {
            File uploadedFile = new File(filePath);
            excelMonitoradorList = monitoradorHttpClient.uploadFile(uploadedFile);

        }


        Monitorador monitorador = new Monitorador();

        //Definições da janela modal de cadastrar pessoa Física
        modal.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
        modal.setInitialHeight(600);
        modal.setInitialWidth(1150);
        modal.setResizable(false);
//      modal.setMarkupId("idDaModalWindow ");
        modal.add(AttributeAppender.append("class", "custom-1"));
        modal.setCssClassName("style");
        modal.showUnloadConfirmation(true);
        modal.add(new DefaultTheme());
        modal.add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if ("true".equals(parameters.get("showTableList").toString(""))) {
                    if(!excelMonitoradorList.isEmpty()) {
                        modal.setInitialHeight(650);
                        modal.setInitialWidth(1150);
                        TableListModal tableListModal = new TableListModal(modal.getContentId(), excelMonitoradorList, true);
                        modal.setContent(tableListModal);
                        modal.setTitle("Lista de Registros Excel");
                        modal.show(target);
                    } else {
                        modal.setInitialHeight(70);
                        modal.setInitialWidth(400);
                        UploadError uploadError = new UploadError(modal.getContentId());
                        modal.setContent(uploadError);
                        modal.setTitle("Operação Cancelada");
                        modal.show(target);
                    }

                }
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                if ("true".equals(parameters.get("showTableList").toString("")) && !modal.isShown()) {
                    response.render(OnDomReadyHeaderItem.forScript(getCallbackScript()));
                }
            }
        });
        modal.setOutputMarkupId(true);
        add(modal);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);
        /* declaração do feedback panel para notificações */


        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        Form<Void> form = new Form<>("form");
        sectionForm.add(form);

        sectionForm.setOutputMarkupId(true);
        form.setDefaultModel(new CompoundPropertyModel<>(monitorador));
        /* Lista que é preenchida com os monitoradores do banco de dados */
        mntList = monitoradorHttpClient.listarTodos();

        PageableListView<Monitorador> monitoradorList = getComponents(sectionForm);

        form.add(monitoradorList);
        form.add(new PagingNavigator("pagingNavigator", monitoradorList));

        /* Método que controla o selecionar/deselecionar do checkbox de excluisão */
        AjaxLink<Void> checkBox = new AjaxLink<>("checkBox", new PropertyModel<>(new CompoundPropertyModel<>(monitorador), "selected")) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (Monitorador monitorador : mntList) {
                    if(!monitorador.isSelected()) {
                        monitorador.setSelected(true);
                        target.add(sectionForm);
                    } else {
                        monitorador.setSelected(false);
                        target.add(sectionForm);
                    }

                }

            }
        };
        form.add(checkBox);

        //Excluir monitorador
        AjaxLink<Void> btnRemove = new AjaxLink<Void>("btnRemove") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<Monitorador> monitoradorRemove = mntList.stream().
                        filter(Monitorador::isSelected).collect(Collectors.toList());

                monitoradorHttpClient.deleteAllMonitoradores(monitoradorRemove);

                mntList.clear();
                mntList.addAll(monitoradorHttpClient.listarTodos());

                if(monitoradorRemove.isEmpty()) {
                    showInfo(target, "Selecione algum registro para remover...");
                } else {
                    showInfo(target, "Foram removidos ("+monitoradorRemove.size()+") monitoradores...");
                }
                target.add(sectionForm);
            }
        };
        btnRemove.add(new AjaxFormSubmitBehavior(form, "click") {});
        add(btnRemove);


        AjaxLink<Void> btnUpload = new AjaxLink<Void>("btnUpload") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                UploadFile uploadFile = new UploadFile(modal.getContentId());
                modal.setTitle("Upload de Arquivo");
                modal.setInitialHeight(160);
                modal.setInitialWidth(500);
                modal.setEscapeModelStrings(true);
                modal.setContent(uploadFile);
                modal.show(target);
//                modal.setWindowClosedCallback();
            }
        };

        btnUpload.add(new AjaxFormSubmitBehavior(form, "click") {});
        btnUpload.setOutputMarkupId(true);
        add(btnUpload);


        // Adicione o link de download na sua página
        ExternalLink btnExcel = new ExternalLink("btnExcel", "http://localhost:8080/api/monitoradores/export/excel");
        ExternalLink btnPDF = new ExternalLink("btnPDF", "http://localhost:8080/api/monitoradores/export/pdf");
        add(btnExcel, btnPDF);


    } //Fora do construtor

    private PageableListView<Monitorador> getComponents(WebMarkupContainer sectionForm) {
        final int itemsPerPage = 5;

        PageableListView<Monitorador> monitoradorList = new PageableListView<Monitorador>("monitoradorList", mntList, itemsPerPage) {
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

                item.add(new AjaxLink<Void>("btnEditarPF") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        Monitorador monitorador = item.getModelObject();
                        monitoradorModel.setObject(monitorador); // Armazena o Monitorador da linha clicada no modelo
                        EditarPF editarPF = new EditarPF(modal.getContentId(), monitorador);
                        modal.setTitle("Editar Monitorador Pessoa Física");
                        modal.setEscapeModelStrings(true);
                        modal.setContent(editarPF);
                        modal.show(target);
                        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
                            @Override
                            public void onClose(AjaxRequestTarget target) {
                                // Lógica para atualizar a lista de monitoradores após a edição
                                mntList.clear();
                                mntList.addAll(monitoradorHttpClient.listarTodos());
                                target.add(sectionForm);
                            }
                        });
                    }

                });
            }
        };
        return monitoradorList;
    }

    //Metodos externos
    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }


}

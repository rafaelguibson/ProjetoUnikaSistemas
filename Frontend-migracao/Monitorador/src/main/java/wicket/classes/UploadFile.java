package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import wicket.entities.Monitorador;
import wicket.http.MonitoradorHttpClient;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UploadFile extends Panel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final FileUploadField fileUploadField;
    private MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    private List<Monitorador> mntList = new ArrayList<>();
    private FeedbackPanel fp;
    private IModel<Monitorador> monitoradorModel = Model.of(new Monitorador());

    public UploadFile(String id) {
        super(id);

        fileUploadField = new FileUploadField("fileUploadField");

        Form<Void> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                FileUpload fileUpload = fileUploadField.getFileUpload();

                if (fileUpload != null) {
                    // Verifica se o arquivo é um arquivo Excel válido
                    if (!isExcelFile(fileUpload)) {
                        error("O arquivo selecionado não é um arquivo Excel válido. Por favor, selecione um arquivo .xlsx ou .xls.");
                        return; // Interrompe a execução do método onSubmit
                    }
                    long maxSize = Bytes.megabytes(30).bytes();
                    // Verifica se o tamanho do arquivo é maior que o máximo permitido
                    if (fileUpload.getSize() > maxSize) {
                        error("O arquivo é muito grande. O tamanho máximo permitido é de 30 MB.");
                        return; // Interrompe a execução do método onSubmit
                    }
                    try {
                        File file = new File("src/main/webapp/uploads/" + fileUpload.getClientFileName());
                        fileUpload.writeTo(file);
                        monitoradorHttpClient.uploadFile(file);
                        mntList.clear();
                        mntList.addAll(monitoradorHttpClient.uploadFile(file));
                        info("Upload completed!");

                        // Criando e configurando os parâmetros da página
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.add("showTableList", "true"); // Adicionando o parâmetro existente
                        pageParameters.add("filePath", file.getAbsolutePath()); // Adicionando o novo parâmetro

                        setResponsePage(HomePage.class, pageParameters);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error("Upload failed!");
                    }
                } else {
                    error("Nenhum arquivo selecionado.");
                }

            }
        };

        form.setMultiPart(true);
        //set a limit for uploaded file's size
        form.setMaxSize(Bytes.megabytes(100));
        form.add(fileUploadField);

        // Adicione o FeedbackPanel ao formulário (dentro do modal)
        fp = new FeedbackPanel("feedbackPanel");
        form.add(fp);

        add(form);

        AjaxButton btnClose = new AjaxButton("btnClose") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                setResponsePage(HomePage.class, new PageParameters().add("showTableList", "false"));
            }
        };
        form.add(btnClose);
    }

    private boolean isExcelFile(FileUpload fileUpload) {
        if (fileUpload != null) {
            String fileName = fileUpload.getClientFileName();
            return fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"));
        }
        return false;
    }
}

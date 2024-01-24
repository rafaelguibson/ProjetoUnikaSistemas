package wicket.classes;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Bytes;

import java.io.Serializable;

public class UploadFile  extends Panel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final FileUploadField fileUploadField;

    public UploadFile(String id) {
        super(id);
        WebMarkupContainer tableSection = new WebMarkupContainer("tableSection");
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

                    fileUpload.writeTo(file);
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

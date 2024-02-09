package wicket.classes;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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

public class UploadError extends Panel {

    private static final long serialVersionUID = 1L;

    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList = new ArrayList<>();
    FeedbackPanel fp;
    public UploadError(String id) {
        super(id);

        add(new AjaxLink<Void>("btnClose") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                ModalWindow.closeCurrent(target);
                setResponsePage(HomePage.class);
            }
        });
    }
}

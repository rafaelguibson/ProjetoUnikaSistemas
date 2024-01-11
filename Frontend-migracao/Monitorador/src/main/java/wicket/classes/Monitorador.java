package wicket.classes;

import org.apache.wicket.markup.html.WebMarkupContainer;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;
import java.util.List;

public class Monitorador extends BasePage implements Serializable {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList;

    public Monitorador() {
        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");


    }
}

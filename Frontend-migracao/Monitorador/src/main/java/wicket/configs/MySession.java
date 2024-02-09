package wicket.configs;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

@Setter
@Getter
public class MySession extends WebSession {
    private String filePath;

    public MySession(Request request) {
        super(request);
    }

}

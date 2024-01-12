package wicket;

import org.apache.wicket.core.util.resource.ClassPathResourceFinder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.resource.IResourceStream;
import com.google.common.collect.Lists;
import wicket.classes.HomePage;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see wicket.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		//Desabilita o Bloqueio de segurança CSP Block
		getCspSettings().blocking().disabled();

		getResourceSettings().setResourceFinders(Lists.newArrayList(new ClassPathResourceFinder("") {
			@Override
			public IResourceStream find(Class<?> clazz, String path) {
				// Modificar o caminho se ele começar com 'wicket/classes/'
				if (path.startsWith("wicket/classes/")) {
					// Redirecionar a busca para 'wicket/html/'
					return super.find(clazz, path.replace("wicket/classes/", "wicket/html/"));
				}

				// Para outros caminhos, usar a busca padrão
				return super.find(clazz, path);
			}
		}, new ClassPathResourceFinder("")));
	}
}

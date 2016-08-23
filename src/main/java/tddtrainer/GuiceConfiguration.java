package tddtrainer;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import tddtrainer.catalog.CatalogDatasourceIF;
import tddtrainer.catalog.XMLCatalogDatasource;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.gui.EditorViewController;
import tddtrainer.gui.RootLayoutController;
import tddtrainer.gui.catalog.ExerciseSelector;
import tddtrainer.logic.PhaseManager;
import tddtrainer.tracking.TrackingManager;

public class GuiceConfiguration extends AbstractModule {

	private final EventBus bus = new EventBus();

	private final Logger logger = LoggerFactory.getLogger(GuiceConfiguration.class);

	private ResourceBundle bundle;

	public GuiceConfiguration() {
		bus.register(this);
	}

	@Override
	protected void configure() {
		logger.trace("Configuring Dependency Injection");
		bind(EditorViewController.class);
		bind(RootLayoutController.class);
		bind(EventBus.class).toInstance(bus);
		bind(PhaseManager.class);
		bind(TrackingManager.class);
		bind(ExerciseSelector.class).asEagerSingleton();
		bind(CatalogDatasourceIF.class).to(XMLCatalogDatasource.class);
		logger.trace("Configuring Dependency Injection completed");
	}

	@Provides
	public FXMLLoader provideLoader(final Injector injector, GuiceBuilderFactory builderFactory,
			ResourceBundle bundle) {

		Callback<Class<?>, Object> guiceControllerFactory = type -> injector.getInstance(type);

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setBuilderFactory(builderFactory);
		fxmlLoader.setControllerFactory(guiceControllerFactory);
		fxmlLoader.setResources(bundle);
		return fxmlLoader;
	}

	@Subscribe
	public void changeLanguage(LanguageChangeEvent event) {
		bundle = event.getBundle();
	}

	@Provides
	public synchronized ResourceBundle getBundle() {
		if (bundle == null) {
			Locale locale = new Locale("en", "EN");
			bundle = ResourceBundle.getBundle("bundles.tddt", locale);
		}
		return bundle;
	}
}

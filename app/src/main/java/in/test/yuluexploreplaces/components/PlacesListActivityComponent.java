package in.test.yuluexploreplaces.components;

import dagger.Component;
import in.test.yuluexploreplaces.activities.PlacesListActivity;
import in.test.yuluexploreplaces.providers.APIModule;
import in.test.yuluexploreplaces.providers.PlacesListActivityModule;

@Component(modules = {APIModule.class, PlacesListActivityModule.class})
public interface PlacesListActivityComponent {

    void injectPlacesListActivity(PlacesListActivity activity);
}

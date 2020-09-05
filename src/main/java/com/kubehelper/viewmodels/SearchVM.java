package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.DashboardModel;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Vlayout;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class SearchVM implements EventListener {

    private static Logger logger = LoggerFactory.getLogger(SearchVM.class);

    private String selectedNamespace = "all";
    private String searchString = "";
    private boolean caseSensitiveSearch = false;
    private Set<Resource> selectedResources = new HashSet<>() {{
        add(Resource.CONFIG_MAP);
        add(Resource.POD);
        add(Resource.SERVICE);
        add(Resource.DEPLOYMENT);
        add(Resource.STATEFUL_SET);
        add(Resource.REPLICA_SET);
        add(Resource.ENV_VARIABLE);
    }};

    private SearchModel searchModel;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private SearchService searchService;

    @Init
    @NotifyChange("*")
    public void init() {
        searchModel = (SearchModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.SEARCH_MODEL, (k) -> Global.NEW_MODELS.get(Global.SEARCH_MODEL));
        searchModel.setNamespaces(commonService.getAllNamespaces());
    }

    /**
     * Creates CheckBox components Dynamically after UI render.
     */
    @AfterCompose
    public void afterCompose() {
        createKubeResourcesCheckboxes();
    }


    @Command
    public void search() {
        searchModel.getSearchResults().clear();
        searchService.search(selectedNamespace, searchString, searchModel);
    }

    /**
     * Select all resources command. For mark or unmark all kubernetes resources with one kubeResourcesGBoxCheckAll CheckBox.
     *
     * @param component - kubeResourcesGBoxCheckAll Checkbox itself.
     */
    @Command
    @NotifyChange("kubeResources")
    public void selectAllResources(@ContextParam(ContextType.COMPONENT) Component component) {
        boolean isResourcesCheckBoxChecked = ((Checkbox) component).isChecked();
        selectedResources = isResourcesCheckBoxChecked ? EnumSet.allOf(Resource.class) : new HashSet<>();
        Vlayout checkboxesVLayout = (Vlayout) Path.getComponent("//indexPage/templateInclude/kubeResources");
        for (int i = 0; i < checkboxesVLayout.getChildren().size(); i++) {
            checkboxesVLayout.getChildren().get(i).getChildren().forEach(cBox -> {
                ((Checkbox) cBox).setChecked(isResourcesCheckBoxChecked);
            });
        }
    }

    /**
     * Create dynamically CheckBoxes for all kubernetes resources. 10 per Hlayout.
     */
    private void createKubeResourcesCheckboxes() {
        List<Resource> resources = Arrays.asList(Resource.values());
        Hlayout hlayout = null;
        Vlayout checkboxesVLayout = (Vlayout) Path.getComponent("//indexPage/templateInclude/kubeResources");
        for (int i = 0; i < resources.size(); i++) {
            Resource resource = resources.get(i);

            if (i % 10 == 0) {
                hlayout = new Hlayout();
                hlayout.setValign("middle");
            }
            Checkbox resourceCheckbox = new Checkbox(Resource.getValueByKey(resource.name()));
            resourceCheckbox.setId(resource.name() + "_Checkbox");
            resourceCheckbox.setStyle("padding: 5px;");
            resourceCheckbox.addEventListener("onCheck", this);
            resourceCheckbox.setChecked(selectedResources.contains(resource));
            hlayout.appendChild(resourceCheckbox);
            checkboxesVLayout.appendChild(hlayout);
        }
    }

    /**
     * Kubernetes Resources CheckBoxes Events handling.
     *
     * @param event - onCheck event.
     */
    @Override
    public void onEvent(Event event) {

        //Add or remove selected resource to selectedResources model.
        String resourceId = ((Checkbox) event.getTarget()).getId();
        String resourceName = resourceId.substring(0, resourceId.lastIndexOf("_"));
        if (selectedResources.contains(Resource.valueOf(resourceName))) {
            selectedResources.remove(Resource.valueOf(resourceName));
        } else {
            selectedResources.add(Resource.valueOf(resourceName));
        }

        //Set checked kubeResourcesGBoxCheckAll CheckBox if at least one resource selected.
        Checkbox kubeResourcesCheckAll = (Checkbox) Path.getComponent("//indexPage/templateInclude/kubeResourcesGBoxCheckAll");
        if (!kubeResourcesCheckAll.isChecked() && !selectedResources.isEmpty()) {
            kubeResourcesCheckAll.setChecked(true);
            BindUtils.postNotifyChange(null, null, this, "kubeResourcesGBoxCheckAll");
        }
    }

    public SearchModel getModel() {
        return searchModel;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public SearchVM setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public String getSearchString() {
        return searchString;
    }

    public SearchVM setSearchString(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public boolean isCaseSensitiveSearch() {
        return caseSensitiveSearch;
    }

    public SearchVM setCaseSensitiveSearch(boolean caseSensitiveSearch) {
        this.caseSensitiveSearch = caseSensitiveSearch;
        return this;
    }
}

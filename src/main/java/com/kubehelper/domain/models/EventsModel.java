package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.domain.filters.EventsFilter;
import com.kubehelper.domain.filters.IpsAndPortsFilter;
import com.kubehelper.domain.results.EventResult;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class EventsModel implements PageModel {

    private String templateUrl = "~./zul/pages/events.zul";

    public static String NAME = Global.EVENTS_MODEL;
    private int desktopWidth;
    private int desktopHeight;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<EventResult> eventsResult = new ListModelList<>();
    private EventsFilter filter = new EventsFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();


    public void addEventResult(EventResult eventResult) {
        eventsResult.add(eventResult);
    }

    public EventsModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    @Override
    public void setDesktopWithAndHeight(int width, int height) {
        this.desktopWidth = width;
        this.desktopHeight = height;
    }

    public void addSearchException(Exception exception) {
        this.searchExceptions.add(new KubeHelperException(exception));
    }

    @Override
    public String getName() {
        return NAME;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public int getDesktopWidth() {
        return desktopWidth;
    }

    @Override
    public int getDesktopHeight() {
        return desktopHeight;
    }

    public ListModelList<EventResult> getEventsResult() {
        return eventsResult;
    }

    public EventsModel setEventsResult(ListModelList<EventResult> eventsResult) {
        this.eventsResult = eventsResult;
        return this;
    }

    public EventsFilter getFilter() {
        return filter;
    }

    public EventsModel setFilter(EventsFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public EventsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public List<KubeHelperException> getSearchExceptions() {
        return searchExceptions;
    }

    public EventsModel setSearchExceptions(List<KubeHelperException> searchExceptions) {
        this.searchExceptions = searchExceptions;
        return this;
    }

    public boolean hasSearchErrors() {
        return !searchExceptions.isEmpty();
    }
}

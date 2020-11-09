package com.kubehelper.services;

import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.EventsModel;
import com.kubehelper.domain.results.EventResult;
import io.kubernetes.client.openapi.models.V1Event;
import io.kubernetes.client.openapi.models.V1EventList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.kubehelper.common.Resource.EVENT;

/**
 * Events service.
 *
 * @author JDev
 */
@Service
public class EventsService {

    private static Logger logger = LoggerFactory.getLogger(EventsService.class);

    @Autowired
    private KubeAPI kubeAPI;

    /**
     * Searches for events by selected namespace.
     *
     * @param eventsModel - search model
     */
    public void search(EventsModel eventsModel) {

        eventsModel.getSearchResults().clear();
        eventsModel.getSearchExceptions().clear();

        try {
            V1EventList v1EventList = kubeAPI.getV1EventList(eventsModel.getSelectedNamespace());
            for (V1Event event : v1EventList.getItems()) {
                addSearchResultToModel(event.getMetadata(), eventsModel, EVENT, event.getMetadata().getName(), "");
            }
        } catch (RuntimeException e) {
            eventsModel.addSearchException(e);
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Add new found variable/text/string to search result.
     *
     * @param metadata       - kubernetes resource/object metadata
     * @param eventsModel    - search model
     * @param resource       - kubernetes @{@link Resource}
     * @param resourceName   - resource name
     * @param additionalInfo - additional info
     */
    private void addSearchResultToModel(V1ObjectMeta metadata, EventsModel eventsModel, Resource resource, String resourceName, String additionalInfo) {
        EventResult newSearchResult = new EventResult(eventsModel.getSearchResults().size() + 1)
                .setNamespace(metadata.getNamespace() == null ? "N/A" : metadata.getNamespace())
                .setResourceType(resource)
                .setResourceName(resourceName)
                .setAdditionalInfo(additionalInfo)
                .setCreationTime(getParsedCreationTime(metadata.getCreationTimestamp()));
        eventsModel.addSearchResult(newSearchResult)
                .addResourceNameFilter(metadata.getName());
    }


    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

}

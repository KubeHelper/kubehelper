/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.viewmodels.popups;

import com.kubehelper.common.Resource;
import com.kubehelper.services.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

import static com.kubehelper.common.Resource.KUBE_HELPER_CONTAINER_SECURITY_CONTEXT;
import static com.kubehelper.common.Resource.KUBE_HELPER_CUSTOM;
import static com.kubehelper.common.Resource.KUBE_HELPER_POD_SECURITY_CONTEXT;

/**
 * Class for displaying resource raw sources in java/yaml/json formats.
 * ViewModel initializes ..kubehelper/components/raw-resource.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class RawResourceVM implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(RawResourceVM.class);

    private int fontSize = 14;

    private int mainWindowHeight = 900;

    private Resource resource;
    private String title;
    private String name;
    private String namespace;
    private String raw;
    private String yaml;
    private String json;

    @WireVariable
    private CommonService commonService;

    @Wire
    private Tabbox rawResourceTabBox;

    @Wire
    private Slider fontSizeSlider;

    /**
     * Initializes popup window.
     *
     * @param resource   - resource to show.
     * @param name       - resource name.
     * @param namespace  - namespace.
     * @param title      - resource title.
     * @param rawContent - resource java.toString() content.
     */
    @Init
    public void init(@ExecutionArgParam("resource") Resource resource, @ExecutionArgParam("name") String name, @ExecutionArgParam("namespace") String namespace,
                     @ExecutionArgParam("title") String title, @ExecutionArgParam("content") String rawContent) {
        this.resource = resource;
        this.name = name;
        this.namespace = namespace;
        this.title = title;
        this.raw = rawContent;

        logger.debug(String.format("Init(): resource=%s, name=%s, namespace=%s, title=%s", resource, name, namespace, title));
    }

    /**
     * Adds {@link org.zkoss.zk.ui.event.SelectEvent} Listener to @rawResourceTabBox.
     * Adds {@link org.zkoss.zk.ui.event.ScrollEvent} Listener to @fontSizeSlider.
     * Highlights init java(raw)  block.
     *
     * @param view - {@link org.zkoss.zul.Window} component.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        rawResourceTabBox.addEventListener("onSelect", this);
        fontSizeSlider.addEventListener("onScroll", this);

        highlightBlock("rawJavaId", "java", this.raw);
    }

    /**
     * Handles {@link org.zkoss.zk.ui.event.SelectEvent} for @rawResourceTabBox.
     * Handles {@link org.zkoss.zk.ui.event.ScrollEvent} for @fontSizeSlider.
     *
     * @param event - {@link Event}
     */
    @Override
    public void onEvent(Event event) {
        if ("onScroll".equals(event.getName())) {
            Slider fontSlider = (Slider) event.getTarget();
            fontSize = fontSlider.getCurpos();
            BindUtils.postNotifyChange(this, "tabPanelFontSize");
        } else if ("onSelect".equals(event.getName())) {
            Tab tab = (Tab) event.getTarget();
            switch (tab.getId()) {
                case "yaml" -> parseResourceToYaml();
                case "json" -> parseResourceToJson();
            }
        }
    }

    /**
     * Parses Kubernetes @{@link Resource} to yaml.
     * Shows warning if parsing was not successfully.
     * Highlights div block with yaml resource.
     */
    private void parseResourceToYaml() {
        if (isCustomResource("yaml")) return;

        if (StringUtils.isBlank(this.yaml)) {
            this.yaml = commonService.getYamlResource(resource, name);

            if (isResourceEmpty(this.yaml, "yaml")) return;

            highlightBlock("rawYamlId", "yaml", this.yaml);
        }
    }

    /**
     * Parses Kubernetes @{@link Resource} to json.
     * Shows warning if parsing was not successfully.
     * Highlights div block with json resource.
     */
    private void parseResourceToJson() {
        if (isCustomResource("json")) return;

        if (StringUtils.isBlank(this.json)) {
            this.json = commonService.getJsonResource(resource, name, namespace);

            if (isResourceEmpty(this.json, "json")) return;

            highlightBlock("rawJsonId", "json", this.json);
        }
    }

    /**
     * Checks if Resource is a kubernetes resource has metadata and can be converted to yaml or json.
     *
     * @param block - message suffix json or yaml string.
     * @return - true if resource is custom and cannot be converted to kubernetes resource.
     */
    private boolean isCustomResource(String block) {
        if (resource == KUBE_HELPER_CUSTOM || resource == KUBE_HELPER_CONTAINER_SECURITY_CONTEXT || resource == KUBE_HELPER_POD_SECURITY_CONTEXT) {
            Notification.show(String.format("This ist Kube Helper Custom Object which does not support convert to %s.", block), "warning", null, "top_center", 4000);
            return true;
        }
        return false;
    }

    /**
     * Shows an warning if the resource has not been converted ({@param content} is empty) to yaml or json.
     *
     * @param content - resource content.
     * @param block   - message suffix json or yaml string.
     * @return - true if {@param content} is empty
     */
    private boolean isResourceEmpty(String content, String block) {
        if (StringUtils.isBlank(content)) {
            Notification.show(String.format("This art of Kubernetes object cannot be converted to %s format.", block), "warning", null, "top_center", 4000);
            return true;
        }
        logger.debug(String.format("Resource %s name=%s, namespace=%s was successfully parsed to %s.", resource, name, namespace, block));
        return false;
    }

    /**
     * Highlights div block with resource content.
     *
     * @param blockId    - div id in ..kubehelper/components/raw-resource.zul
     * @param blockClass - div highlight class in ..kubehelper/components/raw-resource.zul
     * @param content    - highlight content.
     */
    private void highlightBlock(String blockId, String blockClass, String content) {
        Div highlightBlock = (Div) Path.getComponent("/rawResourceWindowID/" + blockId);
        highlightBlock.appendChild(new Html("<pre><code class=" + blockClass + ">" + content + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
        Clients.evalJavaScript(String.format("highlightBlock('%s');", blockClass));
    }

    public String getTitle() {
        return title;
    }

    public String getMainWindowHeight() {
        return mainWindowHeight + "px";
    }

    public String getTabPanelFontSize() {
        return String.format("font-size: %spx;", fontSize);
    }

    public int getFontSize() {
        return fontSize;
    }

}
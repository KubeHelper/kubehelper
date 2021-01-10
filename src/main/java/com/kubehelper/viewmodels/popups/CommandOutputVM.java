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

import com.kubehelper.services.CommonService;
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
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Slider;

/**
 * Class for displaying resource raw sources in java/yaml/json formats.
 * ViewModel initializes ..kubehelper/components/raw-resource.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class CommandOutputVM implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(CommandOutputVM.class);

    private final String fontSizeCss = "font-size: %spx;";
    private int fontSize = 14;

    private int mainWindowHeight = 900;

    private String title;
    private String command;
    private String commandOutput;

    @WireVariable
    private CommonService commonService;

    @Wire
    private Slider fontSizeSlider;

    /**
     * Initializes popup window.
     *
     * @param command       - executed command.
     * @param commandOutput - executed command output.
     * @param title         - resource title.
     */
    @Init
    public void init(@ExecutionArgParam("command") String command, @ExecutionArgParam("commandOutput") String commandOutput, @ExecutionArgParam("title") String title) {
        this.command = command;
        this.commandOutput = commandOutput;
        this.title = title;

        logger.debug(String.format("Init(): command=%s, title=%s", command, title));
    }

    /**
     * Adds {@link org.zkoss.zk.ui.event.ScrollEvent} Listener to @fontSizeSlider.
     * Highlights commandOutput block.
     *
     * @param view - {@link org.zkoss.zul.Window} component.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        fontSizeSlider.addEventListener("onScroll", this);
        highlightBlock();
    }

    /**
     * Handles {@link org.zkoss.zk.ui.event.ScrollEvent} for @fontSizeSlider.
     *
     * @param event - {@link Event}
     */
    @Override
    public void onEvent(Event event) {
        Slider fontSlider = (Slider) event.getTarget();
        fontSize = fontSlider.getCurpos();
        BindUtils.postNotifyChange(null, null, this, "fontSizeCss");
    }

    /**
     * Highlights div block with command output content.
     */
    private void highlightBlock() {
        Div highlightBlock = (Div) Path.getComponent("/commandOutputWindowID/commandOutputWindowId");
        highlightBlock.appendChild(new Html("<pre><code>" + commandOutput + "</code></pre>"));
        BindUtils.postNotifyChange(null, null, this, ".");
//        Clients.evalJavaScript("highlightBlock();");
    }

    public String getTitle() {
        return title;
    }

    public String getMainWindowHeight() {
        return mainWindowHeight + "px";
    }

    public String getCommand() {
        return command;
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getFontSizeCss() {
        return String.format(fontSizeCss, fontSize);
    }

}
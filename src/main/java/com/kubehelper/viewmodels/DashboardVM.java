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
package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.domain.models.DashboardModel;
import com.kubehelper.domain.results.ClusterResult;
import com.kubehelper.domain.results.NodeResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.DashboardService;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Vlayout;

import java.util.Map;

/**
 * Class for displaying Kube Helper dashboard Cluster and nodes metrics.
 * ViewModel initializes ..kubehelper/pages/dashboard.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class DashboardVM {

    private int centerLayoutHeight = 700;

    private DashboardModel dashboardModel;

    @WireVariable
    private DashboardService dashboardService;

    @WireVariable
    private CommonService commonService;


    @Init
    public void init() {
        dashboardModel = (DashboardModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.DASHBOARD_MODEL, (k) -> Global.NEW_MODELS.get(Global.DASHBOARD_MODEL));
        dashboardService.showDashboard(dashboardModel);
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        createDashboard();
    }

    @Listen("onAfterSize=#centerLayoutIpsAndPortsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(this, ".");
    }

    public ClusterResult getClusterResult() {
        return dashboardModel.getClusterResult();
    }

    /**
     * Creates dashboard dynamically. Depends of nodes count.
     */
    private void createDashboard() {
        Vlayout mainDashboardVLayout = (Vlayout) Path.getComponent("//indexPage/templateInclude/mainDashboardVLayout");
        Hbox hbox = buildNewHbox();
        for (int i = 0; i < dashboardModel.getNodesResults().size(); i++) {
            NodeResult node = dashboardModel.getNodesResults().get(i);

            hbox.appendChild(createNodePanel(node));
            if (hbox.getChildren().size() == 2 || i == dashboardModel.getNodesResults().size() - 1) {
                if (i % 2 == 0 && i == dashboardModel.getNodesResults().size() - 1) {
                    hbox.appendChild(buildNewCell());
                }
                mainDashboardVLayout.appendChild(hbox);
                hbox = buildNewHbox();
            }
        }
    }

    /**
     * Creates new {@link Hbox}
     *
     * @return - new Hbox.
     */
    private Hbox buildNewHbox() {
        Hbox hbox = new Hbox();
        hbox.setWidth("100%");
        hbox.setSpacing("3px");
        return hbox;
    }

    /**
     * Creates new Cell for Hbox.
     *
     * @return - new Cell.
     */
    private Cell buildNewCell() {
        Cell cell = new Cell();
        cell.setWidth("50%");
        cell.setStyle("box-shadow: 5px 10px 15px #E4F4FF;padding: 5px");
        return cell;
    }

    /**
     * Creates new node panel.
     *
     * @param node - {@link NodeResult}
     * @return - cell with node result.
     */
    private Cell createNodePanel(NodeResult node) {
        Cell cell = buildNewCell();
        Panel panel = buildNewPanel(node.getName(), true);
        Panelchildren panelchildren = new Panelchildren();
        panelchildren.appendChild(createListBox("Metadata", node.getMetaMap(), null));
        panelchildren.appendChild(createListBox("Node Info", node.getNodeInfoMap(), null));
        panelchildren.appendChild(createListBox("Spec", node.getSpecMap(), null));
        panelchildren.appendChild(createListBox("Status", node.getStatusMap(), node.getTotalImagesSize()));
        panel.appendChild(panelchildren);
        cell.appendChild(panel);
        return cell;
    }

    /**
     * Creates Listbox for node.
     *
     * @param name - name.
     * @param listBoxData - listbox Data.
     * @param totalImagesSize - total images size.
     * @return - {@link Listbox} for node Hbox.
     */
    private Listbox createListBox(String name, Map<String, String> listBoxData, String totalImagesSize) {
        Listbox listBox = new Listbox();
        Auxhead auxhead = new Auxhead();
        Auxheader auxheader = new Auxheader();
        auxheader.setColspan(2);
        auxheader.setLabel(name);
        auxhead.appendChild(auxheader);
        Listheader keyHeader = new Listheader();
        keyHeader.setWidth("20%");
        Listheader valueHeader = new Listheader();
        valueHeader.setWidth("80%");
        Listhead listHead = new Listhead();
        listBox.appendChild(auxhead);
        listHead.appendChild(keyHeader);
        listHead.appendChild(valueHeader);
        listBox.appendChild(listHead);

        listBoxData.entrySet().forEach(listEntry -> {
            Listitem listItem = new Listitem();
            Listcell keyCell = new Listcell();
            Listcell valueCell = new Listcell();
            Label valueLabel = new Label();
            valueLabel.setValue(listEntry.getValue());
            valueLabel.setMultiline(true);
            keyCell.setLabel(listEntry.getKey());
            keyCell.setStyle("font-weight: bold;");
            valueCell.appendChild(valueLabel);
            listItem.appendChild(keyCell);

            if ("images".equals(listEntry.getKey())) {
                valueCell.getChildren().clear();
                Panel imagesPanel = buildNewPanel("Images [" + totalImagesSize + "]", false);
                Panelchildren panelchildren = new Panelchildren();
                panelchildren.appendChild(valueLabel);
                imagesPanel.appendChild(panelchildren);
                valueCell.appendChild(imagesPanel);
            }
            listItem.appendChild(valueCell);
            listBox.appendChild(listItem);
        });
        return listBox;
    }

    /**
     * Builds new panel with title and state open/closed.
     *
     * @param title - panel title.
     * @param isOpen - true if panel will be open.
     * @return - {@link Panel}
     */
    private Panel buildNewPanel(String title, boolean isOpen) {
        Panel panel = new Panel();
        panel.setCollapsible(true);
        panel.setTitle(title);
        panel.setOpen(isOpen);
        panel.setBorder("none");
        panel.setWidth("100%");
        return panel;
    }
}
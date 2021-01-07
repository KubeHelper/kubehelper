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
import com.kubehelper.domain.models.VersionsModel;
import com.kubehelper.domain.results.UtilResult;
import com.kubehelper.services.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.ListModelList;

import java.util.List;

/**
 * Class for displaying versions of tools which running in container.
 * ViewModel initializes ..kubehelper/pages/versions.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class VersionsVM {

    private static Logger logger = LoggerFactory.getLogger(VersionsVM.class);
    private int centerLayoutHeight = 700;

    private VersionsModel versionsModel;
    private ListModelList<UtilResult> versionsResults = new ListModelList<>();

    @WireVariable
    private CommonService commonService;

    @Init
    public void init() {
        versionsModel = (VersionsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.VERSIONS_MODEL, (k) -> Global.NEW_MODELS.get(Global.VERSIONS_MODEL));
        onInitPreparations();
    }

    /**
     * For @Wire GUI components and Event Listeners.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
    }

    @Listen("onAfterSize=#versionsPanelId")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 43;
        BindUtils.postNotifyChange(null, null, this, ".");
    }


    /**
     * Gets version for each registered util and save result to @{@link ListModelList}.
     */
    private void onInitPreparations() {
        List<UtilResult> utilsResults = versionsModel.getUtilsResults();
        utilsResults.forEach(util -> {
            util.setVersion(commonService.executeCommand("bash", util.getVersionCheckCommand()));
        });
        versionsResults = new ListModelList<>(utilsResults);
    }


    public ListModelList<UtilResult> getVersionsResults() {
        return versionsResults;
    }

    public String getMainGridHeight() {
        return centerLayoutHeight + "px";
    }

}

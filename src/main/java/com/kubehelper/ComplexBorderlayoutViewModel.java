package com.kubehelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Area;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimplePieModel;

public class ComplexBorderlayoutViewModel {

	private static final String searchUrl = "http://www.zkoss.org/doc/searchresult.jsp?cx=008321236477929467003%3A63kdpeqkkvw&cof=FORID%3A11&q=";

	private String search;
	private List<Contribution> contributions = new ArrayList<Contribution>();
	private PieModel pieModel;

	@Init
	public void init() {
		contributions.add(new Contribution("Europe", 31));
		contributions.add(new Contribution("Africa", 4));
		contributions.add(new Contribution("Americas", 25));
		contributions.add(new Contribution("Oceania", 4));
		contributions.add(new Contribution("Asia", 29));
		contributions.add(new Contribution("Others", 4));

		createPieModel();
	}

	private void createPieModel() {
		pieModel = new SimplePieModel();

		for (Contribution contribution : contributions) {
			pieModel.setValue(contribution.getArea(), contribution.getValue());
		}
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public PieModel getPieModel() {
		return pieModel;
	}

	public List<Contribution> getContributions() {
		return contributions;
	}

	@Command
	public void search() {
		if(StringUtils.isEmpty(search)) {
			Clients.showNotification("Search field must not be empty");
		} else {
			Executions.getCurrent().sendRedirect(searchUrl.concat(search), "_zk");
		}
	}

	@Command
	public void displayArea(@ContextParam(ContextType.TRIGGER_EVENT) MouseEvent event) {
		Component component = event.getAreaComponent();

		if (component instanceof Area) {
			Area area = (Area) component;
			Clients.alert(area.getAttribute("entity") + ":"
					+ area.getTooltiptext());
		}
	}

	@Command
	@NotifyChange("pieModel")
	public void updatePieModel() {
		createPieModel();
	}

	public static class Contribution {
		private String area;
		private int value;

		public Contribution(String country, int value) {
			super();
			this.area = country;
			this.value = value;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	}
}

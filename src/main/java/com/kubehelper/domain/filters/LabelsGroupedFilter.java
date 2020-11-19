package com.kubehelper.domain.filters;

/**
 * @author JDev
 */
public class LabelsGroupedFilter {
    private String name = "";
    private int amount;

    public LabelsGroupedFilter() {
    }

    public String getName() {
        return name;
    }

    public LabelsGroupedFilter setName(String name) {
        this.name = name;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

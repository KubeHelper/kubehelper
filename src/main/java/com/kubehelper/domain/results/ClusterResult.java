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
package com.kubehelper.domain.results;

import org.apache.commons.io.FileUtils;

import java.util.StringJoiner;

/**
 * @author JDev
 */
public class ClusterResult {

    private int totalNodes;
    private int totalCpus;
    private int totalCpusTime;
    private String totalCpusTimeFormat = "";

    private long totalMemory;
    private long totalHdd;

    private int totalPods;
    private int totalAllowedPods;


    public ClusterResult() {
    }

    public void addTotalCpu(String nodeCpus) {
        totalCpus += Integer.parseInt(nodeCpus);
    }

    public void addTotalCpuTime(String nodeCpuTime) {
        totalCpusTime += Integer.parseInt(nodeCpuTime);
    }

    public void addTotalMemory(long nodeMemory) {
        totalMemory += nodeMemory;
    }

    public void addTotalHdd(long size) {
        totalHdd += size;
    }

    public void addTotalAllowedPods(String allowedPods) {
        totalAllowedPods += Integer.parseInt(allowedPods);
    }


    public int getTotalNodes() {
        return totalNodes;
    }

    public ClusterResult setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
        return this;
    }

    public int getTotalCpus() {
        return totalCpus;
    }


    public String getTotalCpusTime() {
        return totalCpusTime + " " + totalCpusTimeFormat;
    }

    public String getTotalCpusTimeFormat() {
        return totalCpusTimeFormat;
    }

    public ClusterResult setTotalCpusTimeFormat(String totalCpusTimeFormat) {
        this.totalCpusTimeFormat = totalCpusTimeFormat;
        return this;
    }

    public String getTotalMemory() {
        return FileUtils.byteCountToDisplaySize(totalMemory);
    }

    public String getTotalHdd() {
        return FileUtils.byteCountToDisplaySize(totalHdd);
    }

    public String getTotalPods() {
        return totalPods + "/" + totalAllowedPods;
    }

    public ClusterResult setTotalPods(int totalPods) {
        this.totalPods = totalPods;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ClusterResult.class.getSimpleName() + "[", "]")
                .add("totalNodes=" + totalNodes)
                .add("totalCpus=" + totalCpus)
                .add("totalCpusTime=" + totalCpusTime)
                .add("totalCpusTimeFormat='" + totalCpusTimeFormat + "'")
                .add("totalMemory=" + totalMemory)
                .add("totalHdd=" + totalHdd)
                .add("totalPods=" + totalPods)
                .add("totalAllowedPods=" + totalAllowedPods)
                .toString();
    }
}

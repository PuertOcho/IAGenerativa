package com.nuka.nuka_server.models;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "admin")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {

    @JsonProperty("_id")
    private String id;
    
    @JsonProperty("idScript")
    private String idScript;
	
    @JsonProperty("Command")
    private String command;

    @JsonProperty("CreatedAt")
    private String createdAt;

    @JsonProperty("ID")
    private String idAux;

    @JsonProperty("Image")
    private String image;

    @JsonProperty("Labels")
    private String labels;

    @JsonProperty("LocalVolumes")
    private String localVolumes;

    @JsonProperty("Mounts")
    private String mounts;

    @JsonProperty("Names")
    private String names;

    @JsonProperty("Networks")
    private String networks;

    @JsonProperty("Ports")
    private String ports;

    @JsonProperty("RunningFor")
    private String runningFor;

    @JsonProperty("Size")
    private String size;

    @JsonProperty("State")
    private String state;

    @JsonProperty("Status")
    private String status;

    // Constructor por defecto
    public Data() {
        super();
    }

    // Constructor con parámetros
    public Data(String command, String createdAt, String id, String image, String labels, String localVolumes, String mounts, String names, String networks, String ports, String runningFor, String size, String state, String status) {
        this.command = command;
        this.createdAt = createdAt;
        this.id = id;
        this.image = image;
        this.labels = labels;
        this.localVolumes = localVolumes;
        this.mounts = mounts;
        this.names = names;
        this.networks = networks;
        this.ports = ports;
        this.runningFor = runningFor;
        this.size = size;
        this.state = state;
        this.status = status;
    }

    // Getters y Setters
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getLocalVolumes() {
        return localVolumes;
    }

    public void setLocalVolumes(String localVolumes) {
        this.localVolumes = localVolumes;
    }

    public String getMounts() {
        return mounts;
    }

    public void setMounts(String mounts) {
        this.mounts = mounts;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getNetworks() {
        return networks;
    }

    public void setNetworks(String networks) {
        this.networks = networks;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String getRunningFor() {
        return runningFor;
    }

    public void setRunningFor(String runningFor) {
        this.runningFor = runningFor;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

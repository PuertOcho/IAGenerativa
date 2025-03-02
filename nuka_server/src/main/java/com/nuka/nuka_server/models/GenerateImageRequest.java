package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateImageRequest {

    @JsonProperty("model")
    public String model;
    
    /* Una descripción de texto de la(s) imagen(es) deseada(s). 
     * La longitud máxima es de 1000 caracteres para dall-e-2 y 4000 caracteres para dall-e-3.*/
    @JsonProperty("prompt")
    public String prompt;
    
    /* dall-e-2 -> 256x256, 512x512, or 1024x1024
     * dall-e-3 -> 1024x1024, 1792x1024, or 1024x1792 */
    @JsonProperty("size")
    public String size;
    
    @JsonProperty("n")
    public int numImages;
    
    // ========== ONLY FOR DALLE 3 ==========
    /*
     * El estilo de las imágenes generadas. Debe ser "vivid" o "natural". 
     * vivid(default): hace que el modelo se incline hacia la generación de imágenes hiperrealistas y dramáticas. 
     * natural: hace que el modelo genere imágenes más naturales y menos hiperrealistas. 
     */
    @JsonProperty("style")
    public String style;
    
    // standard(default) o hd
    @JsonProperty("quality")
    public String quality;
    
    public GenerateImageRequest(String model, String prompt, String size, int numImages) {
		super();
		this.model = model;
		this.prompt = prompt;
		this.size = size;
		this.numImages = numImages;
    }

    public GenerateImageRequest() {
    	super();
    }
    
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getNumImages() {
		return numImages;
	}

	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}
    
    
}
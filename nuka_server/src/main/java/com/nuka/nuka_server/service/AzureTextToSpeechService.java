package com.nuka.nuka_server.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;

import lombok.RequiredArgsConstructor;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AzureTextToSpeechService {

    Logger logger = LoggerFactory.getLogger(getClass());
    
	public String vozPorDefecto = "es-ES-AbrilNeural";
	public List<String> listaVoces = Arrays.asList("es-ES-AbrilNeural", "es-ES-ElviraNeural", "es-ES-AlvaroNeural", "es-ES-ArnauNeural", 
		"es-ES-DarioNeural", "es-ES-EliasNeural", "es-ES-EstrellaNeural", "es-ES-IreneNeural", "es-ES-LaiaNeural", "es-ES-LiaNeural", 
		"es-ES-NilNeural", "es-ES-SaulNeural", "es-ES-TeoNeural", "es-ES-TrianaNeural", "es-ES-VeraNeural");
	
	public String vozStringFormatter = "YYYYY-XXXXXNeural";
	
	@Autowired
	NukaConfiguration nukaConfiguration;
	
	public String getVozPorDefecto() {
		return vozPorDefecto;
	}

	public void setVozPorDefecto(String vozPorDefecto) {
		this.vozPorDefecto = vozPorDefecto;
	}
	
	public Boolean sintetizar(String texto, String wavFilename, String vozChat, String idioma) throws IOException {
	        Boolean res = false;

	        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
	            
	            String MICROSOFT_COG_REGION = nukaConfiguration.getMICROSOFT_COG_REGION();
	            String MICROSOFT_COG_KEY = nukaConfiguration.getMICROSOFT_COG_KEY();
	            
	            String urlTTS = Constantes.MICROSOFT_URL_TTS.replace("MICROSOFT_COG_REGION", MICROSOFT_COG_REGION);
	            
	            HttpPost httpPost = new HttpPost(urlTTS);
	            httpPost.addHeader("Ocp-Apim-Subscription-Key", MICROSOFT_COG_KEY);
	            httpPost.addHeader("Content-Type", "application/ssml+xml");
	            httpPost.addHeader("X-Microsoft-OutputFormat", "audio-24khz-160kbitrate-mono-mp3");
	            
	            String idiomaRespuesta = "es-ES";
	            if (!ObjectUtils.isEmpty(idioma)) { idiomaRespuesta = idioma; }
	            String ssml = ""
	            	+ "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:mstts=\"https://www.w3.org/2001/mstts\" xml:lang=\"IDIOMA\">"
	            	+ "	<voice name=\"VOZ\"><lang xml:lang=\"IDIOMA\"> TEXTO </lang></voice>"
	            	+ "</speak>";

	            for (String c: Constantes.MAP_CARACTERES_ESPECIALES_XML.keySet()) {
	        	texto = texto.replace(c, Constantes.MAP_CARACTERES_ESPECIALES_XML.get(c));
	            }
	            
	            String ssmlFormatted = ssml
	        	.replace("IDIOMA", idiomaRespuesta)
	        	.replace("VOZ", vozStringFormatter).replace("YYYYY", idiomaRespuesta).replace("XXXXX", vozChat)
	        	.replace("TEXTO", texto);
	            
	            httpPost.setEntity(new StringEntity(ssmlFormatted));

	            CloseableHttpResponse response = httpClient.execute(httpPost);
	            HttpEntity entity = response.getEntity();

	            if (entity.getContent() != null) {
	                FileUtils.copyInputStreamToFile(entity.getContent(), new File(Constantes.AUDIO_PATH + wavFilename));
	                res = true;
	            } else {
	        	logger.error("sintetizar : Error entity.getContent() null");
	            }
	        } catch (Exception e) {
	            logger.error(e.getMessage());
	            e.printStackTrace();
		}
	        return res;
	 }

}
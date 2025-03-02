package com.nuka.nuka_server.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;
import com.nuka.nuka_server.models.GrupoOpciones;
import com.nuka.nuka_server.models.NukaChat;
import com.nuka.nuka_server.models.OpcionBasica;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UtilsService {

    Logger logger = LoggerFactory.getLogger(getClass());
    private static String OS = System.getProperty("os.name").toLowerCase();

    @Autowired
    UserRepository userRepository;

    @Autowired
    NukaConfiguration nukaConfiguration;

    public String determineContentType(String filename) {
	int lastIndexOfDot = filename.lastIndexOf('.');
	if (lastIndexOfDot == -1) {
	    return "application/octet-stream"; // Extensión no encontrada, tipo de contenido por defecto
	}
	String extension = filename.substring(lastIndexOfDot + 1).toLowerCase();

	switch (extension) {
	case "mp4":
	    return "video/mp4";
	case "webm":
	    return "video/webm";
	case "mov":
	    return "video/quicktime";
	case "avi":
	    return "video/x-msvideo";
	case "mkv":
	    return "video/x-matroska";
	default:
	    return "application/octet-stream";
	}
    }

    public String generarHash256(String texto) {
	try {
	    // Crear una instancia de MessageDigest con el algoritmo SHA-256
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");

	    // Obtener los bytes del texto
	    byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);

	    // Calcular el hash
	    byte[] hash = digest.digest(bytes);

	    // Convertir el hash a una representación hexadecimal
	    StringBuilder hexString = new StringBuilder();
	    for (byte b : hash) {
		String hex = Integer.toHexString(0xff & b);
		if (hex.length() == 1) {
		    hexString.append('0');
		}
		hexString.append(hex);
	    }
	    return hexString.toString();

	} catch (NoSuchAlgorithmException e) {
	    String traza = "generarHash256 : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return null;
	}
    }

    public String convertToWavFromAudioBytes(byte[] audioBytes, String uploadDir, String wavFilename) {
	String filePath = null;
	try {
	    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
	    AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBytes), audioFormat,
		    audioBytes.length / audioFormat.getFrameSize());
	    filePath = uploadDir + File.separator + wavFilename;
	    File file = new File(filePath);
	    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
	    audioInputStream.close();
	} catch (Exception e) {
	    String traza = "convertToWavFromAudioBytes : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}

	return filePath;
    }

    public byte[] convertirWavABytes(File archivoWav) {
	try {
	    FileInputStream fis = new FileInputStream(archivoWav);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[4096];
	    int bytesRead;

	    while ((bytesRead = fis.read(buffer)) != -1) {
		baos.write(buffer, 0, bytesRead);
	    }

	    fis.close();
	    baos.close();

	    return baos.toByteArray();
	} catch (IOException e) {
	    String traza = "convertirWavABytes : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return null;
	}
    }

    public int saveWav(File archivoWav, String uploadDir, String wavFilename)
	    throws UnsupportedAudioFileException, IOException {
	FileInputStream fis = new FileInputStream(archivoWav);
	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fis);
	String filePath = uploadDir + File.separator + wavFilename;
	return AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(filePath));
    }

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
	File file = new File(multipartFile.getOriginalFilename());
	multipartFile.transferTo(file);
	return file;
    }

    public String timestampNowStringWithFormat(String format) {
	Instant timestamp = Instant.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
	String timestampString = formatter.format(timestamp);
	return timestampString;
    }

    public long timestampWithFormat(String formatter, String date) {

	try {
	    // Crear un objeto SimpleDateFormat con el formato proporcionado
	    SimpleDateFormat sdf = new SimpleDateFormat(formatter);

	    // Parsear la cadena de fecha en un objeto Date
	    Date parsedDate = sdf.parse(date);

	    // Obtener el timestamp en milisegundos
	    long timestamp = parsedDate.getTime();

	    return timestamp;
	} catch (ParseException e) {
	    // Manejar la excepción si hay un error al parsear la fecha
	    String traza = "timestampWithFormat : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return -1; // Devolver un valor especial para indicar un error
	}
    }

    public String getFormattedDateFromTimestamp(String formatter, Long timestamp) {
	SimpleDateFormat dateFormat = new SimpleDateFormat(formatter);
	Date date = new Date(timestamp);
	return dateFormat.format(date);
    }

    public String getImagenPublicUrlByNotificacion(String imagenRelativeUri) {
	String[] splitedUri = imagenRelativeUri.split("/");
	String hostNuka = nukaConfiguration.getNUKA_HOST();
	String finalUrl = Constantes.NUKA_URL_NOAUTH_IMAGEN.replace("URL", hostNuka)
		+ splitedUri[splitedUri.length - 1];
	return finalUrl;
    }

    public String timestampNowString() {
	Instant timestamp = Instant.now();
	return Long.toString(timestamp.toEpochMilli());
    }

    public Long timestampNowLong() {
	Instant timestamp = Instant.now();
	return timestamp.toEpochMilli();
    }

    public String generarIdAleatorio(int longitud) {
	String caracteres = "abcdefghijklmnopqrstuvwxyz0123456789";
	StringBuilder resultado = new StringBuilder();

	for (int i = 0; i < longitud; i++) {
	    int indice = (int) (Math.random() * caracteres.length());
	    resultado.append(caracteres.charAt(indice));
	}

	return resultado.toString();
    }

    public String generarIdMongo() {
	return timestampNowString() + "_" + generarIdAleatorio(24);
    }

    public String guardarAudio(MultipartFile audioFile, String fileName) {
	String filePath = null;
	try {
	    filePath = Constantes.AUDIO_PATH + fileName;
	    audioFile.transferTo(new File(filePath));
	} catch (IOException e) {
	    String traza = "guardarAudio : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	return filePath;
    }

    public String guardarArchivoTemporal(MultipartFile file, String carpeta, String fileName) {
        String filePath = null;
        try {

            System.out.println("Constantes.TMP_PATH + carpeta: " + Constantes.TMP_PATH + carpeta);

            File tmpDirectory = new File(Constantes.TMP_PATH + carpeta);
            if (!tmpDirectory.exists() && !tmpDirectory.mkdirs()) {
                throw new IOException("No se pudo crear el directorio temporal: " + Constantes.TMP_PATH);
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Construir el nombre completo del archivo con la extensión
            String fullFileName = fileName + extension;

            // Construir la ruta completa
            filePath = Constantes.TMP_PATH + carpeta + File.separator + fullFileName;

            // Guardar el archivo en la ruta especificada
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            String traza = "guardarArchivoTemporal : ";
            logger.error(traza + e.getMessage());
            e.printStackTrace();
        }
        return filePath;
    }
    
    public String getResourceMapKeyByValue(String valorBuscado) {
	for (Map.Entry<String, List<String>> entry : Constantes.MAP_RESOURCE.entrySet()) {
	    List<String> valores = entry.getValue();
	    if (valores.contains(valorBuscado)) {
		return entry.getKey();
	    }
	}
	return null;
    }

    public String guardarMultipartFile(MultipartFile file, String fileName) {
	String filePath = null;
	try {
	    String resourcekey = getResourceMapKeyByValue(file.getContentType());
	    List<String> resourceValues = Constantes.MAP_RESOURCE.get(resourcekey);
	    filePath = Constantes.DATOS_PATH + File.separator + resourceValues.get(0) + File.separator + (fileName+"_test")
		    + resourceValues.get(1);
	    file.transferTo(new File(filePath));

	} catch (IOException e) {
	    String traza = "guardarMultipartFile : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	return filePath;
    }

    public String convertAACtoWAV(String inputFile) {
	String outputFile = inputFile.replace(".aac", ".wav");
	try {

	    File fileOriginal = new File(inputFile);
	    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileOriginal);

	    AudioFormat desiredAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, // Sample Rate
		    16, // Sample Size In Bits
		    2, // Channels (Stereo)
		    4, // Frame Size
		    44100, // Frame Rate
		    false); // Big Endian

	    File fileDestino = new File(outputFile);
	    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileDestino);

	} catch (Exception e) {
	    String traza = "convertAACtoWAV : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	return null;
    }

    public boolean isWindows() {
	return (OS.indexOf("win") >= 0);
    }

    public boolean isMac() {
	return (OS.indexOf("mac") >= 0);
    }

    public boolean isUnix() {
	return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public boolean isSolaris() {
	return (OS.indexOf("sunos") >= 0);
    }

    public String convertToWav(String inputFilePath) throws IOException {
	if (ObjectUtils.isEmpty(inputFilePath)) {
	    throw new IllegalArgumentException("Los nombres de archivo no pueden estar vacíos.");
	}
	String outputFilePath = inputFilePath.replace(".aac", ".wav");

	String ffmpegPath = null;
	if (isWindows()) {
	    ffmpegPath = "C:\\FFmpeg\\bin\\ffmpeg.exe";
	} else {
	    ffmpegPath = "ffmpeg";
	}

	// Comando para convertir a WAV.
	String[] command = { ffmpegPath, "-i", inputFilePath, "-c:a", "pcm_s16le", outputFilePath };
	// Ejecuta el comando.
	ProcessBuilder processBuilder = new ProcessBuilder(command);
	Process process = processBuilder.start();
	try {
	    process.waitFor();
	} catch (InterruptedException e) {
	    String traza = "convertToWav : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return null;
	}
	return outputFilePath;
    }

    public Boolean borrarArchivo(String rutaArchivo) {
	Path pathArchivo = Paths.get(rutaArchivo);
	if (Files.exists(pathArchivo)) {
	    try {
		Files.delete(pathArchivo);
		return true;
	    } catch (IOException e) {
		logger.error(e.getMessage());
		return false;
	    }
	} else {
	    return false;
	}
    }

    public OpcionBasica getOpcionBasica(NukaChat chat, String etiquetaGrupoOpciones, String id) {
	try {
	    List<GrupoOpciones> grupoOpciones = chat.getOpcionesModal().getOpciones();
	    GrupoOpciones go = grupoOpciones.stream().filter(go1 -> etiquetaGrupoOpciones.equals(go1.getEtiqueta())).toList().get(0);
	    OpcionBasica opcionBasica = go.getOpciones().stream().filter(ob -> id.equals(ob.getId())).toList().get(0);
	    return opcionBasica;
	} catch (Exception e) {
	    return null;
	}
    }


	public Double calculateCosineSimilarity(Double[] vectorA, Double[] vectorB) {
        // Verificar que los vectores tengan la misma longitud
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Los vectores deben tener la misma longitud");
        }

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        // Calcular el producto punto y las magnitudes en un solo bucle (más eficiente)
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            magnitudeA += vectorA[i] * vectorA[i];
            magnitudeB += vectorB[i] * vectorB[i];
        }

        // Tomar la raíz cuadrada para obtener las magnitudes
        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        // Evitar división por cero
        if (magnitudeA == 0 || magnitudeB == 0) {
            return 0.0; // Similitud coseno no definida cuando un vector es cero
        }

        return dotProduct / (magnitudeA * magnitudeB);
    }
	

	public Double[] parseEmbeddingResponse(String embeddingResponse) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(embeddingResponse);
		JsonNode embeddingNode = rootNode.get("embedding");

		if (embeddingNode != null && embeddingNode.isArray()) {
			Double[] embeddingArray = new Double[embeddingNode.size()];
			for (int i = 0; i < embeddingNode.size(); i++) {
				embeddingArray[i] = embeddingNode.get(i).asDouble();
			}
			return embeddingArray;
		}
		return new Double[0]; // Si hay un error, retornar un array vacío
	}

    private Map<String, String> getUserData(String usuarioId) {
	Optional<User> usuario = userRepository.findById(usuarioId);
	if (usuario != null & usuario.get() != null) {
	    Map<String, String> MAP_DATOS = Stream
		    .of(new Object[][] { { "$DATE", timestampNowStringWithFormat("yyyy-MM-dd HH:mm:ss") },
			    { "--$TEST--", "--TEST--" } })
		    .collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
	    return MAP_DATOS;
	}
	return null;
    }

    public String cambiarDatosUsuarioDeTexto(String usuarioId, String texto) {
	String res = texto;
	Map<String, String> userData = getUserData(usuarioId);
	if (userData != null) {
	    for (Map.Entry<String, String> entry : userData.entrySet()) {
		res = res.replace(entry.getKey(), entry.getValue());
	    }
	    return res;
	} else {
	    return null;
	}
    }

    public String convertFileToString(String pathFile) {
	try {
	    return new String(Files.readAllBytes(Paths.get(pathFile)));
	} catch (IOException e) {
	    String traza = "convertFileToString : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	return null;
    }

    public String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    public String getFileExtension(String filePath) {
        String fileName = getFileName(filePath);
        int dotIndex = fileName.lastIndexOf('.');
        if(dotIndex >= 0) {
            return fileName.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    public void espera(int desde, int hasta) {
	espera((int) (Math.random() * (hasta - desde)) + desde);
    }

    public void espera(int tiempoMilisegundos) {
	try {
	    Thread.sleep(tiempoMilisegundos);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
    
    public String markdownToPlainText(String markdown) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        // Parsear markdown
        Node document = parser.parse(markdown);
        // Renderizar a HTML y luego puedes limpiar HTML si deseas
        String html = renderer.render(document);
        // Extrae texto plano del HTML
        String plainText = html.replaceAll("<[^>]+>", ""); // Elimina etiquetas HTML
        
        System.out.println(plainText);
		return plainText;
   
    }
    
}

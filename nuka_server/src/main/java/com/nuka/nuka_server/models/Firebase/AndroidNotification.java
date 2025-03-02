package com.nuka.nuka_server.models.Firebase;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    
@JsonIgnoreProperties(ignoreUnknown = true)
public class AndroidNotification {

    private String title;
    private String body;
    private String icon;
    private String color;
    private String sound;
    private String tag;
    private String clickAction;
    private String bodyLocKey;
    private List<String> bodyLocArgs;
    private String titleLocKey;
    private List<String> titleLocArgs;
    private String channelId;
    private String ticker;
    private boolean sticky;
    private String eventTime;
    private boolean localOnly;
    private NotificationPriority notificationPriority;
    private boolean defaultSound;
    private boolean defaultVibrateTimings;
    private boolean defaultLightSettings;
    private List<String> vibrateTimings;
    private Visibility visibility;
    private int notificationCount;
    private LightSettings lightSettings;
    private String image;

    // Enum para NotificationPriority
    public enum NotificationPriority {
	/*
	PRIORITY_UNSPECIFIED	Si no se especifica la prioridad, la prioridad de notificación se establece en PRIORITY_DEFAULT .
	PRIORITY_MIN	Prioridad de notificación más baja. Es posible que las notificaciones con este PRIORITY_MIN no se muestren al usuario excepto en circunstancias especiales, como registros de notificaciones detallados.
	PRIORITY_LOW	Menor prioridad de notificación. La interfaz de usuario puede optar por mostrar las notificaciones en un tamaño más pequeño o en una posición diferente en la lista, en comparación con las notificaciones con PRIORITY_DEFAULT .
	PRIORITY_DEFAULT	Prioridad de notificación predeterminada. Si la aplicación no prioriza sus propias notificaciones, utilice este valor para todas las notificaciones.
	PRIORITY_HIGH	Mayor prioridad de notificación. Úselo para notificaciones o alertas más importantes. La interfaz de usuario puede optar por mostrar estas notificaciones en mayor tamaño o en una posición diferente en las listas de notificaciones, en comparación con las notificaciones con PRIORITY_DEFAULT .
	PRIORITY_MAX	Máxima prioridad de notificación. Utilícelo para los elementos más importantes de la aplicación que requieren la rápida atención o entrada del usuario.
	 */
	PRIORITY_UNSPECIFIED, PRIORITY_MIN, PRIORITY_LOW, PRIORITY_DEFAULT, PRIORITY_HIGH, PRIORITY_MAX
    }

    // Enum para Visibility
    public enum Visibility {
        /*
        VISIBILITY_UNSPECIFIED	Si no se especifica, el valor predeterminado es Visibility.PRIVATE .
        PRIVATE	Muestre esta notificación en todas las pantallas de bloqueo, pero oculte información confidencial o privada en pantallas de bloqueo seguras.
        PUBLIC	Muestra esta notificación en su totalidad en todas las pantallas de bloqueo.
        SECRET	No reveles ninguna parte de esta notificación en una pantalla de bloqueo segura. 
         */
	VISIBILITY_UNSPECIFIED, PRIVATE, PUBLIC, SECRET
    }

	public AndroidNotification(String title, String body, String icon, String color, String sound, String tag,
			String clickAction, String bodyLocKey, List<String> bodyLocArgs, String titleLocKey,
			List<String> titleLocArgs, String channelId, String ticker, boolean sticky, String eventTime,
			boolean localOnly, NotificationPriority notificationPriority, boolean defaultSound,
			boolean defaultVibrateTimings, boolean defaultLightSettings, List<String> vibrateTimings,
			Visibility visibility, int notificationCount, LightSettings lightSettings, String image) {
		super();
		this.title = title;
		this.body = body;
		this.icon = icon;
		this.color = color;
		this.sound = sound;
		this.tag = tag;
		this.clickAction = clickAction;
		this.bodyLocKey = bodyLocKey;
		this.bodyLocArgs = bodyLocArgs;
		this.titleLocKey = titleLocKey;
		this.titleLocArgs = titleLocArgs;
		this.channelId = channelId;
		this.ticker = ticker;
		this.sticky = sticky;
		this.eventTime = eventTime;
		this.localOnly = localOnly;
		this.notificationPriority = notificationPriority;
		this.defaultSound = defaultSound;
		this.defaultVibrateTimings = defaultVibrateTimings;
		this.defaultLightSettings = defaultLightSettings;
		this.vibrateTimings = vibrateTimings;
		this.visibility = visibility;
		this.notificationCount = notificationCount;
		this.lightSettings = lightSettings;
		this.image = image;
	}

	public AndroidNotification() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getClickAction() {
		return clickAction;
	}

	public void setClickAction(String clickAction) {
		this.clickAction = clickAction;
	}

	public String getBodyLocKey() {
		return bodyLocKey;
	}

	public void setBodyLocKey(String bodyLocKey) {
		this.bodyLocKey = bodyLocKey;
	}

	public List<String> getBodyLocArgs() {
		return bodyLocArgs;
	}

	public void setBodyLocArgs(List<String> bodyLocArgs) {
		this.bodyLocArgs = bodyLocArgs;
	}

	public String getTitleLocKey() {
		return titleLocKey;
	}

	public void setTitleLocKey(String titleLocKey) {
		this.titleLocKey = titleLocKey;
	}

	public List<String> getTitleLocArgs() {
		return titleLocArgs;
	}

	public void setTitleLocArgs(List<String> titleLocArgs) {
		this.titleLocArgs = titleLocArgs;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public boolean isSticky() {
		return sticky;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public boolean isLocalOnly() {
		return localOnly;
	}

	public void setLocalOnly(boolean localOnly) {
		this.localOnly = localOnly;
	}

	public NotificationPriority getNotificationPriority() {
		return notificationPriority;
	}

	public void setNotificationPriority(NotificationPriority notificationPriority) {
		this.notificationPriority = notificationPriority;
	}

	public boolean isDefaultSound() {
		return defaultSound;
	}

	public void setDefaultSound(boolean defaultSound) {
		this.defaultSound = defaultSound;
	}

	public boolean isDefaultVibrateTimings() {
		return defaultVibrateTimings;
	}

	public void setDefaultVibrateTimings(boolean defaultVibrateTimings) {
		this.defaultVibrateTimings = defaultVibrateTimings;
	}

	public boolean isDefaultLightSettings() {
		return defaultLightSettings;
	}

	public void setDefaultLightSettings(boolean defaultLightSettings) {
		this.defaultLightSettings = defaultLightSettings;
	}

	public List<String> getVibrateTimings() {
		return vibrateTimings;
	}

	public void setVibrateTimings(List<String> vibrateTimings) {
		this.vibrateTimings = vibrateTimings;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public int getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(int notificationCount) {
		this.notificationCount = notificationCount;
	}

	public LightSettings getLightSettings() {
		return lightSettings;
	}

	public void setLightSettings(LightSettings lightSettings) {
		this.lightSettings = lightSettings;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}



}



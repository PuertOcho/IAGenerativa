package com.nuka.nuka_server.models;

public class ElementoSeleccionado {

	public String id;
	public Archivo archivo;
	public String value;

    public ElementoSeleccionado(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public ElementoSeleccionado(String id, Archivo archivo) {
        this.id = id;
        this.archivo = archivo;
    }
    

    public ElementoSeleccionado() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Método para verificar si el valor es un archivo
    public boolean isFile() {
        return archivo instanceof Archivo;
    }

    // Método para obtener el archivo si es válido
    public Archivo getArchivo() {
        if (isFile()) {
            return (Archivo) archivo;
        }
        throw new IllegalStateException("El valor no es un archivo.");
    }


    public static class Archivo {
        private String name;
        private byte[] content;
        private String path;
        
        public Archivo(String name, byte[] content) {
            this.name = name;
            this.content = content;
        }

        public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }
    }
    
}

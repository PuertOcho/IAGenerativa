#!/bin/bash

# Comprueba si los archivos docker-compose-template.yml
if [ ! -f "docker-compose-template.yml" ]; then
    echo "El archivo docker-compose-template.yml no existe."
    exit 1
fi

git checkout master > /dev/null
git pull
sudo chmod -R 777 ./datos/mongodb > /dev/null

# Determina el nombre del archivo de salida
docker compose -f docker-compose.dev.yml down
docker image rm nuka_server-spring-dev-c:latest -f

sudo cp docker-compose-template.yml docker-compose.dev.yml
while IFS='=' read -r label value; do
sed -i "s/$label/$value/g" docker-compose.dev.yml
done < config.dev
   
sudo cp application.properties-template src/main/resources/application.properties
while IFS='=' read -r label value; do
sed -i "s/$label/$value/g" src/main/resources/application.properties
done < config.dev
   
docker compose -f docker-compose.dev.yml up -d

sudo chmod -R 777 .

echo "Archivo $output_file creado con éxito para el entorno $environment."

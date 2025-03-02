#!/bin/bash

sudo docker compose down
sudo docker image rm nuka_app-nuka-app:latest -f
sudo docker compose up -d

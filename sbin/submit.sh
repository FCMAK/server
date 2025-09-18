#!/bin/bash
user_ss="servisofts"
host_ss="192.168.2.3"
path_server="/home/servisofts/servicios/tapeke/entornos/tapeke/servicios/tapeke/"


ssh "$user_ss@$host_ss" "cp $path_server/server.jar $path_server/server.jar.bak"
scp "./Tapeke-Server.jar" "$user_ss@$host_ss:$path_server/server.jar"
scp -r "./query" "$user_ss@$host_ss:$path_server/"
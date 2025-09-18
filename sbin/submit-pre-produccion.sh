#!/bin/bash
user_ss="servisofts"
# host_ss="test.tapeke.servisofts.com"
host_ss="192.168.2.2"
path_server="/home/servisofts/servicios_test/tapeke/entornos/tapeke-test/servicios/tapeke/"

# scp "./servisofts.jks" "$user_ss@$host_ss:$path_server"
# scp "./config.json" "$user_ss@$host_ss:$path_server"
# scp "./servicio.pem" "$user_ss@$host_ss:$path_server"
scp "./Tapeke-Server.jar" "$user_ss@$host_ss:$path_server/server.jar"
scp -r "./query" "$user_ss@$host_ss:$path_server/"
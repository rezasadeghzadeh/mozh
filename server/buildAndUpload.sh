#!/usr/bin/env bash
export  GOARCH=386
cd /home/reza/java/mozhdengi/server/main
go  build  main.go
scp  main  root@ochm.yourlocalplace.com:/opt/mozhdeh/server
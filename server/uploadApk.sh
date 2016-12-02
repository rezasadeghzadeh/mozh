#!/bin/bash
cd /home/reza/java/mozhdengi/app/app/build/outputs/apk/
mv  app-release.apk mozhdeh.apk 
scp mozhdeh.apk  root@ochm.yourlocalplace.com:/opt/mozhdeh/updates/

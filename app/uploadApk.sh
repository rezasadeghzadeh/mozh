#!/bin/bash
cd /home/reza/java/mozhdeh/app/app/build/outputs/apk/
mv  app-release.apk mozhdeh.apk 
md5sum  mozhdeh.apk
scp mozhdeh.apk  root@ochm.yourlocalplace.com:/opt/mozhdeh/updates/

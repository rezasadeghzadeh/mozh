#!/bin/bash
tar -czvf web.tar.gz  web
scp  web.tar.gz  root@ochm.yourlocalplace.com:/var/www/ochm/cybervillage/root/static/mozh/

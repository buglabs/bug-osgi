#!/bin/bash

DATE=`date +%Y%m%d%H%M`
SOURCEDIR=/opt/cruisecontrol-bin-2.5/artifacts/com.buglabs.dragonfly/current
FILENAME=dragonfly-$DATE.zip
REMOTEDIR=/data/buglabs/shared/sdk/integration
REMOTESYS=buglabs_prod
REMOTEUSER=buglabs

cd $SOURCEDIR
cp *updatesite* $FILENAME
scp $FILENAME $REMOTEUSER@$REMOTESYS:$REMOTEDIR
ssh $REMOTEUSER@$REMOTESYS "cd $REMOTEDIR ; mkdir $DATE ; cd $DATE ; unzip ../$FILENAME"
ssh $REMOTEUSER@$REMOTESYS "cd $REMOTEDIR ; rm current ; ln -s $DATE current ; rm $FILENAME"
rm $FILENAME
# ssh $REMOTEUSER@$REMOTESYS "/home/buglabs/bin/make_index.rb > /data/buglabs/shared/sdk/index.html"

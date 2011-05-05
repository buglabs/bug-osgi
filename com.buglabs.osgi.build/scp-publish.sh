#!/bin/bash

#   scp-publish.sh
#   This script will properly format and upload a Dragonfly SDK build for 
#     deployment on http://buglabs.net/sdk/

PUBENV=$1
if [ -z "$PUBENV" ]; then 
  echo
  echo "  Usage:"
  echo "    /usr/local/bin/scp-publish.sh environment"
  echo "    where environment is one of either production, testing or integration"
  echo
  exit 1
fi

DATE=`date +%Y%m%d%H%M`
WHOAMI=`/usr/bin/whoami`
# SOURCEDIR1=/opt/sdk_build/com.buglabs.build/artifacts/$PUBENV/com.buglabs.dragonfly/current
SOURCEDIR1=artifacts/$PUBENV/com.buglabs.dragonfly/current
FILENAME1=$SOURCEDIR1/com.buglabs.dragonfly.updatesite*.zip
VERSION=`ls $SOURCEDIR1/updatesite/features | grep "_[IPT]" | sed 's/com\.buglabs\.dragonfly\.feature_//' | sed 's/\.jar$//' | grep -v buglabs`
REMOTESYS=newkeds
REMOTEUSER=web
REMOTEBASEDIR=/var/tmp
REMOTEDIR1=$REMOTEBASEDIR/$PUBENV/$VERSION/updatesite/
REMOTEDIR2=$REMOTEBASEDIR/$PUBENV/$VERSION/full/

# Deploy a little differently from a Production build
if [ "$PUBENV" = "production" ]; then
  SOURCEDIR2=/opt/sdk_build/com.buglabs.build/artifacts/$PUBENV/com.buglabs.sdk/current
  FILENAME2=$SOURCEDIR2/dragonfly*.zip
  VERSION=`ls $SOURCEDIR2 | grep lin | sed 's/^.*\-//' | sed 's/\.zip$//' | grep -v buglabs`
fi

 echo "PUBENV is:   $PUBENV"
 echo "VERSION is:  $VERSION"
 echo "FILENAME1 IS: $FILENAME1"
 echo "SOURCEDIR1 is: $SOURCEDIR1"
 #exit 1

echo "Creating directories..."
ssh $REMOTEUSER@$REMOTESYS "mkdir -p $REMOTEDIR1"
echo "Uploading update site..."
scp $FILENAME1 $REMOTEUSER@$REMOTESYS:$REMOTEDIR1
echo "Decompressing update site..."
ssh $REMOTEUSER@$REMOTESYS "cd $REMOTEDIR1 ; unzip *.zip; rm *.zip"
if [ "$PUBENV" = "production" ]; then
  ssh $REMOTEUSER@$REMOTESYS "mkdir -p $REMOTEDIR2"
  echo "Uploading full SDKs..."
  scp $FILENAME2 $REMOTEUSER@$REMOTESYS:$REMOTEDIR2
fi
echo "Running remote commands..."
ssh $REMOTEUSER@$REMOTESYS "/home/web/setup_sdk_dirs.rb"

echo
echo "Build uploaded. Please open your web browser and go to http://buglabs.net/sdk/ to verify."
echo


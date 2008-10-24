#!/bin/bash
# This script will move a cvs sticky tag to the position of another tag or HEAD
#  $1 = project
#  $2 = source
#  $3 = dest
#  $4 = cvsauth
# Usage: migratetag project source_tag destination_tag cvs_auth

PROJECT=$1
SOURCE=$2
DEST=$3
CVSAUTH=$4

function die {
   echo $1
   exit
}

function run() {
   cvs -d $CVSAUTH co -r $DEST $PROJECT
   cvs -d $CVSAUTH tag -d $SOURCE
   cvs -d $CVSAUTH tag $SOURCE
   rm -Rf $PROJECT
}

if [ -z "${PROJECT}" ] || [ -z "${SOURCE}" ] || [ -z "${DEST}" ] || [ -z "${CVSAUTH}" ] ; then 
   die "usage: ./migration.sh  <project> <source> <destination> <cvsauth>"
else
   run
fi

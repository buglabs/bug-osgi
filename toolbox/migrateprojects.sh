#!/bin/bash
# This script will take a list of projects, and migrate tags.  See migratetag.sh for more information
# $1 = file containing project names
# $2 = source
# $3 = dest
# $4 = cvs auth

for i in `cat $1`; do  
  ./migratetag.sh $i $2 $3 $4
done

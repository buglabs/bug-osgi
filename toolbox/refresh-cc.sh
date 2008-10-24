#!/bin/bash
# This script will replace the CC configuration and root build files from the CVS HEAD
rm -Rf ../projects/*.xml
rm ../config.xml
cvs -d :pserver:anonymous@lurcher:/root co com.buglabs.build
cp com.buglabs.build/projects/*.xml ../projects/
cp com.buglabs.build/config.xml ../
rm -Rf com.buglabs.build
echo "CC Configuration and product build files updated."
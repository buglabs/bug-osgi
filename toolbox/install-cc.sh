#!/bin/bash
# This script unzips cruise control, removes some files and installs it in the root of the build project.
echo "Downloading cruisecontrol-bin-2.7.1.zip..."
echo ""
wget http://prdownloads.sourceforge.net/cruisecontrol/cruisecontrol-bin-2.7.1.zip?download
unzip cruisecontrol-bin-2.7.1.zip 
rm -Rf cruisecontrol-bin-2.7.1/projects
rm -Rf cruisecontrol-bin-2.7.1/*.bat
rm -Rf cruisecontrol-bin-2.7.1/logs/*
rm cruisecontrol-bin-2.7.1/config.xml 
mv cruisecontrol-bin-2.7.1/* ../
rm -Rf cruisecontrol-bin-2.7.1

# Overview
This repository contains the libraries used for Java application development on the BUG platform.  Each library is packages as an OSGi bundle, and the OSGi service registry is used extensively.

# To build 
Set the `basedir` variable to the directory which build artifacts and test results should be created at, then run the ant build:

    ant -Dbasedir=/tmp -f bug-osgi/com.buglabs.osgi.build/com.buglabs.osgi.xml clean fetch.dependencies test test-osgi checkstyle pmd cpd

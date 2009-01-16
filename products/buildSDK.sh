build_level=$1
ant -Dbuild.level=$build_level -f com.buglabs.bug.emulator.xml
ant -Dbuild.level=$build_level -f com.buglabs.osgi.concierge.xml
ant -Dbuild.level=$build_level -f com.buglabs.dragonfly.xml

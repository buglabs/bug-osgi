cd rpm/BUILD/linux
cp new_config .config
make ARCH=arm CROSS_COMPILE= 'HOSTCC=/usr/bin/gcc -B/usr/bin//'  oldconfig
make ARCH=arm CROSS_COMPILE= 'HOSTCC=/usr/bin/gcc -B/usr/bin//'  zImage
make ARCH=arm CROSS_COMPILE= 'HOSTCC=/usr/bin/gcc -B/usr/bin//'  modules
echo "DONE"


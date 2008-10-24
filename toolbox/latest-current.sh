#!/bin/bash

if [[ ${#} != 2 ]]
then
    echo "link-to-newest-subdirectory <DirectoryName> <LinkName>"
    exit -1
fi

rm -f $2
rm -f /tmp/outf2
rm -f /tmp/outf3
rm -f /tmp/outf4
find $1 -maxdepth 1 -type d > /tmp/outf2
egrep -v ^$1$ /tmp/outf2 > /tmp/outf3
ls -trd $(cat /tmp/outf3) > /tmp/outf4
tail -1 /tmp/outf4 > /tmp/outf5
sed 's;^.*/\([^/][^/]*\)$;\1;' /tmp/outf5 > /tmp/outf6
ln -s $(cat /tmp/outf5) $2



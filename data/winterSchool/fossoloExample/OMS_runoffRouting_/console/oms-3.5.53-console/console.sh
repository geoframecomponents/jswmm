#!/bin/sh
set +u

dist=`dirname "$0"`
flags="-client -Xms128m -Xmx512m"

if [ ! -f "$dist/oms.console.jar" ]; then
    echo "oms.console.jar not found: $dist/oms.console.jar"
    exit 2
fi


if [ `uname` = "Darwin" ]; then
    opt="-Xdock:name=OMS -Xdock:icon=$dist/objects-icon-64.png"
    opt="$opt -Dcom.apple.mrj.application.apple.menu.about.name=OMS"
    opt="$opt -Dapple.laf.useScreenMenuBar=true"
else
    opt=
fi

java $flags $opt \
    -Dsun.java2d.d3d=false \
    -jar $dist/oms.console.jar "$@"

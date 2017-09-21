#! /bin/sh
case $1 in
  "build") docker build . -t grb;;
  "run") docker run -ti --rm --name grb -p 8080:8080 -v `pwd`:/app grb sbt;;
  "*") $1;;
esac

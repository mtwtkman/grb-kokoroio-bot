#! /bin/sh
case $1 in
  "build") docker build . -t grbr_kokoroio_bot;;
  "run") docker run -ti --rm --name grbr_kokoroio_bot --env-file ./.env -p 8080:8080 -v `pwd`:/app grbr_kokoroio_bot sbt;;
  *) $1;;
esac

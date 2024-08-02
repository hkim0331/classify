#!/bin/sh
#
# sample start.sh
#
export BASE="/Users/hkim/ramdisk/"
export SRC="unsorted/"
export DESTS='{"A" "a/" "B" "b/" "C" "c/" "D" "d/" "Other" "other/"}'
export PORT=3333

java -jar target/default+uberjar/classify.jar

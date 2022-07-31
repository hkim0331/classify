#!/bin/sh
#
# origin: ${utils}/src/bump-version.sh
#
# bump-vesion script for clojure projects.
# confused using macos's /usr/bin/sed. so gsed.
#
# CAUSION
# The POSIX standard regular expressions does not support back-references.
# Back-references are considered as an "extended" faciliy.
# This script, bump-version.sh, uses the extended function.
# So, gnu-sed on macOS.

if [ -z "$1" ]; then
    echo "usage: $0 <version>"
    exit
fi

# use  extended regular expressions in the script
if [ -x "${HOMEBREW_PREFIX}/bin/gsed" ]; then
    SED="${HOMEBREW_PREFIX}/bin/gsed -E"
else
    SED="/usr/bin/sed -E"
fi

# project.clj
${SED} -i.bak "s/(defproject \S+) \S+/\1 \"$1\"/" project.clj

now=`date '+%F %T'`
${SED} -i.bak \
    -e "s/(def \^:private version) .+/\1 \"$1\")/" \
    -e "s/(def \^:private updated_at) .+/\1 \"$now\")/" \
    src/clj/classify/routes/home.clj

# cljs
${SED} -i.bak \
    -e "s/^\(def \^:private version .+/(def ^:private version \"$1\")/" \
    src/cljs/classify/core.cljs

${SED} -i.bak \
    -e "s/app.js\?version=.*/app.js?version=$1\"%}/" \
    resources/html/home.html


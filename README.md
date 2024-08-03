# classify

generated using Luminus version "4.39"

Classify (grouing?) students' reports and exam-papers quickly after browsing.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

    npm install
    npm install xmlhttprequest

## Usage

See dev-config.edn. Other than luminus app settings,
:base, :src, and :dests are found. You can image what they mean.

{:base "/Users/hkim/ramdisk/"
 :src "unsorted/"
 :dests {"A" "a/"
         "B" "b/"
         "C" "c/"
         "D" "d/"
         "Other" "o/"}}

To start a web server for the application, run:

    lein run

or,

```sh
#!/bin/sh
export BASE="/Users/hkim/ramdisk/"
export SRC="unsorted/"
export DESTS='{"A" "a/" "B" "b/" "C" "c/" "D" "d/" "Other" "other/"}'
java -jar target/uberjar/classify.jar
```

## Example usage

```sh
% cd <to this directory>
% npx shadow-cljs watch app
% tar -cf unsorted | (cd ~/ramdisk && tar -xf -)
% code .
clj:user> (start)
% open http://localhost:3000
```

## Calva

1. Leiningen + shadow-cljs
2. after stating, choose :app to build.

## License

Copyright © 2022 Hiroshi Kimura

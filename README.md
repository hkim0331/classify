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

## License

Copyright © 2022 Hiroshi Kimura

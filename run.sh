#!/bin/bash
# stackoverflow.com/q/59895#246128
readonly DIR="$(cd "$( dirname "$0}" )" &> /dev/null && pwd)"
cd $DIR
jwebserver -d "$DIR/.znai-plugin/dist" &
mdbook serve --open

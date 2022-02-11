#!/bin/bash
if [ ! -d .tmp ]; then
		mkdir -m 777 .tmp
fi

if [ ! -d .ivy2 ]; then
		mkdir -m 777 .ivy2
fi

if [ ! -d .cache ]; then
		mkdir -m 777 .cache
fi

if [ ! -d ~/.npm ]; then
		mkdir -m 777  ~/.npm
fi
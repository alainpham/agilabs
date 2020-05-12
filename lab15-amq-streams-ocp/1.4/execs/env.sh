#!/bin/bash
cat config/application.properties | awk -F= '{print "-D" $1 "=" $2 }'
cat config/application.properties | awk -F= '{printf "%s ", "-D" $1 "=" $2 }'
cat config/application.properties | awk -F= '{gsub("\\.","_",$1) ;print "export " toupper($1)  "=" $2 }'

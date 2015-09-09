#!/bin/bash
# capture first command line argument (folder to overload with files)
PATH=$1
# capture second command line argument (number of files to create)
NUMFILES=$2


COUNTER=0
for ((i=0; i < $NUMFILES; i++))
do
  FILENAME="file$COUNTER.ext(Richie's conflicted copy 2015-09-08).sookasa"
  echo > "$PATH/$FILENAME" The counter is $COUNTER
  let COUNTER=COUNTER+1
done
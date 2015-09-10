#!/bin/bash
# capture first command line argument (folder to overload with files)
PATHSOOKASA=$1
# capture second command line argument (number of files to create)
NUMFILES=$2

COUNTER=0
for ((i=0; i < $NUMFILES; i++))
do
  # Create dummy filename
  FILENAME="file$COUNTER.ext(Richie's conflicted copy 2015-09-08).sookasa"
  # Copy test file into sookasa folder with dummy filename
  cp ./test_files/testImage.jpg "$PATHSOOKASA/$FILENAME"
  let COUNTER=COUNTER+1
done

find $PATHSOOKASA -maxdepth 1 -type f -name "*).sookasa" | grep -q $PATHSOOKASA \
  && echo Test Failed - Not all files were renamed properly \
  || echo Test Passed - All files were properly renamed
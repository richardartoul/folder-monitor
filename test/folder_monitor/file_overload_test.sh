#!/bin/bash
# capture first command line argument (folder to overload with files)
PATHSOOKASA=$1
# capture second command line argument (number of files to create)
NUMFILES=$2

# Use a simple regex to check if any improperly named files remain in the folder.
# This regex is definitely not robust enough, but it serves its purpose for this simple test.
# If folder-monitor is working properly, there should be none, and the script will echo "Test Passed"
# -maxdepth 2 means that it will also searched within the nested directory
find $PATHSOOKASA -maxdepth 2 -type f -name "*).sookasa" | grep -q $PATHSOOKASA \
  && echo Simple Test Failed - Not all files were renamed properly \
  || echo Simple Test Passed - All files were properly renamed

echo Cleaning up...

# Remove all the created files
COUNTER=0
for ((i=0; i < $NUMFILES; i++))
do
  FILENAME="file$COUNTER(Richie's conflicted copy 2015-09-08).ext.sookasa"
  rm "$PATHSOOKASA/$FILENAME"
  let COUNTER=COUNTER+1
done

# removed nested folder
rm -r "$PATHSOOKASA/nested"
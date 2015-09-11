#!/bin/bash
# capture first command line argument (folder to overload with files)
PATHSOOKASA=$1
# capture second command line argument (number of files to create)
NUMFILES=$2

mkdir "$PATHSOOKASA/nested"
sleep 2

COUNTER=0
# create number of files based on second command line argument
for ((i=0; i < $NUMFILES; i++))
do
  # Create dummy filename
  FILENAME="file$COUNTER.ext(Richie's conflicted copy 2015-09-08).sookasa"
  # Copy test file into sookasa folder with dummy filename
  cp ./test_files/testImage.jpg "$PATHSOOKASA/$FILENAME"
  let COUNTER=COUNTER+1
done

# Same as above, but copies files into folder nested within monitored folder
# This tests to make sure that the program properly registers new event listeners
# for subdirectories that are added after the program has started running
# Could have included this in the previous loop, but broke it out for clarity
let COUNTER=0
for ((i=0; i < $NUMFILES; i++))
do
  # Create dummy filename
  FILENAME="file$COUNTER.ext(Richie's conflicted copy 2015-09-08).sookasa"
  # Copy test file into sookasa folder with dummy filename
  cp ./test_files/testImage.jpg "$PATHSOOKASA/nested/$FILENAME"
  let COUNTER=COUNTER+1
done

echo "$NUMFILES Files created!"
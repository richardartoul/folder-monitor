#!/bin/bash
# capture first command line argument (folder to overload with files)
PATHSOOKASA=$1
# capture second command line argument (number of times to copy test files)
NUMFILES=$2

mkdir "$PATHSOOKASA/nested"

COUNTER=0
# create number of files based on second command line argument
for ((i=0; i < $NUMFILES; i++))
do
  # Create dummy filename
  FILENAME="file$COUNTER.ext(Richie's conflicted copy 2015-09-08).sookasa"
  # Copy test file into sookasa folder with dummy filename
  cp ./test_files/testImage.jpg "$PATHSOOKASA/image-$FILENAME"
  cp ./test_files/testWordDoc.doc "$PATHSOOKASA/doc-$FILENAME"
  cp ./test_files/testVideo.mp4 "$PATHSOOKASA/video-$FILENAME"
  let COUNTER=COUNTER+1
  # Simulating a 50 millisecond pause to copy each file
  # sleep 0.05
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
  cp ./test_files/testImage.jpg "$PATHSOOKASA/nested/image-$FILENAME"
  cp ./test_files/testWordDoc.doc "$PATHSOOKASA/nested/doc-$FILENAME"
  cp ./test_files/testVideo.mp4 "$PATHSOOKASA/nested/video-$FILENAME"
  let COUNTER=COUNTER+1
  # Simulating a 50 millisecond pause to copy each file
  # sleep 0.05
done

echo "Test files copied $NUMFILES times!"
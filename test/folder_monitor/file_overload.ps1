param (
  [string]$PATH,
  [string]$NUMFILES
)

for ($i=1; $i < $NUMFILES; $i++) {
  $FILENAME="file$i.ext(Richie's conflicted copy 2015-09-08).sookasa"
  FOR %%G IN (test_files/testImage.jpg test_files/testVideo.mp4 test_files/testWordDoc.doc) DO copy %%G "$PATH/$FILENAME"
}

for ($i=1; $i < $NUMFILES; $i++) {
  $FILENAME="file$i.ext(Richie's conflicted copy 2015-09-08).sookasa"
  FOR %%G IN (test_files/testImage.jpg test_files/testVideo.mp4 test_files/testWordDoc.doc) DO copy %%G "$PATH/nested/$FILENAME"
}
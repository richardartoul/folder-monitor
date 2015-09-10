# folder-monitor

Folder-monitor is a simple program, written in [Clojure](http://clojure.org/) (a modern dialect of LISP), that monitors a specified folder for files whose name match a certain pattern, and renames them appropriately.

## Installation

The Clojure languages runs ontop of the JVM. As long as the host machine has Java installed, simply execute the jar file from the command line with the following command `PUT COMMAND HERE`. All of folder-monitor's dependcies are pre-packaged inside of the jar file.

Alternatively, if you'd like to build the jar file on your own, [DO THIS]

## Usage

Folder-monitor is packaged as jar file with all of its dependencies included. It accepts only one command line argument: the path of the Sookasa folder.

`$ java -jar folder-monitor-0.1.0-standalone.jar <path to sookasa folder>`

## Options

[Consider adding recurse as an option]

## Examples

`$ java -jar folder-monitor-0.1.0-standalone.jar /home/richie/Desktop/sookasa`

## Design Decisions

### Clojure Language

I chose to write folder-monitor in Clojure for a few reasons. First, I decided that I did not want to take a polling approach to checking the folder for incorrectly named files because this could become a huge performance issue with folders that have many files in them. Instead, I decided to tap into filesystem events to determine when a file was added or modified inside of the Sookasa folder. Clojure, being a primarily functional programming language, lends itself much more elegantly to solving this type of asynchronous, event-based problem than other languages I considered like Python and Java.

Second, I didn't want to have to write platform-specific code for interacting with the filesystem and listening for events. After doing some investigation online, I found that many of the python libraries for managing filesystem events were no longer maintained or had many known issues. The Java Watch Service API, on the other hand, seemed to be well documented, robust, and cross-platform. Since the Clojure language runs on top of the JVM, I was able to use (with a small library that basically serves as a Clojure wrapper around the Java Watch Service API) the same code for Linux, OSX, and Windows, despite the fact that they each have different filesystem APIs.

Finally, Clojure is a particularly expressive language and because of that the actual implementation of folder-monitor is approximately 15 lines of non-boilerplate code that can easily be reasoned about.

### Filesystem Events vs. Polling

I chose not to use polling because I was worried that if the user was storing a large number of files, indexing the files on a regular basis would quickly become a bottleneck. The file-system events approach avoids this issue because files are only changed when they are added to the folder, or any kind of modification happens to a file that already inside the folder.

The downside of the file-system event approach is that it doesn't affect files that are already improperly named in the folder. Only files that are added or modified are checked. However, this problem could easily be solved by performing a single index of all the files in the folder when the program starts, and then using filesystem events for all subsequent actions. 

## Testing

### Testing Decisions

I took a two-pronged approach to testing for the folder-monitor application. 

#### Unit Tests

First, I wrote a few simple unit tests that simply make sure the renaming behavior and regex for identifying improperly named files work properly. These tests are written in Clojure and can be executed using Leiningen as described below.

#### Stress Tests

Second, I wanted to stress test the application and make sure that it would handle large numbers of files being added to the monitored folder at once. I decided to implement this test using a bash script because I didn't want users to have to use Clojure/Leiningen to run this test. Also, Bash lends itself nicely to this type of test.

The bash script [file_overload.sh](test/test_files/file_overload.sh) copies a small (11kb) test image file into the monitored folder x number of times in rapid succession (I tested it up to x = 2000). It then checks to see if all the files were properly renamed, prints a test success/failure result, and then removes the test files from the folder.

### Running Tests

#### Unit Tests

1. Install [Leiningen](http://leiningen.org/)
2. Clone this github repository
3. Navigate to the local repository on your machine in the CLI
4. Execute `lein test`

![Folder-Monitor Unit Tests](resources/unit_tests.png)

#### Stress Tests

1. Make sure folder-monitor is running
2. Navigate to test/folder_monitor within the file structure
3. Execute the file_overload.sh script with two command line arguments. The first should be the absolute path to the Sookasa folder. The second should be the number of times the test file gets copied into the Sookasa folder.

![Folder-Monitor Stress Tests](resources/stress_tests.png)

## Bugs

One of the issues I found while stress testing the application is that it will fail if too many files are added to the folder at once. The limit (on my system) occurred when I tried to simultaneously add more than 500 files to the folder in quick succession, with each file being only a single line in length. I believe this issue is caused simply by blocking IO. Since the files are so small, they're added to the folder before the program can process each event and trigger a rename. Eventually an event is triggered and it encounters a blocked thread, causing a silent failure.

I believe this problem could be solved in two different ways:

1. The standard java IO API is blocking, however, as of Java 7 there is a non-blocking equivalent. Several Clojure wrappers exist for this functionality.

2. Clojure has excellent support for concurrency. A "load balancer" of sorts could be setup where one thread listens for events and delegates the actual filesystem operations to a cluster of "worker threads". This would allow the program to support many more events without file IO serving as a bottleneck. In fact, there is a Clojure library [OJO](https://github.com/rplevy/ojo) that implements filesystem event parallelism automatically.

I didn't implement either of these solutions because they both seemed like overkill for the problem at hand. In practical use, this bug doesn't seem to crop up as once the size of the files becomes a few kilobytes, it doesn't matter how many files are added to the Sookasa folder, the folder-monitor program is able to handle the load because the process of renaming a file becomes much quicker than copying a few kilobytes of data.

## Potential Improvements

1. Add support for concurrency / parallelism or non-blocking IO
2. Create a genuine log file for all activity instead of simply printing to the console
3. Perform a single check of every file in the monitored folder on program start so that files that are improperly named and are already present in the folder are renamed, not just files that are added/changed after the program has started running.
4. Improve the robustness of the regular expressions used to identify improperly named folders. I would also recommend a lot more tests surrounding this particular feature. The regular expressions used throughout folder-monitor are the "bare minimum" to get it working and could be made much more robust.
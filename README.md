# folder-monitor

Folder-monitor is a simple program, written in [Clojure](http://clojure.org/) (a modern dialect of LISP), that monitors a specified folder for files whose name match a certain pattern, and renames them appropriately.

**NOTE:** folder-monitor requires at least version 7 of the Java Runtime Environement. You can check which version is currently running on your machine by executing `java -version` in the command line. Anything above 1.7 will work properly.

## Installation

The Clojure languages runs ontop of the JVM. As long as the host machine has Java installed, simply execute the jar file from the command line. All of folder-monitor's dependicies are pre-packaged inside of the jar file.

## Usage

Folder-monitor is packaged as jar file with all of its dependencies included. It accepts only one command line argument: the path of the Sookasa folder.

`$ java -jar folder-monitor-0.1.0-standalone.jar <path to sookasa folder>`

This command works on Linux, OSX, and Windows (as long as the PATH environment variable is configured properly).

**Note:** On Windows, make sure that the "view file extensions" settings is enabled in the file viewer, otherwise it may be difficult to determine if the program is working as intended.

### Examples

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

The bash script [file_overload.sh](test/test_files/file_overload.sh) copies a small (11kb) test image file into the monitored folder x number of times in rapid succession (I tested it up to x = 2000).

The companion script [file_overload_test.sh](test/test_files/file_overload.sh) checks to see if all the files were properly renamed, prints a test success/failure result, and then removes the test files from the folder.

These two scripts were originally implemented as one script, however, I split them into two separate scripts due to incompatibilities between different operating systems. For example, including them as one script worked fine on Linux because the filesystem events are properly supported, however, on OSX the Java Watch Service will sometimes have to fall back on polling (it does so gracefully and silently) and there was no easy way to wait until the folder-monitor application had properly renamed all the files before running the portion of the script that checks the folder. With the two scripts as separate files, the developer can use the first script to bombard the folder with many new files, and then once they've determined a sufficient amount of time has passed for folder-monitor to rename all the files (a few milliseconds in most cases), execute the companion script to make sure all the files were properly renamed and then perform automatical cleanup (removal of the test files).

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
3. Make the two files "file_overload.sh" and "file_overload_test.sh" executable by running the following commands in the terminal: `chmod +x file_overload.sh` and `chmod +x file_overload_test.sh`
4. Execute the file_overload.sh script with two command line arguments. The first should be the absolute path to the monitored folder. The second should be the number of times the test file gets copied into the Sookasa folder.
5. Once the files have been copied, execute the file_overload_test.sh script with the exact same command line arguments used in the step above.

![Folder-Monitor Stress Tests](resources/stress_tests.png)

## Development

If you want to develop the folder-monitor program further, the easiest way is to install and use [Leiningen](http://leiningen.org/). With Leiningen installed, changing the source code is easy:

1. Clone this repo
2. Make any modifications that you like to core.clj
3. Inside the project directory, execute `lein run <options>` to compile and run the program
4. Inside the project directory, execute `lein uberjar` to create an executable jar file that can be distributed to others

## Bugs

### Large Numbers of Small Files

One of the issues I found while stress testing the application is that it will fail if too many files are added to the folder at once. The limit (on my system) occurred when I tried to simultaneously add more than 500 files to the folder in quick succession, with each file being only a single line in length. I believe this issue is caused simply by blocking IO. Since the files are so small, they're added to the folder before the program can process each event and trigger a rename. Eventually an event is triggered and it encounters a blocked thread, causing a silent failure.

I believe this problem could be solved in two different ways:

1. The standard java IO API is blocking, however, as of Java 7 there is a non-blocking equivalent. Several Clojure wrappers exist for this functionality.

2. Clojure has excellent support for concurrency. A "load balancer" of sorts could be setup where one thread listens for events and delegates the actual filesystem operations to a cluster of "worker threads". This would allow the program to support many more events without file IO serving as a bottleneck. In fact, there is a Clojure library [OJO](https://github.com/rplevy/ojo) that implements filesystem event parallelism automatically.

I didn't implement either of these solutions because they both seemed like overkill for the problem at hand. In practical use, this bug doesn't seem to crop up as once the size of the files becomes a few kilobytes, it doesn't matter how many files are added to the Sookasa folder, the folder-monitor program is able to handle the load because the process of renaming a file becomes much quicker than copying a few kilobytes of data.

## Potential Improvements

1. Add support for concurrency / parallelism or non-blocking IO
2. Using a simple text file for logging purposes has the simple advantage of being easy to read and parse/filter using traditional UNIX utilities, however, if I was expecting heavy monitoring / debugging of this program I might consider converting the logging functionality to a simple SQLite database.
3. Perform a single check of every file in the monitored folder on program start so that files that are improperly named and are already present in the folder are renamed, not just files that are added/changed after the program has started running. This would be pretty trivial to add as a feature, 90% of the needed functionality is already written it would simply be a matter of getting a list of all files currently in the directory when the application starts, but I decided not to include it because I wasn't sure if it really fit with the purpose of this program. That being said, I left some comments in the code on how that feature could be implemented if it became necessary.
4. Improve the robustness of the regular expressions used to identify improperly named folders. I would also recommend a lot more tests surrounding this particular feature. The regular expressions used throughout folder-monitor are the "bare minimum" to get it working and could be made much more robust.
5. Even listeners are not automatically removed for folders that are deleted. I didn't investigate the performance implications of this too thoroughly, but it would be something to keep in mind moving forward.
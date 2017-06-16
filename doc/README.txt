1. How to Use Satix
   a. download Satix package and unzip it.
   b. go to the folder deliverable\
   c. modify the file global.properties if necessary under the folder config\
   d. create a test case in xml format such as example\googleSearch\googleSearch.xml
   e. run the following in command line
      satix.bat|sh -c ..\example\googleSearch\global.properties -t ..\example\googleSearch\googleSearch.xml
   f. You will see the running log from the console, or from the log files under
      the folder logs\, and you can see the summary from the folder report\; if
	  there is any error during the testing, you can get the screenshots under
	  the folder screenshots\
      
2. How to Run UT
   a. go to the folder bin\
   b. run UT.bat|sh
   c. you can get all running result such as logs, reports, screenshots under
      the folder deliverable\
	  
3. File Structure
   Satix
     	 ----build.xml           for ant to use
	 ----.project            for the project to be opened in Eclipse
	 ----.classpath          for the project to be opened in Eclipse
	 ----UT                  UT test cases
	 ----src                 source code
	 ----lib                 libraries to be used by satix
	 ----example		 example test case of google search
	 ----doc                 documents including user guide and this file
	 ----deliverable         You can just use the package to use in your
	                         production environment
	 ----config              configuration files  
	 ----build               the place to hold satix.jar built from the
	                         source code
	 ----bin                 scripts
	 ----.settings           for the project to be opened in Eclipse
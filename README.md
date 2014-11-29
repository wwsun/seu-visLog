Server Log Visualization
==========

##Prerequisties

To rebuild this project, you'll need to have the following:
	
- Maven
- JDK 1.7 (1.8 is not recommended)
- MongoDB (least version)

##Project Introduction
visLog is a visualization project for web server logs. The project is a cooperation project with Focus Technology (China) for the server log analysis of www.made-in-china.com.

##Running

1. Before you start the project you should connect to the MongoDB cluster first
2. Using the following command to run the project in the project root folder

		mvn compile exec:java -Dexec.mainClass=course.BlogController

3. Open your browser, and visit the link of:
		
		http://localhost:8082/

##Team
The Data&Intelligence Lab, Southeast University (Nanjing)

- [Weiwei SUN](http://wwsun.me)
- Minglu SHAO

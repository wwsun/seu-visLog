Server Log Visualization - RESTful version
==========

##Prerequisties

To rebuild this project, you'll need to have the following:
	
- Maven
- JDK 1.7 (1.8 is not recommended)
- MongoDB (least version)
- GlassFish

##Project Introduction
visLog is a visualization project for web server logs. 
The project is a cooperation project with Focus Technology (China) 
for the server log analysis of www.made-in-china.com.

The project architecture is:

- Database: MongoDB 3.0 clusters
- Back-end: RESTful Web Services based on Jersey, running on GlassFish
- Front-end: Single Page Application based on AngularJS

##Running(development)

1. Start the application in Intellij: `shift` + `F10`
2. or you can deploy the `war` file to the GlassFish directly
3. Consume the RESTful services via:


##Team
The Data&Intelligence Lab, Southeast University (Nanjing)

- [Weiwei SUN](http://wwsun.me)
- Minglu SHAO

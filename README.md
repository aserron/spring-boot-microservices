# spring-boot-microservices

<h2>SprintBoot2 based Microservices Demo.</h2>

<p>
First, thanks for you time and I hope you find this work interesting, please feel free to contact me for any comment, question or inquiry on it.
</p>

<h3>Production Notes</h3><p>
The solution presented here was coded as a challenge test & proof concept for a respected contractor.
One week used for getting in touch with the technologies requested, two weeks for the development process itself.
NetBeans, Eclipse & STS were used for code quality, debugging and visualize the application structure and functionality.
Other tools where used to test the REST interface for expected behaviors.</p>


<h3>The challenge as a great technology & development proof of concept.</h3>

<p>It’s possible & will be extended, polished, even feature addition are planned as the demo itself reflect the status at the actual presentation of the development challenge result.

To this date, the code reflect the result without code cleanning, fixes or features added.
The documentation part of the delivery package details every aspect of the application provided.
</p>

<h3>Required Technologies</h3>
<ul>
<li>JAVA 8 / JDK 8
<li>MYSQL 8 with updated JConnector
<li>Maven 3+
</ul>

The demos are 2 maven projects, it should not matter if they are imported on any mayor IDE, or compiled from console as long as the compatible stack is used (jdk8, mvn 3, mysql8 + jconnector).

Project settings are set for compile target java 1.8, usign jdk8, debug errors, utf-8.
Some maven optimizations are used running from Netbeans (by own config)

* Project tested usign Netbeans (checkout,run each) at this date.


<h3>Usage Guide</h3>
<ul>
<li> unpack to an empty folder
<li> run the create sql script 
<li> add sql user.. admin:12345  with full rigths to the schema
<li> within a console, run the bat, that will open 2 powershell consoles running maven with spring-boot plugin. 
<li> Expected: 2 open spring-boot applications with embed jetty server
</ul>
<br>
<h4>Documentation notes.</h4>
<p>Docs folder contains some usefull informations like
<ul>
 <li>Project definition, challenge description
 <li>REST api reference (actions, urls, params)
 <li>Usage Notes
 <li>Project documentation, delivery report, with detailed production notes (some very detailed insigths)
</ul>
</p>



<h3>Changes</h3>
<ul> 
 <li>8/28/2018: Updated to Spring Boot 2.1.0 M2</li>
 <li>8/28/2018: Created repo with tested project files</li>
</ul>

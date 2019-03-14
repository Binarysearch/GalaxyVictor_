cd ./back
mvn clean 
mvn package
cp target/galaxyvictor.war C:/tomcat/webapps/ROOT.war
cd ..
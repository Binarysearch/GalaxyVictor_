cd front
ng build --prod
cp dist/front/*.* ../back/src/main/webapp/
cd ../back
mvn clean 
mvn package
cp target/galaxyvictor.war C:/tomcat/webapps/ROOT.war
cd ..
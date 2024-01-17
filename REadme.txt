
SQL Lite DB Login and Registeration 
https://www.youtube.com/watch?v=mPhqDzO7PUU

https://github.com/enamul95/QRCode
https://stackoverflow.com/questions/46276890/android-scan-barcode-from-camera-with-google-barcode-api-what-is-missing
https://stackoverflow.com/questions/53279989/with-api-28-and-androidx-appcompat-library-project-says-appcompatactivity-sy

https://stackoverflow.com/questions/36514574/android-studio-how-to-make-a-2-column-linearlayout

https://stackoverflow.com/questions/51957944/android-view-viewonunhandledkeyeventlistener

https://stackoverflow.com/questions/51240223/adb-exe-is-obsolete-and-has-serious-performance-problems
https://www.geeksforgeeks.org/java-program-for-quicksort/

https://forums.docker.com/t/how-to-access-docker-container-from-another-machine-on-local-network/4737/3
https://blog.oddbit.com/post/2014-08-11-four-ways-to-connect-a-docker/
		http://192.168.1.4:9998


REgister the App , uses in Builte SQL Lite DB



FOR Image scanning from LOCLAL PC connection 
Starte adb reverse tcp:9998 tcp:9998 

and aalso connect using USB Cable 

Start the Docker Image on the PC 

docker pull apache/tika:1.24
docker pull apache/tika:1.24-full

docker run -it --name tika-server-ocr-ip --net=host  -d -p 9998:9998  apache/tika:1.24-full  
docker run -it --name tika-server-ocr -d -p 9998:9998  apache/tika:1.24-full
adfb429b692bff406ca7734fac411757933333ffa2229a63c7d87b00be4aecdc

docker run -it --name tika-server-ip --net=host -d -p 9997:9998  apache/tika:1.24

docker run -it --name tika-server -d -p 9997:9998  apache/tika:1.24

a680acfffed73b9a9dffbcbcf12e0f0d4f4da34431a921270106ece853385e53

Here is a list of the storage locations of the docker images on different operating systems:
Ubuntu: /var/lib/docker/
Fedora: /var/lib/docker/
Debian: /var/lib/docker/
Windows: C:\ProgramData\DockerDesktop.




CHECK the DCOKER Image Uplaod using PUT to CURL 

WOKS 
curl -X PUT --data-binary @"ListofItems.jpg" http://localhost:9998/tika 	 --header "Content-type: image/jpeg"
Suddenly NICE
Suddenly SAD
What it it is to
MAKE you RISE

USE this in CODE 
  172.31.64.1  tika-server-ocr-ip  --net=host
curl -X PUT --data-binary @"ListofItems.jpg" http://192.168.1.4:9998/tika 	 --header "Content-type: image/jpeg"
curl -X PUT --data-binary @"ListofItems.jpg" http://172.31.64.1:9998/tika 	 --header "Content-type: image/jpeg"


Windsows BATCH cripts 

https://github.com/npocmaka/batch.scripts/blob/master/hybrids/jscript/imageProcessing/scale.bat

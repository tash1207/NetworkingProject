#cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ubuntu@" $2}' | xargs -I {} ssh {} -i ~/.ssh/schoolProject.pem 'git clone  -b amazon  https://github.com/tash1207/NetworkingProject.git' ; done
# cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ubuntu@" $2}' | xargs -I {} ssh {} -i ~/.ssh/schoolProject.pem 'sudo apt-get install make' ; done
# cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ubuntu@" $2}' | xargs -I {} ssh {} -i ~/.ssh/schoolProject.pem 'sudo apt-get -y install openjdk-7-jdk' ; done
 cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ubuntu@" $2}' | xargs -I {} ssh {} -i ~/.ssh/schoolProject.pem 'sudo killall java' ; done
 cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ubuntu@" $2}' | xargs -I {} ssh {} -i ~/.ssh/schoolProject.pem 'sudo killall javac' ; done
 cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ssh ubuntu@"$2 " -i ~/.ssh/schoolProject.pem \"cd NetworkingProject; git pull origin amazon\""}' | sh  ; done
 cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ssh ubuntu@"$2 " -i ~/.ssh/schoolProject.pem \"cd NetworkingProject; make clean \""}' | sh  ; done
 cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ssh ubuntu@"$2 " -i ~/.ssh/schoolProject.pem \"cd NetworkingProject; make bin \""}' | sh  ; done
cat PeerInfo.cfg | while read a; do echo $a | awk '{print "ssh -nf ubuntu@"$2 " -i ~/.ssh/schoolProject.pem \"cd NetworkingProject; make run peerid=" $1 "  \""}' | sh  ; sleep 6; done

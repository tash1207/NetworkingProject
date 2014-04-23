bin:
	mkdir bin
	javac src/com/networking/project/*.java -d bin

clean:
	rm -rf bin
	rm -rf *.log
	rm -rf peer_2
	rm -rf peer_3
	rm -rf peer_4

startRemotePeers:
	java -classpath "./startRemote" StartRemotePeers

run: bin
	java -classpath "./bin" com.networking.project.Main $(peerid)

bin:
	mkdir bin
	javac src/com/networking/project/*.java -d bin

clean:
	rm -rf bin
	rm -rf *.log

startRemotePeers:
	./startRemote.sh

run: bin
	java -Xmx400M -classpath "./bin" com.networking.project.Main $(peerid)

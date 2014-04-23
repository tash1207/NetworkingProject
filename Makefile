bin:
	mkdir bin
	javac src/com/networking/project/*.java -d bin

clean:
	rm -rf bin
	rm -rf *.log

startRemotePeers:
	(cd startRemote && ./compileJava)
	java -classpath "./startRemote" StartRemotePeers

run: bin
	java -classpath "./bin" com.networking.project.Main $(peerid)

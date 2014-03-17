bin:
	mkdir bin

build: bin
	javac src/com/networking/project/*.java -d bin

clean:
	rm -rf bin

run:
	java -classpath "./bin" com.networking.project.Main

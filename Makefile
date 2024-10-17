# Variables
SRC_DIR = src
DIST_DIR = dist
MANIFEST = $(DIST_DIR)/manifest/MANIFEST.MF
JAR_NAME = part1.jar
MAIN_CLASS = LexicalAnalyzer
INPUT_FILE = ../Euclid.gls
TEST_FILES = test/test1.txt test/test2.txt test/test3.txt test/test4.txt

# Default target
all: compile jar run

# Compile the Java source files
compile:
	cd $(SRC_DIR) && jflex LexicalAnalyzer.flex
	cd $(SRC_DIR) && javac *.java

# Create the JAR file
jar: compile
	cd $(SRC_DIR) && jar cfm ../$(DIST_DIR)/$(JAR_NAME) ../$(MANIFEST) *.class

# Run the program using the JAR
run:
	cd $(SRC_DIR) && java -jar ../$(DIST_DIR)/$(JAR_NAME) $(INPUT_FILE)

# Run tests
test:
	@for testfile in $(TEST_FILES); do \
		echo "Running test on $$testfile..."; \
		java -cp $(SRC_DIR) $(MAIN_CLASS) $$testfile; \
	done

# Clean .class files & JAR
clean:
	rm -rf $(SRC_DIR)/*.class $(DIST_DIR)/*.jar

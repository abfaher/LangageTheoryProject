# Variables
SRC_DIR = src
DIST_DIR = dist
MANIFEST = $(DIST_DIR)/manifest/MANIFEST.MF
JAR_NAME = part1.jar
MAIN_CLASS = LexicalAnalyzer
INPUT_FILE = ./Euclid.gls
TEST_FILES = test/LongComment.gls test/ShortComment.gls test/CombinedComments.gls test/WhiteSpace1.gls test/WhiteSpace2.gls test/ProgName.gls

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
	@for input_file in $(INPUT_FILE); do \
		echo "Running program on $$input_file..."; \
		cd $(SRC_DIR) && java -jar ../$(DIST_DIR)/$(JAR_NAME) ../$$input_file; \
	done

# Run tests
test: jar
	@for test_file in $(TEST_FILES); do \
		echo "Running test on $$test_file..."; \
		$(MAKE) run INPUT_FILE=$$test_file; \
	done

# Clean .class files & JAR
clean:
	rm -rf $(SRC_DIR)/*.class $(DIST_DIR)/*.jar

# Variables
SRC_DIR = src
DIST_DIR = dist
MANIFEST = $(DIST_DIR)/manifest/MANIFEST.MF
JAR_NAME = part2.jar
MAIN_CLASS = Main
LATEXT_OUTPUT_DIR = latex
TEST_FILES = test/LongComment.gls test/ArithTest.gls test/ErrorTest.gls test/Euclid.gls

# Default target
all: compile jar run

# Compile the Java source files
compile:
	cd $(SRC_DIR) && javac *.java

# Create the JAR file
jar: compile
	cd $(SRC_DIR) && jar cfm ../$(DIST_DIR)/$(JAR_NAME) ../$(MANIFEST) *.class

# Run the program using the JAR
run:
	@ARGS_LENGTH=`echo $(ARGS) | wc -w`; \
	if [ $$ARGS_LENGTH -eq 1 ]; then \
		echo "Running without -wt option"; \
		INPUT_FILE=`echo $(ARGS) | awk '{print $$1}'`; \
		echo "Running program on $$INPUT_FILE..."; \
		cd $(SRC_DIR) && java -jar ../$(DIST_DIR)/$(JAR_NAME) ../$$INPUT_FILE; \
	elif [ $$ARGS_LENGTH -eq 3 ]; then \
		echo "Running with -wt option"; \
		mkdir -p $(LATEXT_OUTPUT_DIR); \
		INPUT_FILE=`echo $(ARGS) | awk '{print $$1}'`; \
		OUTPUT_FILE=`echo $(ARGS) | awk '{print $$3}'`; \
		echo "Running program on $$INPUT_FILE..."; \
		echo "Running program on $$OUTPUT_FILE..."; \
		cd $(SRC_DIR) && java -jar ../$(DIST_DIR)/$(JAR_NAME) -wt ../$(LATEXT_OUTPUT_DIR)/$$OUTPUT_FILE ../$$INPUT_FILE; \
	else \
		echo "Usage: make run ARGS='<input file> [-wt <output file>]"; \
	fi

# Run tests
test: jar
	@for test_file in $(TEST_FILES); do \
		echo "Running test on $$test_file..."; \
		cd $(SRC_DIR) && java -jar ../$(DIST_DIR)/$(JAR_NAME) ../$$test_file; \
	done

# Clean .class files
clean:
	rm -rf $(SRC_DIR)/*.class
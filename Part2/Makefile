# directories
SRC_DIR = src
BIN_DIR = bin
DIST_DIR = dist
TEST_DIR = test

# Java compiler and flags
JAVAC = javac
JAVA = java
JAR = jar
JAVAC_FLAGS = -d $(BIN_DIR)

#Main class and jar file
MAIN_CLASS = Main
JAR_FILE = $(DIST_DIR)/part2.jar

# source files
JAVA_FILES = $(wildcard $(SRC_DIR)/*.java)
CLASS_FILES = $(patsubst $(SRC_DIR)/%.java,$(BIN_DIR)/%.class,$(JAVA_FILES))

# get all arguments after the target for run command
RUN_ARGS = $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

# default target
all: compile jar

#create necessary directories
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

$(DIST_DIR):
	mkdir -p $(DIST_DIR)

#compile Java files
compile: $(BIN_DIR)
	$(JAVAC) $(JAVAC_FLAGS) $(JAVA_FILES)

# create JAR file
jar: compile $(DIST_DIR)
	$(JAR) cfe $(JAR_FILE) $(MAIN_CLASS) -C $(BIN_DIR) .

# run program with file argument
run: jar
	@if [ "$(RUN_ARGS)" = "" ]; then \
		echo "Usage: make run [WT=out.tex] file.gls"; \
		exit 1; \
	fi
	@if [ "$(WT)" != "" ]; then \
		java -jar $(JAR_FILE) -wt $(WT) $(RUN_ARGS); \
	else \
		java -jar $(JAR_FILE) $(RUN_ARGS); \
	fi

# run tests on all .gls files in test directory
test: jar
	@for file in $(TEST_DIR)/*.gls; do \
		if [ -f "$$file" ]; then \
			echo "Testing $$file..."; \
			if [ "$(WT)" != "" ]; then \
				java -jar $(JAR_FILE) -wt $(WT) $$file; \
			else \
				java -jar $(JAR_FILE) $$file; \
			fi; \
		fi \
	done

#clean build artifacts
clean:
	rm -rf $(BIN_DIR)
	rm -rf $(DIST_DIR)

# handle arguments
%:
	@:

.PHONY: all compile jar run test clean
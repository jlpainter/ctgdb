The following shell scripts are included to assist in building and running the pipeline.

1. download_ctdata.sh

	Description: Downloads CT data files (requires wget). The data will
                     be downloaded and expanded in the src/main/data folder.
	Usage: ./download_ctdata.sh
	Note: Ensure wget is installed on your system.

2. build_dataparser.sh

	Description: Compiles and packages the data parser into a JAR file.
	Usage: ./build_dataparser.sh
	Note: This script assumes all dependencies are already installed and configured.

3. run-parser.sh

	Description: Executes the data parser using the built JAR file.
	Usage: ./run-parser.sh
	Note: This script should be run after successfully building the parser.


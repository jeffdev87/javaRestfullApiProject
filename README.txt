*** Project settings

1) Database System: SQLite

2) Database name: moviescript

3) Builder/Runner tool: Maven

*** Instructions to execute:

1) Open the terminal

2) Extract the project source into a folder of your choice (e.g. /home/user/documents)

3) Go to the previously selected folder and navigate to: 

	cd ./jersey-service

4) Execute the following script to create the database:

	./sqliteScript.sh 

5) Confirm that a new file named "moviescript.db" was created in the directory

6) Clean and build the project by running the following command:

	mvn clean compile

7) Execute the project by running the following command:

	mvn exec:java

* Instructions to perform tests using UNIX command curl:

	Obs1: Local server addres: localhost:8080/myapp
	Obs2: Open a new terminal to execute the commands below

1) POST moviescript/script/{name}

Description:
	Store a script into the database.
	Where <name> is the name of the script. This name will be used to identify the script in the database.
	You should specify the source of the data to be sent in the POST request.

Sample:
	# Read the file screenplay.txt in the current directory and sent as a POST request. The name of the newly created script
	is full1.
	$curl -H "Content-Type: text/plain" -X POST --data-binary "@./screenplay.txt" localhost:8080/myapp/moviescript/script/full1
	
Result:
	JSON string with a successfull or error message.

2) GET moviescript/settings/{scriptId}

Description:
	Retrieve all settings from the specified script.
	Where <scriptId> is the identifier of the script.

Sample:
	# Retrieve all settings from the script identified by 1
	curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/settings/1
	
Result:
	JSON string with the requested data or an empty array [].

3) GET moviescript/settings/{scriptId}/{settingId}

Description:
	Retrieve the setting settingId from the specified script.
	Where <scriptId> is the identifier of the script.
	      <settingId> is the identifier of the setting.	

Sample:
	# Retrieve the setting 2 from the script identified by 1
	curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/settings/1/2
	
Result:
	JSON string with the requested data or an empty array [].


4) GET moviescript/characters/{scriptId}

Description:
	Retrieve all characters from the specified script.
	Where <scriptId> is the identifier of the script.

Sample:
	# Retrieve all characters from the script identified by 1
	curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters/1
	
Result:
	JSON string with the requested data or an empty array [].

5) GET moviescript/characters/{scriptId}/{characterId}

Description:
	Retrieve the character characterId from the specified script.
	Where <scriptId> is the identifier of the script.
	      <characterId> is the identifier of the character.	

Sample:
	# Retrieve the character 2 from the script identified by 1
	curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters/1/2
	
Result:
	JSON string with the requested data or an empty array [].


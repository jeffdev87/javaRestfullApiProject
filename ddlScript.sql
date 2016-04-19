DROP TABLE IF EXISTS script;
DROP TABLE IF EXISTS character;
DROP TABLE IF EXISTS setting;
DROP TABLE IF EXISTS settingCharacter;
DROP TABLE IF EXISTS characterScriptWordCount;

CREATE TABLE script (
   id INTEGER PRIMARY KEY autoincrement NOT NULL,
   script_name TEXT UNIQUE NOT NULL
);

CREATE TABLE character (
   id INTEGER PRIMARY KEY autoincrement NOT NULL,
   character_name TEXT UNIQUE   
);

CREATE TABLE setting (
   id INTEGER PRIMARY KEY autoincrement NOT NULL,
   setting_name TEXT UNIQUE   
);

CREATE TABLE settingCharacter (
   script_id INTEGER NOT NULL,
   setting_id INTEGER NOT NULL,
   character_id INTEGER NOT NULL,
   
   PRIMARY KEY (script_id, setting_id, character_id),

   FOREIGN KEY (script_id) REFERENCES script(id),
   FOREIGN KEY (setting_id) REFERENCES script(id),
   FOREIGN KEY (character_id) REFERENCES character(id)		 
);

CREATE TABLE characterScriptWordCount (
   script_id INTEGER NOT NULL,
   setting_name TEXT NOT NULL,
   character_id INTEGER NOT NULL,
   word TEXT NOT NULL,
   counter INT DEFAULT 0,
   
   PRIMARY KEY (script_id, setting_name, character_id, word),
   FOREIGN KEY (script_id, setting_name, character_id) REFERENCES setting(script_id, setting_name, character_id)
);


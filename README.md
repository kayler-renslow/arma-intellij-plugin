# Arma Intellij Plugin
Arma games are created by [Bohemia Interactive](https://www.bistudio.com/) and Intellij is created by [Jetbrains](https://www.jetbrains.com/). Arma Intellij Plugin was created by Kayler Renslow. The plugin and it's creator aren't affiliated with Jetbrains or Bohemia Interactive.

### License
This project is open source. Do what you want with the code. Sell it, modify it, claim you made it, or anything else - I don't care. Just make sure you are respecting the rights of Jetbrains and Bohemia Interactive.

### How to Clone and Edit Source for Arma Intellij Plugin
1. You should know how to write Java and you should have experience using [Intellij](https://www.jetbrains.com/idea/).
2. Read the official documentation for Plugin development with Intellij. This will tell you how to prepare for a new Plugin project. Visit the docs [here](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html).
3. By now you should have Intellij set up and a new project. Now go to the Main Menu Bar, then "VCS", then "Checkout", then "Github". Clone the Arma Intellij Plugin source. Now you will want to link that source code to the plugin project you created.
4. Done

### Some Information on the Source Code
*None* of the documentation files for SQF commands or BIS functions is inside the repo. This is intentional and keeps the repo size smaller. You may need to create a few directories to get the plugin started and possibly a few blank files with command names as the name (e.g. createVehicle command's doc file will be located in "/com/kaylerrenslow/a3plugin/lang/sqf/raw_doc/commands-doc/createVehicle" and it has no file extension like .txt or .html).
**Alternatively** you could extract the documentation files out of the Arma.Intellij.Plugin.jar and place them inside your project.

Also, the Lexer and Parser is generated and is not committed either. You will need to find Header.bnf, Header.flex, SQF.bnf, and SQF.flex and run the generators. For generating .flex files, right click and click "Run JFlex generator". To generate .bnf files, right click and click "Generate Parser Code". 

### Useful links
Intellij Plugin documentation:
* Code examples: http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html
* More technical documentation: http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support.html

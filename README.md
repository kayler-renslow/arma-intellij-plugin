# Arma Intellij Plugin

### License
Arma games are created and owned by [Bohemia Interactive](https://www.bistudio.com/) and Intellij is created and owned by [Jetbrains](https://www.jetbrains.com/). Arma Intellij Plugin was created by Kayler Renslow. The plugin and it's creator aren't affiliated with Jetbrains or Bohemia Interactive. This project is licensed under the [MIT License](https://en.wikipedia.org/wiki/MIT_License). You don't need to contact me if you want to create derivatives or publish the project elsewhere.

### Key Features
* Syntax checking for Header files (*.h, *.hh, *.sqm, *.ext, *.hpp) and SQF files
* Description.ext function lookup
* Documentation 'tags' which can link to command wiki documentation without opening the browser.
* Automatic plugin update checking (as of 1.0.3)
* Finding usages of variables
* Syntax highlighting
* Rename refactoring for functions and variables
* Seamless config function creation (alt + insert)
* Auto-completion (ctrl + space)
* An "Arma Color" picker dialog which allows to convert HEX or RGB to Arma's color format (black -> [0,0,0,1])
* Wiki documentation on all commands and BIS functions via ctrl+Q

### Download the Jar
Downloading the .jar is optional (See Install from JetBrains Plugin Repository below.).
* GitHub: https://github.com/kayler-renslow/arma-intellij-plugin/releases
* JetBrains Plugin Repo: https://plugins.jetbrains.com/idea/plugin/9254-arma-intellij-plugin  
**NOTE: both GitHub and JetBrains Repo include the same .jar files.** You only need one plugin jar from one of the repositories to install.

### Installation
Prerequisites
* Get [IntelliJ IDEA](https://www.jetbrains.com/idea/)

To install the plugin, you can [install it from disk](https://www.jetbrains.com/help/idea/2016.3/installing-plugin-from-disk.html) or install from [JetBrains Plugin Repository](https://www.jetbrains.com/help/idea/2016.3/installing-updating-and-uninstalling-repository-plugins.html).

#### Install From Disk (Need the .jar)
* Step 1: In IntelliJ Settings Dialog, click on "Plugins", click **Install plugin from Disk**.
* Step 2: Locate the "Arma Intellij Plugin.jar" (file name may have version info like v1.0.7)
* Step 3: Click OK button when you located the plugin jar and then Restart IntelliJ IDEA
* Step 4: You're good to go!

#### Install From JetBrains Plugin Repository
* Step 1: In IntelliJ Settings Dialog, click on "Plugins", click **Browse Repositories**.
* Step 2: Search for "Arma Intellij Plugin"
* Step 3: Click Install and then Restart IntelliJ IDEA
* Step 4: You're good to go!



### Video Walkthrough
https://www.youtube.com/watch?v=BOkfMCutb7U

### Source Code
#### How to Clone and Edit Source for Arma Intellij Plugin
1. You should know how to write Java and you should have experience using [Intellij](https://www.jetbrains.com/idea/).
2. Read the official documentation for Plugin development with Intellij. This will tell you how to prepare for a new Plugin project. Visit the docs [here](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html).
3. By now you should have Intellij set up and a new project. Now go to the Main Menu Bar, then "VCS", then "Checkout", then "Github". Clone the Arma Intellij Plugin source. Now you will want to link that source code to the plugin project you created.
4. Done

#### Some Information on the Source Code
*None* of the documentation files for SQF commands or BIS functions is inside the repo. This is intentional and keeps the repo size smaller. You may need to create a few directories to get the plugin started and possibly a few blank files with command names as the name (e.g. createVehicle command's doc file will be located in "/com/kaylerrenslow/a3plugin/lang/sqf/raw_doc/commands-doc/createVehicle" and it has no file extension like .txt or .html).
**Alternatively** you could extract the documentation files out of the Arma.Intellij.Plugin.jar and place them inside your project.

Also, the Lexer and Parser is generated and is not committed either. You will need to find Header.bnf, Header.flex, SQF.bnf, and SQF.flex and run the generators. For generating .flex files, right click and click "Run JFlex generator". To generate .bnf files, right click and click "Generate Parser Code". 

### Useful links
Intellij Plugin documentation:
* Code examples: http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html
* More technical documentation: http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support.html

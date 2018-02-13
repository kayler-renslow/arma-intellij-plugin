**Version:** 2.0.0  
**Release Date:** February 12, 2018

**Added**  
* Breadcrumbs to SQF
* full type checking for SQF (including arrays)
* full syntax checking for SQF
* case insensitivity for all commands and variables in SQF
* a better preprocessor for SQF
* fully implemented preprocessor for Header/Config files
* better addon support (config.h files are now parsed)
* configurable syntax highlighting for SQF control structure commands (if, then, etc)
* implemented tests for plugin builds to ensure code consistency and reliability

**Changed**  
* Rewrote the plugin for optimization and code clarity purposes
* removed Arma Color Picker since there is Arma Dialog Creator
* all SQF command documentation now has the Notes section from the wiki
* updated all command definitions and documentation to 1.78
* heavily optimized code inspections
* The Variable and Magic variable icons have changed to match default syntax highlighting colors

**Known Issues**
* \#include paths don't quite work when using \ as the path starter.
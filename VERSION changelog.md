**Version:** 2.0.2 (NOTE TO SELF: update plugin.xml version)  
**Release Date:** June 21, 2018

**Added**  
* auto completion for literals (ctrl+space on disableAI will reveal things like "AUTOCOMBAT")

**Changed**  
* removed duplicate vars from auto completion
* prioritized auto completion such that literals are always first, config functions are second, vars are third, and commands are last. 

**Fixed**  
* scenario where config functions couldn't be located when no directory was marked as sources root.
  This was resolved by assuming the parent directory of the module .iml file was the src root.
* https://github.com/kayler-renslow/arma-intellij-plugin/issues/73

**Known Issues**
* 
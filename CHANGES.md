**Added**  
* auto completion for literals (ctrl+space on disableAI will reveal things like "AUTOCOMBAT")

**Changed**  
* removed duplicate vars from auto completion
* prioritized auto completion such that literals are always first, config functions are second, vars are third, and commands are last. 

**Fixed**  
* scenario where config functions couldn't be located when no directory was marked as sources root.
  This was resolved by assuming the parent directory of the module .iml file was the src root.

**Notes**
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
* WE NEED TO DO THIS: https://github.com/kayler-renslow/arma-intellij-plugin/issues/45
* What if we automatically mark any Addons in the current module that the user has (they are developing an addon) as a reference directory?
    We could reuse code this way. People could also reference other projects if we had multiple reference directories without needing to copy and paste stuff everywhere

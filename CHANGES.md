**Added**  
*  

**Changed**  
* 

**Fixed**  
* if statement tests were wrong. Fixed them
* added Location type to == and != syntax xml
* made command expressions return the last type that consumes all of the types rather than always the first
    * if true then {}; should return ANYTHING since then consumes if true
    * count _x + 1 == 1 should return a BOOLEAN and not a NUMBER

**Notes**
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
* We need to cache macros from preprocessed header files. What we should do instead of storing the preprocessed config files is store the de-binarized versions.
then the plugin can cache the preprocessed versions in a temp folder and store the macro definitions as well. then we can have a "re-process" button and it will re-preprocess.
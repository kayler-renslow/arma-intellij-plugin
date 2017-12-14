**Added**  
*  

**Changed**  
* 

**Fixed**  
* {+1.5} was thinking it was  {} + 1.5 and the } was a syntax error
* If CodeType had a _VARIABLE or ANYTHING return type, it was assumed to not be equal to a CodeType that had any type (NUMBER, ANYTHING, CONFIG, etc)
* Made CODE hardEqual to CodeType by overriding BaseType's isHardEqual method. We did this because putting CODE in polymorphic types was causing CodeTypes with different return types to be equal.

**Notes**
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
* We need to cache macros from preprocessed header files. What we should do instead of storing the preprocessed config files is store the de-binarized versions.
then the plugin can cache the preprocessed versions in a temp folder and store the macro definitions as well. then we can have a "re-process" button and it will re-preprocess.
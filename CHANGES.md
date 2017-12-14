**Added**  
*  

**Changed**  
* 

**Fixed**  
*

**Notes**
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
* We need to cache macros from preprocessed header files. What we should do instead of storing the preprocessed config files is store the de-binarized versions.
then the plugin can cache the preprocessed versions in a temp folder and store the macro definitions as well. then we can have a "re-process" button and it will re-preprocess.
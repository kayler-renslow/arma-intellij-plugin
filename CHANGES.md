**Added**  
* generic error reporting for ArmaAddonsIndexingCallback interface

**Changed**  
* 

**Fixed**  
* IndexArmaAddonsStatusDialog:
    * made error and warning count display correctly at start
* ArmaTools CfgConvert: wasn't converting the bin file to a de-binarized file 

**Notes**
* scope works, but it doesn't work for deep nested scopes
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
* we should still clean up all temp directories from pbo extracting even if the indexing throws an exception!
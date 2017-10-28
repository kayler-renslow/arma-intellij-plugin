**Added**  
* ArmaAddonsManger forwarding thread logger now writes the year, month, and day

**Changed**  
* 

**Fixed**  
* 

**Notes**
* scope works, but it doesn't work for deep nested scopes
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
* we should still clean up all temp directories from pbo extracting even if the indexing throws an exception!
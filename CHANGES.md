**Added**  
*

**Changed**  
* made config function parsing faster by not using the TransactionGaurd thing before. Instead of writing all updated header
files to disk, we can just read them from RAM or from disk (IntelliJ decides with its VFS system)

**Fixed**  
* 

**Notes**
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress

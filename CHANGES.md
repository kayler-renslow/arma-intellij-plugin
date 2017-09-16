**Added**  
* 

**Changed**  
* optimized SQFScope.getPrivateVarInstances(true) by checking the file scope instead of recursively checking
  every containing scope which is super redundant

**Fixed**  
* wrong scopes for each _a: `private _a={private _a = 0;};`

**Notes**
* we need to find references for global vars and commands across all files
    * we already have a reference contributor for global vars
* fix renaming variable
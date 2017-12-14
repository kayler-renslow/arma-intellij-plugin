### Features
* check if function exists when doing call or spawn
* renaming files will rename them in includes and if a SQF function, will rename the class decl
* for header preprocessors, you will have to implement the lexer both times. However, use the current SQF version for both. Then have a shared TokenType called preprocessor
    * We would also have a shared annotator that would look for these tokens and provide proper coloring and error checking
    * We need to figure out how to provide references for them though
* header lexer could not match input when doing analyze->inspect code


* in the future, the type/syntax checker should be able to detect when a Cfg* entry is needed and search description.ext for the classnames

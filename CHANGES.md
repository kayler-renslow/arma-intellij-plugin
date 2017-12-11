**Added**  
* 

**Changed**  
* made the lexer no longer make a command in a macro function call as a MACRO_FUNC lexer token. Instead, it splits it into a COMMAND_TOKEN
  and then re-lexes everything between, and including, the parenthesis. 
  For example, createVehicle(test) will return a COMMAND_TOKEN, LPAREN token, VARIABLE token, and RPAREN token.
  However, doing createVeh(test) will return only a MACRO_FUNC token 

**Fixed**  
* 

**Notes**
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
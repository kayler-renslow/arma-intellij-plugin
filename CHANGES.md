**Added**  
* more implementation to SQFSyntaxChecker

**Changed**  
* changed SQFUnaryExpression ([+-] expression) to SQFSignedExpression. Then, SQFUnaryExpression is now interface used for any unary expression

**Fixed**  
* 

**Notes**
* scope works, but it doesn't work for deep nested scopes
* ArmaAddonsManager needs to finish ArmaAddonsIndexingCallback.java utilization by incrementing an addon's current work progress and total work progress
* We should test when ANYTHING type for addition adn such will throw an error
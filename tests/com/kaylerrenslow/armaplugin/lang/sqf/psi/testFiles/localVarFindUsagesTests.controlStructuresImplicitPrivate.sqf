/*
Note that variable names are case insensitive.

For this file:
    _sharedVar can be referenced anywhere in file.
    The following vars have multiple scopes; one inside their respective control structure and the other after the structure:
        _ifVar
        _forVar
        _whileVar
        _switchVar
*/
_sharedVar = 1;
hint format ["%1", _sharedVar];

//FIND_USAGE_SECTION:START IF
if true then {
    _ifVar = 1;
    _onlyInIfStatement = 6;
    hint format ["%1, %2", _IFVAR, _sharedVar];
};
//FIND_USAGE_SECTION:END IF

_ifVar = 2;
hint format ["%1", _IFVAR];

//FIND_USAGE_SECTION:START FOR
for[{_forVar = 1;}, {_forVar < 2}, {_forVar = _forVAR + 1}] do {
    [_ifVar, _fORVar];
    _onlyInForStatement = 7;
};
//FIND_USAGE_SECTION:END FOR

//FIND_USAGE_SECTION:START SECOND FOR
for "_forVarInString" from 0 to 10 do {
    hint format["%1", _forVarInString];
};
//FIND_USAGE_SECTION:END SECOND FOR

//FIND_USAGE_SECTION:START WHILE
while (_ifVar > 0) do {
    a = [_WHILEVAR] + [_ifVar, _sharedVar];
};
//FIND_USAGE_SECTION:END WHILE

_forVar = "wowsers";
_whiLEVar = 69;

//FIND_USAGE_SECTION:START SWITCH
switch _forVar do {
    case 0: {
        _switchVar = 2;
    };
    case 1:{
        _switchVar = 3;
    };
    default {
        _switchVar = 0;
    };
};
//FIND_USAGE_SECTION:END SWITCH
hint format["%1 %2", _switchVar, _forVar];
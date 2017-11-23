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

//FIND USAGE TEST HELPER:START IF
if true then {
    _ifVar = 1;
    hint format ["%1, %2", _IFVAR, _sharedVar];
};
//FIND USAGE TEST HELPER:END IF

_ifVar = 2;
hint format ["%1", _IFVAR];

//FIND USAGE TEST HELPER:START FOR
for[{_forVar = 1;}, {_forVar < 2}, {_forVar = _forVAR + 1}] do {
    [_ifVar, _fORVar];
};
//FIND USAGE TEST HELPER:END FOR

//FIND USAGE TEST HELPER:START SECOND FOR
for "_forVarInString" from 0 to 10 do {
    hint format["%1", _forVarInString];
};
//FIND USAGE TEST HELPER:END SECOND FOR

//FIND USAGE TEST HELPER:START WHILE
while (_ifVar > 0) do {
    a = [_WHILEVAR] + [_ifVar, _sharedVar];
};
//FIND USAGE TEST HELPER:END WHILE

_forVar = "wowsers";
_whiLEVar = 69;

//FIND USAGE TEST HELPER:START SWITCH
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
//FIND USAGE TEST HELPER:END SWITCH
hint format["%1 %2", _switchVar, _forVar];
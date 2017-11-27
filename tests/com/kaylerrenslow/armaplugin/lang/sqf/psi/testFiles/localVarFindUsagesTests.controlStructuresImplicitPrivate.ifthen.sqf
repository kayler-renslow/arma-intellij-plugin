
_sharedVar = 1;
hint format ["%1", _sharedVar];

//SCOPE_HELPER:IF:BEGIN
if true then {
    _ifVar = 1;
    _onlyInIfStatement = 6;
    hint format ["%1, %2", _IFVAR, _sharedVar];
};
//SCOPE_HELPER:IF:END

_ifVar = 2;
hint format ["%1", _IFVAR];

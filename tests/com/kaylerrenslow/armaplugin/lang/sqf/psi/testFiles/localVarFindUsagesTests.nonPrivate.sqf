/*
For this file, _var can be referenced anywhere. _var2 also only has 1 usage.
Note that _var is sometimes intentionally not matching the case of "_var"
*/
_var = 1;
_var2 = 0;
for [{_Var = 0;}, {_vAr < 10}, {_VAR = _VAr + 1}] do {
    hint format["%1", _var];
};

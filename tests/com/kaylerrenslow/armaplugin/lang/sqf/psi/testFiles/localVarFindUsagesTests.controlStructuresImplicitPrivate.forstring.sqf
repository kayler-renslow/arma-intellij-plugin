_sharedVar = 1;
hint format ["%1", _sharedVar];


for "_forVarInString" from 0 to 10 do {
    hint format["%1", _forVarInString];
};

hint format["%1 %2", forVarInString, _sharedVar];
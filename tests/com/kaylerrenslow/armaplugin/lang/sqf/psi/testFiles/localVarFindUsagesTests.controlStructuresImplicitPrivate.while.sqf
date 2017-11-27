_sharedVar = 1;
hint format ["%1", _sharedVar];

while (_shared > 0) do {
    a = [_WHILEVAR] + [_sharedVar];
};

_whiLEVar = 69;

hint format["%1 %2", _sharedVar, _whileVar];
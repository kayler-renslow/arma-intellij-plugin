
_sharedVar = 1;
hint format ["%1", _sharedVar];


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

hint format["%1 %2", _switchVar, _sharedVar];
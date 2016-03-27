/*
    This script does absolutely nothing useful.
*/

disableSerialization; //disable the serialization

[] spawn
{
    private["_arr", "_localVar"];

    _localVariable = 'single quote string'
    meaningOfLife = 42;

    if (1==1 and 2==2 && 42==42) then {
        hint "42 is equal to 42";
        _arr = [2e2, 3.1415926535, missionConfigFile];
    };

    _localVar = 2 + 2;

    {
        _x setDamage 1;
    } forEach units group player;

    switch (meaningOfLife) do {
        case 42: { hint "meaning of life is good"; };
        default { hint "meaning of life is wrong"; };
    };

    <commentNote>//NOTE this is a comment note.</commentNote>
};
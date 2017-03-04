### Major
* Fix case sensitive variables. right now, can't refactor all names that don't match the same case
Finding scope for sqf variables, especially with loops
* https://github.com/kayler-renslow/arma-intellij-plugin/issues/39

### Mild
* This is broken again (scope)
    ```
    _i = 100;
    for [{private _i = 0}, {_i < 5}, {_i = _i + 1}] do {};
    hint str _i; // 100
    ```
* private doesn't work very well inside spawn

### Minor
* https://github.com/kayler-renslow/arma-intellij-plugin/issues/37

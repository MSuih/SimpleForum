"use strict";

(function(window){
    var user = document.getElementById("username");
    var pass1 = document.getElementById("pass1");
    var pass2 = document.getElementById("pass2");

    pass2.onchange = function() {
        if (!pass2.value || pass2.value.length < 5)
            pass2.setCustomValidity("Password too short!");
        else if (pass2.value === pass1.value)
            pass2.setCustomValidity("");
        else
            pass2.setCustomValidity("Not the same password!");
    };
    pass1.onchange = function() {
        if (!pass1.value || pass1.value.length < 5)
            pass1.setCustomValidity("Password too short!");
        else
            pass1.setCustomValidity("");
    };
    user.onchange = function() {
        if (!user.value || user.value.length < 3)
            user.setCustomValidity("Username too short!");
        else
            user.setCustomValidity("");
    };
})(window);

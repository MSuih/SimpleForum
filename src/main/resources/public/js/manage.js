"use strict";

(function(window){
    var holder = window.mangementVariables;
    if (!holder)
        console.log("Global variable holder wasn't found!");

    function showCategory(type) {
        return function() {
            var elem = null;
            if (type === "user")
                elem = document.getElementById("userm");
            else
                elem = document.getElementById("catem");
            elem.classList.toggle("disabled");
        };
    }

    function getUserDetails(username, callback) {
        if (!username || username.length < 3) {
            callback(null);
            return;
        }
        var req = new XMLHttpRequest();
        req.onreadystatechange = function() {
            if (req.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (req.status !== 200) {
                callback(false);
                return;
            }
            callback(JSON.parse(req.responseText));
        };
        req.open("GET", holder.rooturl + "user/findbyname/" + username);
        req.send();
    }

    function toggleForms(enable) {
        document.getElementById("username").disabled = enable;
        document.getElementById("usr-verify").value = enable ? "Cancel" : "Check";
        document.getElementById("role").disabled = !enable;
        document.getElementById("usr-disable").disabled = !enable;
        document.getElementById("usr-update").disabled = !enable;
    }

    var editmode = false;
    function userCheck() {
        var field = document.getElementById("username");

        if (editmode) {
            toggleForms(false);
            editmode = false;
            return;
        }
        var text = field.value;
        if (!text)
            return;
        getUserDetails(text, function(returned) {
            if (!returned) {
                field.setCustomValidity(returned === false ? "User was not found!" : "Username is too short!");
                field.onkeydown = function() {
                    field.setCustomValidity("");
                    field.onkeydown = null;
                };
                return;
            }
            holder.userid = returned.id;
            document.getElementById("role").value = returned.type;
            document.getElementById("usr-disable").checked = returned.disabled;
            editmode = true;
            toggleForms(true);
        });
    }

    function updateUser() {
        var req = new XMLHttpRequest();
        req.onreadystatechange = function() {
            if (req.readyState !== XMLHttpRequest.DONE)
                return;
            if (req.status !== 200) {
                showError("Could not update user!\n" +
                        "status" + req.status);
                console.log(req.responseText);
            }
            editmode = false;
            toggleForms(false);
        };
        req.open("PATCH", holder.rooturl
            + "user/" + holder.userid
            + "?role=" + document.getElementById("role").value
            + "&disabled=" + document.getElementById("usr-disable").checked
            + "&" + document.querySelector('meta[name="_csrf"]')
                .getAttribute("content") + "=" // TODO: Why does this work?
            + document.querySelector('meta[name="_csrf_token"]')
                .getAttribute("content"));     // and why doesn't setRequesHeader work?
        req.withCredentials = true;
        req.send();
    }

    function deleteTopic(event) {
        event.preventDefault();
        var elem = getLinkTag(event.target);
        var id = elem.id.substring(1);
        console.log("del t " + id);
        var req = new XMLHttpRequest();
        req.onreadystatechange = function() {
            if (req.readyState !== XMLHttpRequest.DONE)
                return;
            if (req.status !== 200) {
                showError("Could not delete this topic!\n" +
                        "status" + req.status);
                console.log(req.status + " " + req.responseText);
                return;
            }
            elem.parentElement.parentElement.removeChild(elem.parentElement);
        };
        req.open("DELETE", holder.rooturl + "topic/" + id
            + "?" + document.querySelector('meta[name="_csrf"]')
                .getAttribute("content") + "="
            + document.querySelector('meta[name="_csrf_token"]')
                .getAttribute("content"));
        req.send();
    }

    function deleteCategory(event) {
        event.preventDefault();
        var elem = getLinkTag(event.target);
        var id = elem.id.substring(1);
        var req = new XMLHttpRequest();
        req.onreadystatechange = function() {
            if (req.readyState !== XMLHttpRequest.DONE)
                return;
            if (req.status !== 200) {
                showError("Could not delete this category!\n" +
                        "status" + req.status);
                console.log(req.status + " " + req.responseText);
                return;
            }
            elem.parentElement.removeChild(elem);
        };
        req.open("DELETE", holder.rooturl + "category/" + id
            + "?" + document.querySelector('meta[name="_csrf"]')
                .getAttribute("content") + "="
            + document.querySelector('meta[name="_csrf_token"]')
                .getAttribute("content"));
        req.send();
    }

    function addTopic(event) {
        event.preventDefault();
        var elem = getLinkTag(event.target);
        var nodes = elem.parentElement.childNodes;
        var next = false;
        for (var i = 0; i < nodes.length; i++) {
            if (!nodes[i].tagName)
                continue;
            if (nodes[i] === elem) {
                next = true;
                continue;
            }
            if (next && nodes[i].tagName.toUpperCase() === "FORM") {
                nodes[i].classList.toggle("disabled");
                break;
            }
        }
    }

    document.getElementById("cateb").onclick = showCategory("category");
    document.getElementById("userb").onclick = showCategory("user");

    document.getElementById("usr-verify").onclick = userCheck;
    document.getElementById("usr-update").onclick = updateUser;

    for (var elem of document.getElementsByClassName("delete-topic")) {
        elem.onclick = deleteTopic;
    }
    for (var elem of document.getElementsByClassName("delete-category")) {
        elem.onclick = deleteCategory;
    }

    for (var elem of document.getElementsByClassName("create-topic")) {
        elem.onclick = addTopic;
    }
})(window);

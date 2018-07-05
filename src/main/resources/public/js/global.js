"use strict";

(function(window){
    var pagecounter = window.globalVariableHolder;
    if (!pagecounter)
        console.log("No global object found!");

    window.showError = function (text) {
        var elem = document.getElementById("warning");
        if (!elem) {
            elem = document.createElement("div");
            elem.id = "warning";
            document.body.appendChild(elem);
        }
        elem.innerText = text;
        elem.classList.remove("disabled");
        setTimeout(function() {
            elem.classList.add("disabled");
        }, 4000);

    };

    /* Timestamps are sent to browser in ISO format, this functions converts
    those timestampst to human readable "X units ago" format. The real timestamp can
    still be accessed by mouseovering the timestamp. */
    window.timecheck = function() {
        var elements = document.getElementsByClassName("timeunit");
        for (var element of elements) {
            var postdate = new Date(element.innerText);
            var now = new Date();
            var diff = Math.abs(now.getTime() - postdate.getTime());
            if (diff < 1000 * 60) // 1 minute
                element.innerText = "just now";
            else if (diff < 1000 * 60 * 60) // 1 hour
                element.innerText =  parseInt(diff / (1000 * 60)) + " minutes ago";
            else if (diff < 1000 * 60 * 60 * 24) // 1 day
                element.innerText = parseInt(diff / (1000 * 60 * 60)) + " hours ago";
            else if (diff < 1000 * 60 * 60 * 24 * 7) // 1 week
                element.innerText = parseInt(diff / (1000 * 60 * 60 * 24)) + " days ago";
            else if (diff < 1000 * 60 * 60 * 24 * 365) // 1 year
                element.innerText = parseInt(diff / (1000 * 60 * 60 * 24 * 7)) + " weeks ago";
            else
                element.innerText = parseInt(diff / (1000 * 60 * 60 * 24 * 365)) + " years ago";
            element.title = postdate;
        }
    };

    /* If you add a onclick to a link tag, event.source sometimes returns the element inside
     * that link tag. This ensures that a link tag is always returned. */
    window.getLinkTag = function(elem) {
        while (elem.tagName.toUpperCase() !== "A")
            elem = elem.parentElement;
        return elem;
    };

    /* Adds text to position of text cursor. If no cursor exists, adds text to the end of text field. */
    function addTextToField(elem, text) {
        if (elem.selectionStart || elem.selectionStart === 0) {
            var startPos = elem.selectionStart;
            var endPos = elem.selectionEnd;
            elem.value = elem.value.substring(0, startPos)
                + text
                + elem.value.substring(endPos, elem.value.length);
        } else {
            elem.value += text;
        }
    }

    function quoteMessage(event) {
        event.preventDefault();
        var msgholder = window.getLinkTag(event.target).parentElement.parentElement;
        var childs = msgholder.children;
        for (var elem of childs) {
            if (elem.tagName.toUpperCase() === "P") {
                var textfield = document.getElementById("msgbox");
                addTextToField(textfield, "[q=" + msgholder.id.substring(3) + "]" + elem.innerText + "[/q]\n");
                textfield.focus();
            }
        }
    }

    function deleteMessage(event) {
        event.preventDefault();
        var msgholder = window.getLinkTag(event.target).parentElement.parentElement;
        var req = new XMLHttpRequest();
        req.onreadystatechange = function() {
            if (req.readyState !== XMLHttpRequest.DONE)
                return;
            switch (req.status) {
                case 200:
                    msgholder.parentElement.removeChild(msgholder);
                    return;
                case 403:
                    showError("You aren't allowed to delete this message!");
                    return;
                default:
                    showError("Unknown error has occured");
                    console.log(req.status + " " + req.responseText);
                    return;
            }
        };
        req.open("DELETE", pagecounter.baseurl + "../../../../message/" + msgholder.id.substring(3)
            + "?" + document.querySelector('meta[name="_csrf"]')
                .getAttribute("content") + "="
            + document.querySelector('meta[name="_csrf_token"]')
                .getAttribute("content"));
        req.send();
    }

    function deletethread(event) {
        event.preventDefault();
        var thread = window.getLinkTag(event.target).parentElement.parentElement;
        var req = new XMLHttpRequest();
        req.onreadystatechange = function() {
            if (req.readyState !== XMLHttpRequest.DONE)
                return;
            switch (req.status) {
                case 200:
                    thread.parentElement.removeChild(thread);
                    return;
                case 403:
                    showError("You aren't allowed to delete threads!");
                    return;
                default:
                    showError("Unknown error has occured");
                    console.log(req.status + " " + req.responseText);
                    return;
            }
        };
        req.open("DELETE", "../../../" + pagecounter.baseurl + thread.id.substring(3)
            + "?" + document.querySelector('meta[name="_csrf"]')
                .getAttribute("content") + "="
            + document.querySelector('meta[name="_csrf_token"]')
                .getAttribute("content"));
        req.send();
    }

    function editthread(event) {
        event.preventDefault();
        var link = window.getLinkTag(event.target);
        var heading = null;
        for (var elem of link.parentElement.parentElement.children){
            if (elem.tagName.toUpperCase() === "H2"){
                heading = elem;
                break;
            }
        }
        var text = prompt("Enter new thread title", heading.innerText);
        console.log(text);
        if (!text || text.length < 1)
            return;
        var req = new XMLHttpRequest();
        req.onreadystatechange = function() {
            if (req.readyState !== XMLHttpRequest.DONE)
                return;
            switch (req.status) {
                case 200:
                    heading.innerText = text;
                    return;
                case 403:
                    showError("You aren't allowed to edit thread titles!");
                    return;
                default:
                    showError("Unknown error has occured");
                    console.log(req.status + " " + req.responseText);
                    return;
            }
            heading.innerText = text;
        };
        req.open("PATCH", "../../../" + pagecounter.baseurl
            + "../thread/" + heading.parentElement.id.substring(3)
            + "?title=" + text
            + "&" + document.querySelector('meta[name="_csrf"]')
                .getAttribute("content") + "="
            + document.querySelector('meta[name="_csrf_token"]')
                .getAttribute("content"));
        req.send();
    }

    /* Registered to window so that pages.js can call this after adding buttons. */
    window.buttoncheck = function() {
        var qbtn = document.getElementsByClassName("btn-quote");
        for (var i = 0; i < qbtn.length; i++) {
            qbtn[i].onclick = quoteMessage;
        }
        var dbtn = document.getElementsByClassName("btn-delete");
        for (var i = 0; i < dbtn.length; i++) {
            dbtn[i].onclick = deleteMessage;
        }
    };

    var btn = document.getElementById("btn-edit-thread");
    if (btn)
        btn.onclick = editthread;
    btn = document.getElementById("btn-delete-thread");
    if (btn)
        btn.onclick = deletethread;

    buttoncheck();
    timecheck();
})(window);


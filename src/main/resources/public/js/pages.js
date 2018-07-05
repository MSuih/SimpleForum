"use strict";

(function(window){
    var pagecounter = window.globalVariableHolder;
    if (!pagecounter)
        console.log("No global object!");

    /* Updates the page switcher buttons displayed on bottom of the screen. */
    function updatePageSelection() {
        if (pagecounter.currentpage < 3)
            moreBefore.classList.add("disabled");
        else if (pagecounter.currentpage >= 3)
            moreBefore.classList.remove("disabled");
        if (pagecounter.currentpage + 3 < pagecounter.totalpages)
            moreAfter.classList.remove("disabled");
        else if (pagecounter.currentpage + 3 >= pagecounter.totalpages)
            moreAfter.classList.add("disabled");
        for (var i = 0; i < 5; i++) {
            var button = elements[i + 2];
            var pagenum = pagecounter.currentpage - 2 + i;
            if (pagenum < 0 || pagenum >= pagecounter.totalpages)
                button.classList.add("disabled");
            else
                button.classList.remove("disabled");
            button.innerText = pagenum + 1;
            button.href = pagecounter.baseurl + pagecounter.baseid + "/page/" + (pagenum + 1);
        }
    }

    function buildMessageElement(msg){
        var element = document.createElement("div");
        element.appendChild(buildIconContainer());
        element.id = "msg" + msg.id;
        var link = document.createElement("a");
        link.href = pagecounter.redirecturl + msg.id;
        link.innerText = new Date(msg.timePosted).toISOString();
        link.classList.add("timeunit");
        var text = document.createElement("p");
        text.innerHTML = msg.content;
        element.appendChild(link);
        element.appendChild(text);
        return element;
    }

    function buildIconContainer() {
        var icons = document.createElement("div");
        icons.classList.add("iconcontainer");
        var quotelink = document.createElement("a");
        quotelink.href = "#";
        quotelink.classList.add("btn-quote");
        var quoteimg = document.createElement("img");
        quoteimg.src = pagecounter.baseurl + "../img/icon-quote.svg";;
        quoteimg.alt = "Quote";
        quoteimg.classList.add("icon");
        quotelink.appendChild(quoteimg);
        var deletelink = document.createElement("a");
        var deleteimg = document.createElement("img");
        deleteimg.src = pagecounter.baseurl + "../img/icon-delete.svg";;
        deleteimg.alt = "Delete";
        deleteimg.classList.add("icon");
        deletelink.appendChild(deleteimg);
        deletelink.href = "#";
        deletelink.classList.add("btn-delete");
        icons.appendChild(quotelink);
        icons.appendChild(deletelink);
        return icons;
    }

    function buildThreadElement(thread) {
        var element = document.createElement("div");
        var link = document.createElement("a");
        link.href = pagecounter.redirecturl + thread.id;
        if (thread.stickied) {
            var icon = document.createElement("img");
            icon.src = pagecounter.baseurl + "../img/icon-sticky.svg";
            icon.classList.add("icon");
            link.appendChild(icon);
        }
        var title = document.createElement("span");
        title.id = "thr" + thread.id;
        title.innerHTML = thread.title;
        link.appendChild(title);
        element.appendChild(link);
        var note = document.createElement("span");
        note.innerText = "Last modified: ";
        note.classList.add("note");
        element.appendChild(note);
        var time = document.createElement("span");
        time.classList.add("timeunit");
        time.innerText = new Date(thread.lastUpdate).toISOString();
        note.appendChild(time);
        return element;
    }

    /* Replaces the current messages or threads with new ones. */
    function replaceCurrentItems(items, createState) {
        var section = document.getElementsByTagName("SECTION")[0];
        for (var i = section.children.length - 1; i >= 0; i--) {
            var child = section.children[i];
            if (child.tagName.toLowerCase() === "div")
                section.removeChild(child);
        }
        if (!items[0]) {
            // No items -> no need to act
        } else if (items[0].timePosted) {
            //These are messages
            for (var item of items) {
                var div = buildMessageElement(item);
                section.appendChild(div);
            }
        } else {
            //these are threads
            for (var item of items) {
                var div = buildThreadElement(item);
                section.appendChild(div);
            }
        }
        window.buttoncheck();
        window.timecheck();
        if (createState)
            window.history.pushState({page: pagecounter.currentpage, items: items}, "", pagecounter.currentpage + 1);
    }

    /* Retrieves new messages or threads and adds them to page*/
    function getContentForPage() {
        var req = new XMLHttpRequest();
        req.onreadystatechange = function(){
            if (req.readyState !== XMLHttpRequest.DONE)
                return;
            if (req.status === 200) {
                var response = JSON.parse(req.responseText);
                replaceCurrentItems(response, true);
            } else {
                console.log("Unexpected status for request! " + req.status);
            }

        };
        if (pagecounter.baseurl.includes("topic"))
            req.open("GET", pagecounter.baseurl + pagecounter.baseid + "/threadsfor/" + (pagecounter.currentpage + 1));
        else
            req.open("GET", pagecounter.baseurl + pagecounter.baseid + "/messagesfor/" + (pagecounter.currentpage + 1));
        req.send();
    }

    function pageChanger(amount) {
        return function(event){
            event.preventDefault();
            if (amount === 0)
                return;
            var nextPage = pagecounter.currentpage + amount;
            nextPage = Math.min(nextPage, pagecounter.totalpages - 1);
            nextPage = Math.max(nextPage, 0);
            if (nextPage === pagecounter.currentpage) {
                console.log("Tried to move to same index! " + nextPage + pagecounter.currentpage);
                return;
            }
            pagecounter.currentpage = nextPage;
            updatePageSelection();
            getContentForPage();
        };
    }

    var container = document.getElementById("pageselect");
    var elements = container.children;
    var moreBefore = elements[1];
    var moreAfter = elements[7];
    //registers a click handler for page number buttons [2 to 7]
    for (var i = 0; i < 5; i++) {
        elements[i + 2].onclick = pageChanger(i - 2);
    }
    // Register a handler for back & foward buttons
    window.onpopstate = function(event){
        pagecounter.currentpage = event.state.page;
        updatePageSelection();
        replaceCurrentItems(event.state.items, false);
    };
})(window);

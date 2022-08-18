const text = document.querySelector('.heading-1');
const image = document.querySelector('.image');

const showWorm = () => {
    [text, image].forEach(el => el.classList.toggle('display-none'));

    // setTimeout(function() {
    //     [text, image].forEach(el => el.classList.toggle('display-none'));
    // }, 1000);
}

const hideWorm = () => {
    [text, image].forEach(el => el.classList.toggle('display-none'));
}


function startEventWoomb() {
    const eventSource = new EventSource('/emitter');
    eventSource.onmessage = (e) => {
        console.log("got: " + e.data);
        if (e.data === "show_worm_now") {
            showWorm();
        } else if (e.data === "no_show_it") {
            hideWorm();
        }
    };
    eventSource.onopen = (e) => {
        console.log("Start of EventSource");
    };
    eventSource.onerror = (e) => {
        console.log("EventSource error!");
        startEventWoomb();
    };
}

startEventWoomb();

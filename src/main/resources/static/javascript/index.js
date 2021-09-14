
const showWorm = () => {
    const text = document.querySelector('.heading-1');
    const image = document.querySelector('.image');

    [text, image].forEach(el => el.classList.toggle('display-none'));

    setTimeout(function() {
        [text, image].forEach(el => el.classList.toggle('display-none'));
    }, 1000);
}


const eventSource = new EventSource('http://localhost:8080/emitter');
eventSource.onmessage = (e) => {
    if (e.data === "showWorm") {
        showWorm();
    }
}

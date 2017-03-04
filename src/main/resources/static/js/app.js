var stompClient = null;
var view = null;

function init(spec, uuid){
    // init vega
    vg.parse.spec(spec, function(error, chart) {

        view = chart({el:"#container", renderer: "svg"});
        view.update();
    });
    // connect ws
    connect(uuid);
}
// TODO: Handle connection errors
function connect(uuid) {
    var socket = new SockJS('/data');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/' + uuid, function (data) {
            // recived data: {command: "MESSAGE", headers: Object, body: "Foo 13"}
            if(view != null){
                var jsonData = JSON.parse(data.body);
                // See http://bl.ocks.org/boeric/f96c09b03918c9277cd5
                // clear chart and insert new data
                view.data("table").remove(function(d) { return true }).insert(jsonData);
                // update chart
                view.update();

            }

        });
    });
}
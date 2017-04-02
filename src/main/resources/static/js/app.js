var stompClient = null;
var view = null;
var specification = null;

function init(spec, uuid){
    // init vega
    specification = spec;
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
                var dataToInsert = jsonData;

                if(dataToInsert['table']){
                    dataToInsert = dataToInsert['table'];
                    for(var i = 0; i < dataToInsert.length;i++){
                        var d = dataToInsert[i];
                        for (var f = 0; f < specification.dataSchema.fields.length; f++){
                            var field = specification.dataSchema.fields[f];
                            if(field['transformTo']){
                                d[field.field] = eval(d[field.field]);
                            }
                        }
                        dataToInsert[i] = d;
                    }
                }
                //Default/replaceAll
                if(specification == null) {
                    replaceAll(view, dataToInsert);
                } else {
                    //  Handle modes
                    var handling = specification.dataSchema.handling;
                    switch(handling.mode){
                        case'reloadAll':
                            replaceAll(view, dataToInsert);
                            break;
                        case'lifo':
                            lifo(view, dataToInsert);
                        default:
                            replaceAll(view, dataToInsert);
                    }
                }
                // update chart
                view.update();

            }

        });
    });
}

function replaceAll(view, dataToInsert) {
    view.data("table").remove(function (d) {
        return true
    }).insert(dataToInsert);
}
//TODO:fix this and the visualisation
function lifo(view, dataToInsert) {
    //new data in
    view.data("table").insert(dataToInsert);
}

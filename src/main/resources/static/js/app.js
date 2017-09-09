var stompClient = null;
var specification = null;
var buffer = null;
var bufferSize = 1;
var connected = false;
function init(spec, uuid){
    // init vega
    specification = spec;
    vg.parse.spec(spec, function(error, chart) {
        var view = chart({el:"#container", renderer: "svg"});
        view.update();

        var handling = specification.dataSchema.handling;
        switch(handling.mode){
            case'reloadAll':
                break;
            case'lifo':
                bufferSize = specification.dataSchema.handling.size;
                buffer = new Array(bufferSize);
                break;
        }
        // connect ws
        connect(uuid, view);
    });
}

function connect(uuid, view) {

    var socket = new SockJS('/data');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        if(!connected) {
            connected = true;
            stompClient.subscribe('/topic/' + uuid, function (data) {



                // recived data: {command: "MESSAGE", headers: Object, body: "Foo 13"}
                if (view) {
                    var jsonData = JSON.parse(data.body);
                    // See http://bl.ocks.org/boeric/f96c09b03918c9277cd5
                    // clear chart and insert new data
                    var dataToInsert = [];

                    if (jsonData['table']) {
                        jsonData = jsonData['table'];
                        for (var i = 0; i < jsonData.length; i++) {
                            var d = jsonData[i];
                            for (var f = 0; f < specification.dataSchema.fields.length; f++) {
                                var field = specification.dataSchema.fields[f];
                                if (field['transformTo']) {
                                    d[field.field] = eval(d[field.field]);
                                }
                            }
                            dataToInsert.push(d);
                        }
                    }

                    //Default/replaceAll

                    if (specification == null) {
                        replaceAll(view, dataToInsert);
                    } else {
                        //  Handle modes
                        var handling = specification.dataSchema.handling.mode;
                        console.log("Useing method " + handling);
                        switch (handling) {
                            case'reloadAll':
                                replaceAll(view, dataToInsert);
                                break;
                            case'lifo':
                                lifo(view, dataToInsert);
                                break;
                            default:
                                replaceAll(view, dataToInsert);
                        }
                    }

                    view.update({duration:50, ease:"quad-in"});
                }

            });
        }

    });

}

function replaceAll(view, dataToInsert) {
    view.data("table").remove(function (d) {
        return true
    }).insert(formatData(dataToInsert));
}

function lifo(view, dataToInsert) {
    dataToInsert = formatData(dataToInsert);
    if(specification.dataSchema.handling.animate != undefined && specification.dataSchema.handling.animate){

        if (buffer.length + dataToInsert.length > bufferSize) {
            buffer = buffer.slice(dataToInsert.length);
        }


        buffer.push.apply(buffer, dataToInsert);

        model = view.model();
        model.data("table").remove(function (d) {
            return !buffer.includes(d); // Funktioniert nicht im IE
        });

        view.update();
        model.data("table").insert(dataToInsert);

    } else {
        //new data in


        if (buffer.length + dataToInsert.length > bufferSize) {
            buffer = buffer.slice(dataToInsert.length);
        }


        buffer.push.apply(buffer, dataToInsert);

        model = view.model();
        model.data("table").insert(dataToInsert).remove(function (d) {
            return !buffer.includes(d); // Funktioniert nicht im IE
        });
    }
}

function formatData(dataToformat) {
    if (specification.dataSchema.handling.postProcess != undefined && specification.dataSchema.handling.postProcess) {
        var first = specification.data[0];
        if (first.format) {
            return window.dl.read(dataToformat, first.format);
        }
    }
    return dataToformat;
}

function alertError(title, msg) {
    alertInner(title, msg, 'error');
}

function alertWarning(title, msg) {
    alertInner(title, msg, 'warning');
}

function alertSuccess(title, msg) {
    alertInner(title, msg, 'success');
}

function alertInner(title, msg, mode) {
    $.gritter.add({
        title: title,
        text: '<p>' + msg + '</p>',
        class_name: 'gritter-' + mode + ' gritter-msg'
    });
}

function S4() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1); 
}

function newUuid() {
    return (S4() + S4() + S4() + "4" + S4().substr(0,3) + S4() + S4() + S4() + S4()).toLowerCase();
}

function cloneObj(obj) {
    var ret = {};
    for (var i in obj) {
        ret[i] = obj[i];
    }
    return ret;
}
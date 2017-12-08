var fs = require('fs');
var url = require('url');
var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var defaultCharset = 'utf8';

app.set('port', process.env.PORT || 8989);
app.use(express.static(__dirname + '/public'));
app.use(bodyParser.urlencoded({
    extended: true
}));


/* JapScan */
app.get('/japscan/mangas/', function(req, res){
    returnHtmlFile('japscan', 'manga-listing', res);
});

app.get('/japscan/mangas/:alias/', function(req, res){
    //req.params.alias
    returnHtmlFile('japscan', 'manga-detail', res);
});

app.get('/japscan/lecture-en-ligne/:mangaAlias/:chapterNumber', function(req, res){
    //req.params.alias
    returnHtmlFile('japscan', 'manga-reader', res);
});

app.get('/japscan/lecture-en-ligne/:mangaAlias/:chapterNumber/:page.html', function(req, res){
    //req.params.alias
    returnHtmlFile('japscan', 'manga-reader', res);
});

app.get('/japscan/cr-images/:mangaAlias/:chapterNumber/:page.jpg', function(req, res){
    //req.params.alias
    returnImageFile(res);
});

/* MangaPanda */

app.get('/mangapanda/alphabetical', function(req, res){
    returnHtmlFile('mangapanda', 'manga-listing', res);
});

app.get('/mangapanda/:alias', function(req, res){
    returnHtmlFile('mangapanda', 'manga-detail', res);
});

app.get('/mangapanda/:manga/:chapter', function(req, res){
    returnHtmlFile('mangapanda', 'manga-reader', res);
});

app.get('/mangapanda/:manga/:chapter/:page', function(req, res){
    returnHtmlFile('mangapanda', 'manga-reader', res);
});

function returnHtmlFile(provider, fileName, res) {
    res.type('text/html; charset=' + defaultCharset);
    fs.readFile('data/' + provider + "/" + fileName + '.html', defaultCharset, function (err, data) {
        if (err) {
            res.status(500);
        }
        res.status(200).send(data);
    });
}


function returnImageFile(res) {
    res.type('image/jpeg');
    fs.readFile('data/page.jpg', function (err, data) {
        if (err) {
            res.status(500);
        }
        res.status(200).send(data);
    });
}


app.listen(app.get('port'), function () {
    console.log('Express server listening on port ' + app.get('port'));
});
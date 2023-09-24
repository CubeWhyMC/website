var express = require('express');
var router = express.Router();

router.get('/', function (req, res, next) {
    res.render('cape', {api: process.env["API-BACKEND"] || "http://127.0.0.1:8080"});
});

module.exports = router;
